package com.yongbi.szsyongbi.refund.application.port.out;

import com.yongbi.szsyongbi.refund.domain.Refund;

public interface SaveRefundPort {
    boolean save(Refund refund);

    class FakeSaveRefundPort implements SaveRefundPort {

        @Override
        public boolean save(Refund refund) {
            if (refund.memberId() == 5L) {
                throw new RuntimeException("처리 중 오류가 발생했습니다.");
            }

            return true;
        }
    }
}
