package com.yongbi.szsyongbi.scrap.application.service;

import com.yongbi.szsyongbi.aes.application.port.in.AESUseCase;
import com.yongbi.szsyongbi.deduction.application.port.out.DeleteDeductionPort;
import com.yongbi.szsyongbi.deduction.application.port.out.ReadDeductionPort;
import com.yongbi.szsyongbi.deduction.application.port.out.SaveDeductionPort;
import com.yongbi.szsyongbi.deduction.domain.Deduction;
import com.yongbi.szsyongbi.deduction.domain.DeductionType;
import com.yongbi.szsyongbi.income.application.port.out.DeleteIncomePort;
import com.yongbi.szsyongbi.income.application.port.out.ReadIncomePort;
import com.yongbi.szsyongbi.income.application.port.out.SaveIncomePort;
import com.yongbi.szsyongbi.income.domain.Income;
import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.scrap.application.port.in.ScrapSaveCommand;
import com.yongbi.szsyongbi.scrap.application.port.in.ScrapSaveUseCase;
import com.yongbi.szsyongbi.scrap.application.port.out.RequestScrapPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

@Service
public class ScrapSaveService implements ScrapSaveUseCase {
    private final ReadMemberPort readMemberPort;
    private final RequestScrapPort requestScrapPort;
    private final ReadDeductionPort readDeductionPort;
    private final DeleteDeductionPort deleteDeductionPort;
    private final SaveDeductionPort saveDeductionPort;
    private final ReadIncomePort readIncomePort;
    private final DeleteIncomePort deleteIncomePort;
    private final SaveIncomePort saveIncomePort;
    private final AESUseCase aesUseCase;

    public ScrapSaveService(ReadMemberPort readMemberPort, RequestScrapPort requestScrapPort, ReadDeductionPort readDeductionPort, DeleteDeductionPort deleteDeductionPort, SaveDeductionPort saveDeductionPort, ReadIncomePort readIncomePort, DeleteIncomePort deleteIncomePort, SaveIncomePort saveIncomePort, AESUseCase aesUseCase) {
        this.readMemberPort = readMemberPort;
        this.requestScrapPort = requestScrapPort;
        this.readDeductionPort = readDeductionPort;
        this.deleteDeductionPort = deleteDeductionPort;
        this.saveDeductionPort = saveDeductionPort;
        this.readIncomePort = readIncomePort;
        this.deleteIncomePort = deleteIncomePort;
        this.saveIncomePort = saveIncomePort;
        this.aesUseCase = aesUseCase;
    }

    @Override
    @Transactional
    public void scrapAndSave(ScrapSaveCommand command) {
        final var beforeYear = LocalDate.now().minusYears(1).getYear();
        final var member = readMemberPort.read(command.id()).orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 회원입니다."));

        // 회원 정보에 암호화 저장되어있는 주민등록번호 복호화
        final var decrypted = aesUseCase.tryDecryptAES256(member.regNo());
        if (decrypted == null) {
            throw new IllegalArgumentException("주민등록번호 복호화 과정에 실패했습니다.");
        }

        // 스크랩 진행 사전에 이미 해당년도에 저장된 정보가 있는지 확인
        final var incomeData = readIncomePort.read(member.id(), beforeYear);
        final var deductionData = readDeductionPort.read(member.id(), beforeYear);

        // 저장된 정보가 있고 덮어쓰기 옵션이 false 인 경우 에러 발생
        if ((incomeData.isPresent() || !deductionData.isEmpty()) && !command.overwrite()) {
            throw new IllegalArgumentException("이미 진행된 스크랩 정보가 있습니다. 해당 정보를 덮어쓰기 하려면 overwrite 옵션을 true로 설정해주십시오.");
        }

        // 덮어쓰기 옵션이 true 이고 저장된 소득 정보가 있으면 미리 삭제
        if (incomeData.isPresent()) {
            deleteIncomePort.delete(member.id(), beforeYear);
        }

        // 덮어쓰기 옵션이 true 이고 저장된 공제 정보가 있으면 미리 삭제
        if (!deductionData.isEmpty()) {
            deleteDeductionPort.delete(member.id(), beforeYear);
        }

        // 스크랩 진행
        final var scrapData = requestScrapPort.get(member.name(), decrypted);

        // 종합 소득
        final var scrapIncome = scrapData.getTotalIncome();
        final var income = new Income(member.id(), beforeYear, scrapIncome);

        // 종합 소득 저장
        saveIncomePort.save(income);

        // 공제액
        final var data = new ArrayList<Deduction>();
        final var scrapDeduction = scrapData.getIncomeDeduction();

        // 세액 공제
        data.add(new Deduction(member.id(), DeductionType.TAX, beforeYear, scrapDeduction.getTaxDeduction()));

        // 국민 연금 공제
        final var pension = scrapDeduction.getNationalPensionDeduction();
        for (YearMonth key: pension.keySet()) {
            data.add(new Deduction(member.id(), DeductionType.NATIONAL_PENSION, key.getYear(), key.getMonthValue(), pension.get(key)));
        }

        // 신용 카드 공제
        final var credit = scrapDeduction.getCreditCardDeduction();
        for (YearMonth key: credit.keySet()) {
            data.add(new Deduction(member.id(), DeductionType.CREDIT_CARD, key.getYear(), key.getMonthValue(), credit.get(key)));
        }

        // 공제 저장
        saveDeductionPort.saveAll(data);
    }
}
