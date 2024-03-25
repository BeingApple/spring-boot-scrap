package com.yongbi.szsyongbi.member.application.service;

import com.yongbi.szsyongbi.aes.application.port.in.AESUseCase;
import com.yongbi.szsyongbi.member.application.port.in.CheckUserIdDuplicationUseCase;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberCommand;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberUseCase;
import com.yongbi.szsyongbi.member.application.port.out.SaveMemberPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CreateMemberService implements CreateMemberUseCase {
    private final CheckUserIdDuplicationUseCase checkUserIdDuplicationUseCase;
    private final SaveMemberPort saveMemberPort;
    private final PasswordEncoder passwordEncoder;
    private final AESUseCase aesUseCase;

    private final Map<String, String> whiteList = Map.of(
            "동탁", "921108-1582816",
            "관우", "681108-1582816",
            "손권", "890601-2455116",
            "유비", "790411-1656116",
            "조조", "810326-2715702"
    );

    public CreateMemberService(CheckUserIdDuplicationUseCase checkUserIdDuplicationUseCase, SaveMemberPort saveMemberPort, PasswordEncoder passwordEncoder, AESUseCase aesUseCase) {
        this.checkUserIdDuplicationUseCase = checkUserIdDuplicationUseCase;
        this.saveMemberPort = saveMemberPort;
        this.passwordEncoder = passwordEncoder;
        this.aesUseCase = aesUseCase;
    }

    @Override
    public boolean create(CreateMemberCommand command) {
        command.verify();

        // 가입 가능한 이름, 주민등록번호인지 화이트 리스트 체크
        final var listCheck = whiteList.get(command.name());
        if (listCheck == null || !listCheck.equals(command.regNo())) {
            throw new IllegalArgumentException("가입할 수 없는 사용자입니다.");
        }

        // 아이디 중복 검사
        final var check = checkUserIdDuplicationUseCase.check(command.userId());
        if (!check) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 주민등록번호 양방향 암호화 처리
        final var encrypted = aesUseCase.tryEncryptAES256(command.regNo());
        if (encrypted == null) {
            throw new IllegalArgumentException("주민등록번호 암호화 과정에 실패했습니다.");
        }

        // 도메인 생성하고 비밀번호 단방향 암호화 처리, 주민등록번호 암호화 반영
        final var domain = command.domain()
                .encryptPassword(passwordEncoder.encode(command.password()))
                .encryptRegNo(encrypted);

        // 저장
        return saveMemberPort.save(domain);
    }
}
