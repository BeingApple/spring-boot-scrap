package com.yongbi.szsyongbi.refund.adapter.out.persistence;

import com.yongbi.szsyongbi.refund.domain.Refund;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "REFUND", indexes = {
        @Index(name = "idx_refund_year", columnList = "refund_year")
})
public class RefundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(name = "refund_year", nullable = false)
    private Integer year;

    @Column(nullable = false, precision = 64, scale = 3)
    private BigDecimal refund;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected RefundEntity() {}

    public RefundEntity(Long id, Long memberId, Integer year, BigDecimal refund, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.year = year;
        this.refund = refund;
        this.createdAt = createdAt;
    }

    public RefundEntity(Refund refund) {
        this.id = refund.id();
        this.memberId = refund.memberId();
        this.year = refund.year();
        this.refund = refund.refund();
        this.createdAt = refund.createdAt();
    }

    public Refund domain() {
        return new Refund(this.id, this.memberId, this.year, this.refund, this.createdAt);
    }
}
