package com.example.scheduler;

import com.example.service.LimitService;
import com.example.service.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;

import static java.util.Objects.nonNull;
import static org.springframework.core.NestedExceptionUtils.getRootCause;


/**
 * Производит очистку старых данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@ConditionalOnProperty(value = "app.housekeeping.enabled", havingValue = "true")
public class HousekeepingScheduler {

    private final TimeService timeService;
    private final LimitService limitService;

    @Value("${app.housekeeping.fetch-records-at-once}")
    private int fetchRecordsAtOnce;
    @Value("${app.housekeeping.housekeeping-max-duration}")
    private Duration housekeepingDuration;

    @Scheduled(cron = "${app.housekeeping.cron}")
    public void scheduledHousekeeping() {
        log.info("Housekeeping scheduler started");
        try {
            LocalDate currentDate = timeService.getCurrentDate();
            long expiredTime = System.currentTimeMillis() + housekeepingDuration.toMillis();
            while (System.currentTimeMillis() < expiredTime) {
                try {
                    int processedCount = limitService.deleteOldLimits(currentDate, fetchRecordsAtOnce);
                    if (processedCount == 0) break;
                } catch (Exception e) {
                    Throwable cause = getRootCause(e);
                    String rootCauseString = nonNull(cause) ? cause.getMessage() : e.getClass().getName();
                    log.error("Housekeeping failed. Reason: {} Cause: {}", e.getMessage(), rootCauseString);
                    break;
                }
            }
        } finally {
            log.info("Housekeeping scheduler finished");
        }
    }
}
