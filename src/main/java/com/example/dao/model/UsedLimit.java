package com.example.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "used_limits")
public class UsedLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "limit_date", nullable = false)
    private LocalDate limitDate;

    @NotNull
    @PositiveOrZero
    @Column(name = "daily_limit", nullable = false)
    private Long dailyLimit;

    @NotNull
    @PositiveOrZero
    @Column(name = "current_limit", nullable = false)
    private Long currentLimit;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ?
                hibernateProxy.getHibernateLazyInitializer().getPersistentClass() :
                o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ?
                hibernateProxy.getHibernateLazyInitializer().getPersistentClass() :
                this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UsedLimit that = (UsedLimit) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ?
                hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() :
                getClass().hashCode();
    }
}
