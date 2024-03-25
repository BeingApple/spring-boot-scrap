package com.yongbi.szsyongbi.refund.application.port.out;

public interface DeleteRefundPort {
    boolean delete(long memberId, int year);

    class FakeDeleteRefundPort implements DeleteRefundPort {

        @Override
        public boolean delete(long memberId, int year) {
            return true;
        }
    }
}
