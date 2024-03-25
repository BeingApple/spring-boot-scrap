package com.yongbi.szsyongbi.deduction.adapter.out.persistence;

import com.yongbi.szsyongbi.deduction.domain.Deduction;
import com.yongbi.szsyongbi.deduction.domain.DeductionType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "DEDUCTION", indexes = {
        @Index(name = "idx_deduction_year_month", columnList = "deduction_year, deduction_month")
})
public class DeductionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeductionType type;

    @Column(name = "deduction_year", nullable = false)
    private Integer year;

    @Column(name = "deduction_month")
    private Integer month;

    @Column(nullable = false, precision = 64, scale = 3)
    private BigDecimal deduction;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected DeductionEntity() {}

    public DeductionEntity(Long id, Long memberId, DeductionType type, Integer year, Integer month, BigDecimal deduction, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.year = year;
        this.month = month;
        this.deduction = deduction;
        this.createdAt = createdAt;
    }

    public DeductionEntity(Deduction deduction) {
        this.id = deduction.id();
        this.memberId = deduction.memberId();
        this.type = deduction.type();
        this.year = deduction.year();
        this.month = deduction.month();
        this.deduction = deduction.deduction();
        this.createdAt = deduction.createdAt();
    }

    public Deduction domain() {
        return new Deduction(this.id, this.memberId, this.type, this.year, this.month, this.deduction, this.createdAt);
    }
}
