package com.yongbi.szsyongbi.deduction.domain;

import java.math.BigDecimal;
import java.util.List;

public class TotalDeduction {
    private final BigDecimal total;
    private final BigDecimal taxDeduction;

    public TotalDeduction(List<Deduction> deductions) {
        var total = BigDecimal.ZERO;
        var taxDeduction = BigDecimal.ZERO;
        for (var deduction : deductions) {
            total = total.add(deduction.deduction());

            if (DeductionType.TAX.equals(deduction.type())) {
                taxDeduction = deduction.deduction();
            }
        }

        this.total = total;
        this.taxDeduction = taxDeduction;
    }

    public BigDecimal total() {
        return total;
    }

    public BigDecimal taxDeduction() {
        return taxDeduction;
    }
}
