package com.yongbi.szsyongbi.refund.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Refund(Long id, Long memberId, Integer year, BigDecimal refund, LocalDateTime createdAt) {
    public Refund(Long memberId, Integer year, BigDecimal refund) {
        this(null, memberId, year, refund, LocalDateTime.now());
    }
}
