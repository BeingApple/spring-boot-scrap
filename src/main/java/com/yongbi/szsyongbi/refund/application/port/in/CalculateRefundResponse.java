package com.yongbi.szsyongbi.refund.application.port.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public record CalculateRefundResponse(
        boolean result,
        String message,
        @JsonProperty(value = "결정세액")
        String finalizedTax
) {
    public CalculateRefundResponse(BigDecimal finalizedTax) {
        this(true, "결정세액 산출에 성공했습니다.", NumberFormat.getNumberInstance(Locale.KOREA).format(finalizedTax));
    }

    public static CalculateRefundResponse fail(String message) {
        return new CalculateRefundResponse(false, message, "");
    }
}
