package com.yongbi.szsyongbi.refund.adapter.out.persistence;

import com.yongbi.szsyongbi.refund.application.port.out.DeleteRefundPort;
import com.yongbi.szsyongbi.refund.application.port.out.ReadRefundPort;
import com.yongbi.szsyongbi.refund.application.port.out.SaveRefundPort;
import com.yongbi.szsyongbi.refund.domain.Refund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class RefundPersistenceAdapter implements SaveRefundPort, ReadRefundPort, DeleteRefundPort {
    private final RefundRepository repository;

    public RefundPersistenceAdapter(RefundRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean save(Refund refund) {
        try {
            final var entity = new RefundEntity(refund);
            repository.save(entity);

            return true;
        } catch (RuntimeException ex) {
            log.error("Error on Saving Income : " + refund, ex);
            throw new RuntimeException("처리 중 오류가 발생했습니다.");
        }
    }

    @Override
    public boolean delete(long memberId, int year) {
        try {
            repository.deleteByMemberIdAndYear(memberId, year);

            return true;
        } catch (RuntimeException ex) {
            log.error("Error on Deleting Income / memberId : " + memberId + ", year: " + year, ex);
            throw new RuntimeException("처리 중 오류가 발생했습니다.");
        }
    }

    @Override
    public Optional<Refund> read(long memberId, int year) {
        final var maybeRefund = repository.findByMemberIdAndYear(memberId, year).stream().findFirst();
        return maybeRefund.map(RefundEntity::domain);
    }
}
