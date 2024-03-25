package com.yongbi.szsyongbi.income.adapter.out.persistence;

import com.yongbi.szsyongbi.income.application.port.out.DeleteIncomePort;
import com.yongbi.szsyongbi.income.application.port.out.ReadIncomePort;
import com.yongbi.szsyongbi.income.application.port.out.SaveIncomePort;
import com.yongbi.szsyongbi.income.domain.Income;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class IncomePersistenceAdapter implements SaveIncomePort, ReadIncomePort, DeleteIncomePort {
    private final IncomeRepository repository;

    public IncomePersistenceAdapter(IncomeRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean save(Income income) {
        try {
            final var entity = new IncomeEntity(income);
            repository.save(entity);

            return true;
        } catch (RuntimeException ex) {
            log.error("Error on Saving Income : " + income, ex);
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
    public Optional<Income> read(long memberId, int year) {
        final var maybeIncome = repository.findByMemberIdAndYear(memberId, year).stream().findFirst();
        return maybeIncome.map(IncomeEntity::domain);
    }
}
