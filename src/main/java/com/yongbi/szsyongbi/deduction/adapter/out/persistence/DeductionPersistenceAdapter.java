package com.yongbi.szsyongbi.deduction.adapter.out.persistence;

import com.yongbi.szsyongbi.deduction.application.port.out.DeleteDeductionPort;
import com.yongbi.szsyongbi.deduction.application.port.out.ReadDeductionPort;
import com.yongbi.szsyongbi.deduction.application.port.out.SaveDeductionPort;
import com.yongbi.szsyongbi.deduction.domain.Deduction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class DeductionPersistenceAdapter implements SaveDeductionPort, ReadDeductionPort, DeleteDeductionPort {
    private final DeductionRepository repository;

    public DeductionPersistenceAdapter(DeductionRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean save(Deduction deduction) {
        try {
            final var entity = new DeductionEntity(deduction);
            repository.save(entity);

            return true;
        } catch (RuntimeException ex) {
            log.error("Error on Saving Deduction : " + deduction, ex);
            throw new RuntimeException("처리 중 오류가 발생했습니다.");
        }
    }

    @Override
    public boolean saveAll(Collection<Deduction> deductions) {
        try {
            final var entities = deductions.stream().map(DeductionEntity::new).toList();
            repository.saveAll(entities);

            return true;
        } catch (RuntimeException ex) {
            log.error("Error on Saving Deductions : " + deductions, ex);
            throw new RuntimeException("처리 중 오류가 발생했습니다.");
        }
    }

    @Override
    public boolean delete(long memberId, int year) {
        try {
            repository.deleteByMemberIdAndYear(memberId, year);

            return true;
        } catch (RuntimeException ex) {
            log.error("Error on Deleting Deduction / memberId : " + memberId + ", year: " + year, ex);
            throw new RuntimeException("처리 중 오류가 발생했습니다.");
        }
    }

    @Override
    public List<Deduction> read(long memberId, int year) {
        final var list = repository.findByMemberIdAndYear(memberId, year);
        return list.stream().map(DeductionEntity::domain).toList();
    }
}
