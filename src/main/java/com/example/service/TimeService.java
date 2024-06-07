package com.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Служба времени. Предназначена для получения текущего времени. Дает возможность фиксировать дату/время в тестах.
 */
@Service
@RequiredArgsConstructor
public class TimeService {

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    public LocalDate getCurrentDate() {
        return getCurrentTime().toLocalDate();
    }

    public boolean isTodayByDate(LocalDateTime checkDate) {
        return nonNull(checkDate) && getCurrentTime().toLocalDate().equals(checkDate.toLocalDate());
    }

    public boolean notExpiredByDate(LocalDateTime timestamp, Duration expirationPeriod) {
        if (isNull(timestamp) || isNull(expirationPeriod)) return true;
        LocalDate currentDate = getCurrentTime().minus(expirationPeriod).toLocalDate();
        LocalDate timestampLocalDate = timestamp.toLocalDate();
        return !currentDate.isAfter(timestampLocalDate);
    }
}
