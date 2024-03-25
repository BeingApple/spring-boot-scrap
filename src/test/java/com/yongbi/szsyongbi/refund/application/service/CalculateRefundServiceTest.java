package com.yongbi.szsyongbi.refund.application.service;

import com.yongbi.szsyongbi.deduction.application.port.out.ReadDeductionPort;
import com.yongbi.szsyongbi.income.application.port.out.ReadIncomePort;
import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.refund.application.port.in.CalculateRefundCommand;
import com.yongbi.szsyongbi.refund.application.port.out.DeleteRefundPort;
import com.yongbi.szsyongbi.refund.application.port.out.ReadRefundPort;
import com.yongbi.szsyongbi.refund.application.port.out.SaveRefundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculateRefundServiceTest {
    private CalculateRefundCommand calculateRefundCommand() {
        return new CalculateRefundCommand(2L);
    }

    @Test
    @DisplayName("저장된 소득내역이 있는 상태에서 결정세액 계산을 요청하면, 예외 발생 없이 마무리 되어야 합니다.")
    void successCalculateRefundTest() {
        final var service = new CalculateRefundService(
                new ReadMemberPort.FakeReadMemberPort(),
                new ReadRefundPort.FakeReadRefundPort(),
                new SaveRefundPort.FakeSaveRefundPort(),
                new DeleteRefundPort.FakeDeleteRefundPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new ReadDeductionPort.FakeReadDeductionPort()
        );

        assertDoesNotThrow(() -> service.calculate(calculateRefundCommand()));
    }

    private CalculateRefundCommand unavailableIncomeCalculateRefundCommand() {
        return new CalculateRefundCommand(4L);
    }

    @Test
    @DisplayName("저장된 소득내역이 없는 상태에서 결정세액 계산을 요청하면, 예외가 발생되어야 합니다.")
    void unavailableIncomeCalculateRefundTest() {
        final var service = new CalculateRefundService(
                new ReadMemberPort.FakeReadMemberPort(),
                new ReadRefundPort.FakeReadRefundPort(),
                new SaveRefundPort.FakeSaveRefundPort(),
                new DeleteRefundPort.FakeDeleteRefundPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new ReadDeductionPort.FakeReadDeductionPort()
        );

        final var message = assertThrows(IllegalArgumentException.class,
                () -> service.calculate(unavailableIncomeCalculateRefundCommand())).getMessage();

        assertThat(message).contains("직전년도 종합소득이 존재하지 않습니다.");
    }

    private CalculateRefundCommand unavailableMemberCalculateRefundCommand() {
        return new CalculateRefundCommand(250L);
    }

    @Test
    @DisplayName("회원 정보가 정상적으로 조회되지 않는 경우, 예외가 발생되어야 합니다.")
    void unavailableMemberCalculateRefundTest() {
        final var service = new CalculateRefundService(
                new ReadMemberPort.FakeReadMemberPort(),
                new ReadRefundPort.FakeReadRefundPort(),
                new SaveRefundPort.FakeSaveRefundPort(),
                new DeleteRefundPort.FakeDeleteRefundPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new ReadDeductionPort.FakeReadDeductionPort()
        );

       final var message = assertThrows(IllegalArgumentException.class,
                () -> service.calculate(unavailableMemberCalculateRefundCommand())).getMessage();

       assertThat(message).contains("찾을 수 없는 회원입니다.");
    }

    private CalculateRefundCommand existRefundHistoryCalculateRefundCommand() {
        return new CalculateRefundCommand(3L);
    }

    @Test
    @DisplayName("이미 계산된 결정세액 내역이 있어도, 예외 발생 없이 마무리 되어야 합니다.")
    void existRefundHistoryCalculateRefundTest() {
        final var service = new CalculateRefundService(
                new ReadMemberPort.FakeReadMemberPort(),
                new ReadRefundPort.FakeReadRefundPort(),
                new SaveRefundPort.FakeSaveRefundPort(),
                new DeleteRefundPort.FakeDeleteRefundPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new ReadDeductionPort.FakeReadDeductionPort()
        );

        assertDoesNotThrow(() -> service.calculate(existRefundHistoryCalculateRefundCommand()));
    }

    private CalculateRefundCommand saveErrorCalculateRefundCommand() {
        return new CalculateRefundCommand(5L);
    }

    @Test
    @DisplayName("결정세액 저장 과정에서 에러가 발생한 경우, 예외가 발생되어야 합니다.")
    void saveErrorCalculateRefundTest() {
        final var service = new CalculateRefundService(
                new ReadMemberPort.FakeReadMemberPort(),
                new ReadRefundPort.FakeReadRefundPort(),
                new SaveRefundPort.FakeSaveRefundPort(),
                new DeleteRefundPort.FakeDeleteRefundPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new ReadDeductionPort.FakeReadDeductionPort()
        );

        final var message = assertThrows(RuntimeException.class,
                () -> service.calculate(saveErrorCalculateRefundCommand())).getMessage();

        assertThat(message).contains("처리 중 오류가 발생했습니다.");
    }
}
