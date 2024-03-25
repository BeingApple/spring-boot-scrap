package com.yongbi.szsyongbi.scrap.adapter.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongbi.szsyongbi.deduction.adapter.out.persistence.DeductionRepository;
import com.yongbi.szsyongbi.income.adapter.out.persistence.IncomeRepository;
import com.yongbi.szsyongbi.income.application.port.out.SaveIncomePort;
import com.yongbi.szsyongbi.income.domain.Income;
import com.yongbi.szsyongbi.member.adapter.out.persistence.MemberRepository;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberCommand;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberUseCase;
import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.member.domain.Member;
import com.yongbi.szsyongbi.token.domain.TokenFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScrapControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private TokenFactory tokenFactory;

    @Autowired
    private ReadMemberPort readMemberPort;

    @Autowired
    private SaveIncomePort saveIncomePort;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private DeductionRepository deductionRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeAll
    static void createMember(@Autowired CreateMemberUseCase createMemberUseCase) {
        createMemberUseCase.create(new CreateMemberCommand("loginId", "password", "동탁", "921108-1582816"));
    }
    
    private long getMemberId() {
        return readMemberPort.read("loginId").map(Member::id).orElse(1L);
    }

    @DisplayName("유효한 JWT와 함께 스크랩 요청시, 성공해야 합니다.")
    @Test
    void requestScrap() throws Exception {
        final var url = "/szs/scrap";
        final var token = tokenFactory.newUserAccessToken(getMemberId(), "loginId", "동탁", 10);

        final ResultActions result = mockMvc.perform(
                post(url)
                        .header("Authorization", "Bearer "+token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("스크랩 결과 저장되었습니다."));
    }

    @DisplayName("기저장된 스크랩 내역이 있을 경우, 덮어쓰기 여부를 물어봐야 합니다.")
    @Test
    void existScrap() throws Exception {
        final var url = "/szs/scrap";
        final var memberId = getMemberId();
        saveIncomePort.save(new Income(memberId, 2023, new BigDecimal("20000000")));
        final var token = tokenFactory.newUserAccessToken(memberId, "loginId", "동탁", 10);

        final ResultActions result = mockMvc.perform(
                post(url)
                        .header("Authorization", "Bearer "+token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("이미 진행된 스크랩 정보가 있습니다. 해당 정보를 덮어쓰기 하려면 overwrite 옵션을 true로 설정해주십시오."));
    }

    @DisplayName("기저장된 스크랩 내역이 있고 덮어쓰기 옵션이 true인 경우, 성공해야 합니다.")
    @Test
    void overwriteScrap() throws Exception {
        final var url = "/szs/scrap";
        final var memberId = getMemberId();
        saveIncomePort.save(new Income(memberId, 2023, new BigDecimal("20000000")));
        final var content = mapper.writeValueAsString(new ScrapController.ScrapRequestPayload(true));
        final var token = tokenFactory.newUserAccessToken(memberId, "loginId", "동탁", 10);

        final ResultActions result = mockMvc.perform(
                post(url)
                        .header("Authorization", "Bearer "+token)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("스크랩 결과 저장되었습니다."));
    }

    @DisplayName("유효하지 않은 JWT와 함께 스크랩 요청시, HTTP 401을 반환해야 합니다.")
    @Test
    void requestUnauthorizedScrap() throws Exception {
        final var url = "/szs/scrap";
        final var token = tokenFactory.newUserAccessToken(getMemberId(), "id-is-not-created", "동탁", 10);

        final ResultActions result = mockMvc.perform(
                post(url)
                        .header("Authorization", "Bearer "+token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @DisplayName("JWT 없이 스크랩 요청시, HTTP 403을 반환해야 합니다.")
    @Test
    void requestEmptyJWTScrap() throws Exception {
        final var url = "/szs/scrap";

        final ResultActions result = mockMvc.perform(
                post(url).accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isForbidden());
    }

    @AfterAll
    static void deleteMembers(@Autowired MemberRepository memberRepository) {
        memberRepository.deleteAll();
    }

    @AfterEach
    void deleteIncomeAndDeduction() {
        incomeRepository.deleteAll();
        deductionRepository.deleteAll();
    }
}
