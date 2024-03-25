package com.yongbi.szsyongbi.refund.application.service;

import com.yongbi.szsyongbi.deduction.application.port.out.ReadDeductionPort;
import com.yongbi.szsyongbi.income.application.port.out.ReadIncomePort;
import com.yongbi.szsyongbi.income.domain.Income;
import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.refund.application.port.in.CalculateRefundCommand;
import com.yongbi.szsyongbi.refund.application.port.in.CalculateRefundResponse;
import com.yongbi.szsyongbi.refund.application.port.in.CalculateRefundUseCase;
import com.yongbi.szsyongbi.refund.application.port.out.DeleteRefundPort;
import com.yongbi.szsyongbi.refund.application.port.out.ReadRefundPort;
import com.yongbi.szsyongbi.refund.application.port.out.SaveRefundPort;
import com.yongbi.szsyongbi.refund.domain.CalculateRefund;
import com.yongbi.szsyongbi.refund.domain.Refund;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CalculateRefundService implements CalculateRefundUseCase {
    private final ReadMemberPort readMemberPort;
    private final ReadRefundPort readRefundPort;
    private final SaveRefundPort saveRefundPort;
    private final DeleteRefundPort deleteRefundPort;
    private final ReadIncomePort readIncomePort;
    private final ReadDeductionPort readDeductionPort;

    public CalculateRefundService(ReadMemberPort readMemberPort, ReadRefundPort readRefundPort, SaveRefundPort saveRefundPort, DeleteRefundPort deleteRefundPort, ReadIncomePort readIncomePort, ReadDeductionPort readDeductionPort) {
        this.readMemberPort = readMemberPort;
        this.readRefundPort = readRefundPort;
        this.saveRefundPort = saveRefundPort;
        this.deleteRefundPort = deleteRefundPort;
        this.readIncomePort = readIncomePort;
        this.readDeductionPort = readDeductionPort;
    }

    @Override
    @Transactional
    public CalculateRefundResponse calculate(CalculateRefundCommand command) {
        final var beforeYear = LocalDate.now().minusYears(1).getYear();
        final var member = readMemberPort.read(command.id()).orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 회원입니다."));

        // 기저장된 결정세액 정보가 있으면 삭제
        final var refundData = readRefundPort.read(member.id(), beforeYear);
        if (refundData.isPresent()) {
            deleteRefundPort.delete(member.id(), beforeYear);
        }

        // 결정 세액 계산 진행
        // 종합소득금액 불러오기
        final var totalIncome = readIncomePort.read(member.id(), beforeYear)
                .map(Income::income).orElseThrow(() -> new IllegalArgumentException("직전년도 종합소득이 존재하지 않습니다."));

        // 공제액 불러오기
        final var deductions = readDeductionPort.read(member.id(), beforeYear);

        // 계산
        final var calculator = new CalculateRefund(totalIncome, deductions);
        final var finalizedTax = calculator.finalizedTax();

        // 저장
        final var refund = new Refund(member.id(), beforeYear, finalizedTax);
        saveRefundPort.save(refund);

        return new CalculateRefundResponse(finalizedTax);
    }
}
