package com.yongbi.szsyongbi.refund.application.port.out;

import com.yongbi.szsyongbi.refund.domain.Refund;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReadRefundPort {
    Optional<Refund> read(long memberId, int year);

    class FakeReadRefundPort implements ReadRefundPort {
        private final Map<Long, Refund> map;

        public FakeReadRefundPort(Map<Long, Refund> map) {
            this.map = map;
        }

        public FakeReadRefundPort() {
            this.map = createFakeRefund();
        }

        private Map<Long, Refund> createFakeRefund() {
            final var map = new HashMap<Long, Refund>();
            final var fakeRefunds = generateFakeRefunds();

            for (final var r : fakeRefunds) {
                map.put(r.id(), r);
            }

            return map;
        }

        private List<Refund> generateFakeRefunds() {
            return List.of(
                    new Refund( // 직전년도 결정세액이 있어도 문제없이 결정세액 계산이 되는 것을 테스트
                            1L,
                            3L,
                            2023,
                            new BigDecimal("759999"),
                            LocalDateTime.now()
                    )
            );
        }

        @Override
        public Optional<Refund> read(long memberId, int year) {
            return map.values().stream()
                    .filter(r -> memberId == r.memberId() && year == r.year())
                    .findFirst();
        }
    }
}
