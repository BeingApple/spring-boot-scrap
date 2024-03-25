package com.yongbi.szsyongbi.income.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Income(Long id, Long memberId, Integer year, BigDecimal income, LocalDateTime createdAt) {
    public Income(Long memberId, Integer year, BigDecimal income) {
        this(null, memberId, year, income, LocalDateTime.now());
    }
}
