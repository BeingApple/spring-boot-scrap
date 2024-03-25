package com.yongbi.szsyongbi.refund.domain;

public enum BasicTaxRate {
    LEVEL1,
    LEVEL2,
    LEVEL3,
    LEVEL4,
    LEVEL5,
    LEVEL6,
    LEVEL7,
    LEVEL8,
    UNKNOWN;

    public static BasicTaxRate getTaxRate(double taxBase) {
        if (taxBase <= 14000000) {
            return LEVEL1;
        } else if (14000000 < taxBase && taxBase <= 50000000) {
            return LEVEL2;
        } else if (50000000 < taxBase && taxBase <= 88000000) {
            return LEVEL3;
        } else if (88000000 < taxBase && taxBase <= 150000000) {
            return LEVEL4;
        } else if (150000000 < taxBase && taxBase <= 300000000) {
            return LEVEL5;
        } else if (300000000 < taxBase && taxBase <= 500000000) {
            return LEVEL6;
        } else if (500000000 < taxBase && taxBase <= 1000000000) {
            return LEVEL7;
        } else if (1000000000 < taxBase) {
            return LEVEL8;
        }

        return UNKNOWN;
    }
}
