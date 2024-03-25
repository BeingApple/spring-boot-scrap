package com.yongbi.szsyongbi.refund.application.port.in;

public interface CalculateRefundUseCase {
    CalculateRefundResponse calculate(CalculateRefundCommand command);
}
