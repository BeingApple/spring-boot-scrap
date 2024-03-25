package com.yongbi.szsyongbi.income.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {
    Collection<IncomeEntity> findByMemberIdAndYear(long memberId, int year);
    void deleteByMemberIdAndYear(long memberId, int year);
}
