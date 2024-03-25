package com.yongbi.szsyongbi.refund.adapter.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongbi.szsyongbi.deduction.adapter.out.persistence.DeductionRepository;
import com.yongbi.szsyongbi.income.adapter.out.persistence.IncomeRepository;
import com.yongbi.szsyongbi.member.adapter.out.persistence.MemberRepository;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberCommand;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberUseCase;
import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.member.domain.Member;
import com.yongbi.szsyongbi.scrap.application.port.in.ScrapSaveCommand;
import com.yongbi.szsyongbi.scrap.application.port.in.ScrapSaveUseCase;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CalculateRefundControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private TokenFactory tokenFactory;

    @Autowired
    private CreateMemberUseCase createMemberUseCase;

    @Autowired
    private ReadMemberPort readMemberPort;

    @Autowired
    private ScrapSaveUseCase scrapSaveUseCase;

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

    @DisplayName("유효한 JWT와 함께 결정세액 계산 요청시, 성공해야 합니다.")
    @Test
    void requestCalculate() throws Exception {
        final var url = "/szs/refund";
        final var token = tokenFactory.newUserAccessToken(getMemberId(), "loginId", "동탁", 10);
        scrapSaveUseCase.scrapAndSave(new ScrapSaveCommand(getMemberId()));

        final ResultActions result = mockMvc.perform(
                get(url)
                        .header("Authorization", "Bearer "+token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.결정세액").value("959,999"))
                .andExpect(jsonPath("$.message").value("결정세액 산출에 성공했습니다."));
    }

    @DisplayName("직전년도 종합소득이 없는 상태로 결정세액 계산 요청 시, HTTP 400을 반환해야 합니다.")
    @Test
    void requestNotProvidedTotalIncomeScrap() throws Exception {
        final var url = "/szs/refund";
        final var token = tokenFactory.newUserAccessToken(getMemberId(), "loginId", "동탁", 10);

        final ResultActions result = mockMvc.perform(
                get(url)
                        .header("Authorization", "Bearer "+token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("직전년도 종합소득이 존재하지 않습니다."));
    }

    @DisplayName("유효하지 않은 JWT와 함께 결정세액 계산 요청시, HTTP 401을 반환해야 합니다.")
    @Test
    void requestUnauthorizedCalculate() throws Exception {
        final var url = "/szs/refund";
        final var token = tokenFactory.newUserAccessToken(getMemberId(), "id-is-not-created", "동탁", 10);

        final ResultActions result = mockMvc.perform(
                get(url)
                        .header("Authorization", "Bearer "+token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
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
