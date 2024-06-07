package com.example.dao.repository;

import com.example.dao.model.UsedLimit;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsedLimitRepository extends CrudRepository<UsedLimit, Long> {

    @Query("""
            select ul
            from UsedLimit ul
            where ul.userId = :userId and ul.limitDate = :date
            """)
    Optional<UsedLimit> findUsedLimitByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Modifying
    @Query(value = """
            INSERT INTO used_limits (user_id, daily_limit, limit_date, current_limit)
            VALUES (:userId, :dailyLimit, :limitDate, :dailyLimit)
            ON CONFLICT DO NOTHING
            """,
            nativeQuery = true)
    void saveWithoutConflict(
            @Param("userId") long userId,
            @Param("dailyLimit") long dailyLimit,
            @Param("limitDate") LocalDate limitDate
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = AvailableSettings.JAKARTA_LOCK_TIMEOUT, value = "-2")) // SKIP_LOCKED = "-2"
    @Query("select ul.id from UsedLimit  ul where ul.limitDate < :currentDate")
    List<Long> findAllOlderThat(@Param("currentDate") LocalDate currentDate, Pageable pageable);
}
