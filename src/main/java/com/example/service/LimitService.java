package com.example.service;

import com.example.dao.model.UsedLimit;
import com.example.dao.model.generated.CurrentLimitDto;
import com.example.dao.repository.UsedLimitRepository;
import com.example.exception.LimitExceeded;
import com.example.exception.UsedLimitNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LimitService {

    private final TimeService timeService;
    private final UsedLimitRepository usedLimitRepository;

    @Value("${app.user-default-limit}")
    private long userDefaultLimit;

    @Retryable(
            maxAttemptsExpression = "${app.limit-service.max-attempts}",
            backoff = @Backoff(delayExpression = "${app.limit-service.delay}"))
    @Transactional(
            timeoutString = "${app.limit-service.transaction-max-duration}",
            propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.REPEATABLE_READ
    )
    public CurrentLimitDto getLimit(Long userId) {
        LocalDate currentDate = timeService.getCurrentDate();
        usedLimitRepository.saveWithoutConflict(userId, userDefaultLimit, currentDate);
        UsedLimit usedLimit = usedLimitRepository
                .findUsedLimitByUserIdAndDate(userId, currentDate)
                .orElseThrow(() -> new IllegalStateException("Failed to process user with id %d".formatted(userId)));
        return new CurrentLimitDto(userId, currentDate, usedLimit.getCurrentLimit());
    }

    @Retryable(
            maxAttemptsExpression = "${app.limit-service.max-attempts}",
            backoff = @Backoff(delayExpression = "${app.limit-service.delay}"))
    @Transactional(
            timeoutString = "${app.limit-service.transaction-max-duration}",
            propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.REPEATABLE_READ
    )
    public void postLimit(Long userId, Long value) {
        LocalDate currentDate = timeService.getCurrentDate();
        usedLimitRepository.saveWithoutConflict(userId, userDefaultLimit, currentDate);
        UsedLimit usedLimit = usedLimitRepository
                .findUsedLimitByUserIdAndDate(userId, currentDate)
                .orElseThrow(() -> new IllegalStateException("Failed to process user with id %d".formatted(userId)));
        Long currentLimit = usedLimit.getCurrentLimit();
        if (currentLimit < value) {
            throw new LimitExceeded("Limit exceeded for user with id %d".formatted(userId));
        }
        usedLimit.setCurrentLimit(currentLimit - value);
        usedLimitRepository.save(usedLimit);
    }

    @Retryable(
            maxAttemptsExpression = "${app.limit-service.max-attempts}",
            backoff = @Backoff(delayExpression = "${app.limit-service.delay}"))
    @Transactional(
            timeoutString = "${app.limit-service.transaction-max-duration}",
            propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.REPEATABLE_READ
    )
    public void rollbackLimit(Long userId, Long value) {
        LocalDate currentDate = timeService.getCurrentDate();
        UsedLimit usedLimit = usedLimitRepository
                .findUsedLimitByUserIdAndDate(userId, currentDate)
                .orElseThrow(() -> new UsedLimitNotFound("Can not find limit to rollback on current date for user %d ".formatted(userId)));
        // Восстанавливаем лимит не больше максимального дневного значения
        Long currentLimit = (usedLimit.getCurrentLimit() + value) > usedLimit.getDailyLimit()
                ? usedLimit.getDailyLimit()
                : usedLimit.getCurrentLimit() + value;
        usedLimit.setCurrentLimit(currentLimit);
        usedLimitRepository.save(usedLimit);
    }

    @Retryable(
            maxAttemptsExpression = "${app.limit-service.max-attempts}",
            backoff = @Backoff(delayExpression = "${app.limit-service.delay}"))
    @Transactional(
            timeoutString = "${app.limit-service.transaction-max-duration}",
            propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.REPEATABLE_READ
    )
    public int deleteOldLimits(LocalDate currentDate, int fetchRecordsAtOnce) {
        List<Long> allOlderIds = usedLimitRepository.findAllOlderThat(currentDate, PageRequest.of(0, fetchRecordsAtOnce));
        if (allOlderIds.isEmpty()) return 0;
        usedLimitRepository.deleteAllById(allOlderIds);
        return allOlderIds.size();
    }
}
