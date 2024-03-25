package com.yongbi.szsyongbi.refund.domain;

import com.yongbi.szsyongbi.deduction.domain.Deduction;
import com.yongbi.szsyongbi.deduction.domain.TotalDeduction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CalculateRefund {
    private BigDecimal taxBase;
    private BigDecimal calculatedTax;
    private BigDecimal taxDeduction;
    private BigDecimal finalizedTax;
    private BasicTaxRate basicTaxRate;

    public CalculateRefund(BigDecimal totalIncome, List<Deduction> deductions) {
        this.calculateTaxBase(totalIncome, deductions);
        this.calculateTax();
        this.finalizeTax();
    }


    private void calculateTaxBase(BigDecimal totalIncome, List<Deduction> deductions) {
        final var deduction = new TotalDeduction(deductions);

        this.taxBase = totalIncome.subtract(deduction.total()).setScale(0, RoundingMode.HALF_EVEN);
        this.taxDeduction = deduction.taxDeduction();
        this.basicTaxRate = BasicTaxRate.getTaxRate(this.taxBase.doubleValue());
    }

    private BigDecimal calculateTax(String base, String overBase, String percent) {
        final var over = this.taxBase.subtract(new BigDecimal(overBase));
        return new BigDecimal(base).add(over.multiply(new BigDecimal(percent))).setScale(0, RoundingMode.HALF_EVEN);
    }

    private void calculateTax() {
        var calculatedTax = BigDecimal.ZERO;
        switch (this.basicTaxRate) {
            case LEVEL1 -> calculatedTax = this.taxBase.multiply(new BigDecimal("0.06")).setScale(0, RoundingMode.HALF_EVEN);
            case LEVEL2 -> calculatedTax = calculateTax("840000", "14000000", "0.15");
            case LEVEL3 -> calculatedTax = calculateTax("6240000", "50000000", "0.24");
            case LEVEL4 -> calculatedTax = calculateTax("15360000", "88000000", "0.35");
            case LEVEL5 -> calculatedTax = calculateTax("37060000", "150000000", "0.38");
            case LEVEL6 -> calculatedTax = calculateTax("94060000", "300000000", "0.4");
            case LEVEL7 -> calculatedTax = calculateTax("174060000", "500000000", "0.42");
            case LEVEL8 -> calculatedTax = calculateTax("384060000", "1000000000", "0.45");
            default -> throw new IllegalArgumentException("과세표준을 결정할 수 없습니다.");
        }

        this.calculatedTax = calculatedTax;
    }

    private void finalizeTax() {
        this.finalizedTax = this.calculatedTax.subtract(this.taxDeduction).setScale(0, RoundingMode.HALF_EVEN);
    }


    public BigDecimal finalizedTax() {
        return finalizedTax;
    }
}
