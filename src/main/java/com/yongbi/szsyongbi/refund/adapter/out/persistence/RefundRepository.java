package com.yongbi.szsyongbi.refund.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RefundRepository extends JpaRepository<RefundEntity, Long> {
    Collection<RefundEntity> findByMemberIdAndYear(long memberId, int year);
    void deleteByMemberIdAndYear(long memberId, int year);
}
