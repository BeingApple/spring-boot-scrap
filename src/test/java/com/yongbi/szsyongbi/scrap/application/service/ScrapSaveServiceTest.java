package com.yongbi.szsyongbi.scrap.application.service;

import com.yongbi.szsyongbi.aes.application.service.AESService;
import com.yongbi.szsyongbi.deduction.application.port.out.DeleteDeductionPort;
import com.yongbi.szsyongbi.deduction.application.port.out.ReadDeductionPort;
import com.yongbi.szsyongbi.deduction.application.port.out.SaveDeductionPort;
import com.yongbi.szsyongbi.income.application.port.out.DeleteIncomePort;
import com.yongbi.szsyongbi.income.application.port.out.ReadIncomePort;
import com.yongbi.szsyongbi.income.application.port.out.SaveIncomePort;
import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.scrap.application.port.in.ScrapSaveCommand;
import com.yongbi.szsyongbi.scrap.application.port.out.RequestScrapPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ScrapSaveServiceTest {
    private ScrapSaveCommand scrapSaveCommand() {
        return new ScrapSaveCommand(1L);
    }

    @Test
    @DisplayName("스크래핑을 요청하면, 예외 발생 없이 마무리 되어야 합니다.")
    void successScrapAndSaveTest() {
        final var service = new ScrapSaveService(
                new ReadMemberPort.FakeReadMemberPort(),
                new RequestScrapPort.FakeRequestScrapPort(),
                new ReadDeductionPort.FakeReadDeductionPort(),
                new DeleteDeductionPort.FakeDeleteDeductionPort(),
                new SaveDeductionPort.FakeSaveDeductionPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new DeleteIncomePort.FakeDeleteIncomePort(),
                new SaveIncomePort.FakeSaveIncomePort(),
                new AESService("key")
        );

        assertDoesNotThrow(() -> service.scrapAndSave(scrapSaveCommand()));
    }

    private ScrapSaveCommand unavailableMemberScrapSaveCommand() {
        return new ScrapSaveCommand(250L);
    }

    @Test
    @DisplayName("회원 정보가 정상적으로 조회되지 않는 경우, 예외가 발생되어야 합니다.")
    void unavailableMemberScrapAndSaveTest() {
        final var service = new ScrapSaveService(
                new ReadMemberPort.FakeReadMemberPort(),
                new RequestScrapPort.FakeRequestScrapPort(),
                new ReadDeductionPort.FakeReadDeductionPort(),
                new DeleteDeductionPort.FakeDeleteDeductionPort(),
                new SaveDeductionPort.FakeSaveDeductionPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new DeleteIncomePort.FakeDeleteIncomePort(),
                new SaveIncomePort.FakeSaveIncomePort(),
                new AESService("key")
        );

       final var message = assertThrows(IllegalArgumentException.class,
                () -> service.scrapAndSave(unavailableMemberScrapSaveCommand())).getMessage();

       assertThat(message).contains("찾을 수 없는 회원입니다.");
    }

    private ScrapSaveCommand decryptErrorScrapSaveCommand() {
        return new ScrapSaveCommand(15L);
    }

    @Test
    @DisplayName("회원 정보의 암호화된 주민등록번호 복호화가 실패하면, 예외가 발생되어야 합니다.")
    void decryptErrorScrapAndSaveTest() {
        final var service = new ScrapSaveService(
                new ReadMemberPort.FakeReadMemberPort(),
                new RequestScrapPort.FakeRequestScrapPort(),
                new ReadDeductionPort.FakeReadDeductionPort(),
                new DeleteDeductionPort.FakeDeleteDeductionPort(),
                new SaveDeductionPort.FakeSaveDeductionPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new DeleteIncomePort.FakeDeleteIncomePort(),
                new SaveIncomePort.FakeSaveIncomePort(),
                new AESService("key")
        );

        final var message = assertThrows(IllegalArgumentException.class,
                () -> service.scrapAndSave(decryptErrorScrapSaveCommand())).getMessage();

        assertThat(message).contains("주민등록번호 복호화 과정에 실패했습니다.");
    }

    private ScrapSaveCommand dataExistScrapSaveCommand() {
        return new ScrapSaveCommand(2L);
    }

    @Test
    @DisplayName("해당년도에 저장된 스크랩 정보가 있는 경우, 예외가 발생되어야 합니다.")
    void dataExistScrapAndSaveTest() {
        final var service = new ScrapSaveService(
                new ReadMemberPort.FakeReadMemberPort(),
                new RequestScrapPort.FakeRequestScrapPort(),
                new ReadDeductionPort.FakeReadDeductionPort(),
                new DeleteDeductionPort.FakeDeleteDeductionPort(),
                new SaveDeductionPort.FakeSaveDeductionPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new DeleteIncomePort.FakeDeleteIncomePort(),
                new SaveIncomePort.FakeSaveIncomePort(),
                new AESService("key")
        );

        final var message = assertThrows(IllegalArgumentException.class,
                () -> service.scrapAndSave(dataExistScrapSaveCommand())).getMessage();

        assertThat(message).contains("이미 진행된 스크랩 정보가 있습니다. 해당 정보를 덮어쓰기 하려면 overwrite 옵션을 true로 설정해주십시오.");
    }

    private ScrapSaveCommand overwriteScrapSaveCommand() {
        return new ScrapSaveCommand(2L, true);
    }

    @Test
    @DisplayName("해당년도에 저장된 스크랩 정보가 있지만 덮어쓰기 옵션이 true인 경우, 예외 발생 없이 마무리 되어야 합니다.")
    void overwriteScrapAndSaveTest() {
        final var service = new ScrapSaveService(
                new ReadMemberPort.FakeReadMemberPort(),
                new RequestScrapPort.FakeRequestScrapPort(),
                new ReadDeductionPort.FakeReadDeductionPort(),
                new DeleteDeductionPort.FakeDeleteDeductionPort(),
                new SaveDeductionPort.FakeSaveDeductionPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new DeleteIncomePort.FakeDeleteIncomePort(),
                new SaveIncomePort.FakeSaveIncomePort(),
                new AESService("key")
        );

        assertDoesNotThrow(() -> service.scrapAndSave(overwriteScrapSaveCommand()));
    }

    private ScrapSaveCommand saveIncomeErrorScrapSaveCommand() {
        return new ScrapSaveCommand(3L, true);
    }

    @Test
    @DisplayName("종합 소득 저장 과정에서 에러가 발생한 경우, 예외가 발생되어야 합니다.")
    void saveIncomeErrorScrapAndSaveTest() {
        final var service = new ScrapSaveService(
                new ReadMemberPort.FakeReadMemberPort(),
                new RequestScrapPort.FakeRequestScrapPort(),
                new ReadDeductionPort.FakeReadDeductionPort(),
                new DeleteDeductionPort.FakeDeleteDeductionPort(),
                new SaveDeductionPort.FakeSaveDeductionPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new DeleteIncomePort.FakeDeleteIncomePort(),
                new SaveIncomePort.FakeSaveIncomePort(),
                new AESService("key")
        );

        final var message = assertThrows(RuntimeException.class,
                () -> service.scrapAndSave(saveIncomeErrorScrapSaveCommand())).getMessage();

        assertThat(message).contains("처리 중 오류가 발생했습니다.");
    }

    private ScrapSaveCommand saveDeductionErrorScrapSaveCommand() {
        return new ScrapSaveCommand(4L, true);
    }

    @Test
    @DisplayName("공제 내역 저장 과정에서 에러가 발생한 경우, 예외가 발생되어야 합니다.")
    void saveDeductionErrorScrapAndSaveTest() {
        final var service = new ScrapSaveService(
                new ReadMemberPort.FakeReadMemberPort(),
                new RequestScrapPort.FakeRequestScrapPort(),
                new ReadDeductionPort.FakeReadDeductionPort(),
                new DeleteDeductionPort.FakeDeleteDeductionPort(),
                new SaveDeductionPort.FakeSaveDeductionPort(),
                new ReadIncomePort.FakeReadIncomePort(),
                new DeleteIncomePort.FakeDeleteIncomePort(),
                new SaveIncomePort.FakeSaveIncomePort(),
                new AESService("key")
        );

        final var message = assertThrows(RuntimeException.class,
                () -> service.scrapAndSave(saveDeductionErrorScrapSaveCommand())).getMessage();

        assertThat(message).contains("처리 중 오류가 발생했습니다.");
    }
}
