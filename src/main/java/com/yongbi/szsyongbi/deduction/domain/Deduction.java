package com.yongbi.szsyongbi.deduction.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Deduction(Long id, Long memberId, DeductionType type, Integer year, Integer month, BigDecimal deduction, LocalDateTime createdAt) {
    public Deduction(Long memberId, DeductionType type, Integer year, Integer month, BigDecimal deduction) {
        this(null, memberId, type, year, month, deduction, LocalDateTime.now());
    }

    public Deduction(Long memberId, DeductionType type, Integer year, BigDecimal deduction) {
        this(null, memberId, type, year, null, deduction, LocalDateTime.now());
    }
}
