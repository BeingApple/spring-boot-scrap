package com.yongbi.szsyongbi.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongbi.szsyongbi.configuration.security.AuthenticationFilter;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberCommand;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RequestLoginTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CreateMemberUseCase createMemberUseCase;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @DisplayName("유효한 사용자가 로그인 요청시, 성공하고 accessToken을 반환해야 합니다")
    @Test
    void requestLogin() throws Exception {
        final var url = "/szs/login";
        createMemberUseCase.create(new CreateMemberCommand("testId", "password", "동탁", "921108-1582816"));
        final var content = mapper.writeValueAsString(
                new AuthenticationFilter.UserCredential("testId", "password")
        );

        final ResultActions result = mockMvc.perform(
                post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @DisplayName("유효하지 않은 사용자가 로그인 요청시, HTTP 401을 응답해야 합니다.")
    @Test
    void loginFail() throws Exception {
        final var url = "/szs/login";
        final var content = mapper.writeValueAsString(
                new AuthenticationFilter.UserCredential("noId =", "password")
        );

        final ResultActions result = mockMvc.perform(
                post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.accessToken").isEmpty());
    }
}
