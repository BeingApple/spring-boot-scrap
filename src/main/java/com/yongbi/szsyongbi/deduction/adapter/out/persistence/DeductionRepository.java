package com.yongbi.szsyongbi.deduction.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DeductionRepository extends JpaRepository<DeductionEntity, Long> {
    Collection<DeductionEntity> findByMemberIdAndYear(long memberId, int year);
    void deleteByMemberIdAndYear(long memberId, int year);
}
