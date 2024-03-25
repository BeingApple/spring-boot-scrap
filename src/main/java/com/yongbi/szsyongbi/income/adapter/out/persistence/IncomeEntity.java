package com.yongbi.szsyongbi.income.adapter.out.persistence;

import com.yongbi.szsyongbi.income.domain.Income;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "INCOME", indexes = {
        @Index(name = "idx_income_year", columnList = "income_year")
})
public class IncomeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(name = "income_year", nullable = false)
    private Integer year;

    @Column(nullable = false, precision = 64, scale = 3)
    private BigDecimal income;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected IncomeEntity() {}

    public IncomeEntity(Long id, Long memberId, Integer year, BigDecimal income, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.year = year;
        this.income = income;
        this.createdAt = createdAt;
    }

    public IncomeEntity(Income income) {
        this.id = income.id();
        this.memberId = income.memberId();
        this.year = income.year();
        this.income = income.income();
        this.createdAt = income.createdAt();
    }

    public Income domain() {
        return new Income(this.id, this.memberId, this.year, this.income, this.createdAt);
    }
}
