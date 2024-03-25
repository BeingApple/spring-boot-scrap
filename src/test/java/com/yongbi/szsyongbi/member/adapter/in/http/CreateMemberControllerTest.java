package com.yongbi.szsyongbi.member.adapter.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberCommand;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateMemberControllerTest {
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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static Stream<Arguments> provideAvailableUser() {
        return Stream.of(
                Arguments.of("userId1", "password", "동탁", "921108-1582816"),
                Arguments.of("userId2", "password", "관우", "681108-1582816"),
                Arguments.of("userId3", "password", "손권", "890601-2455116"),
                Arguments.of("userId4", "password", "유비", "790411-1656116"),
                Arguments.of("userId5", "password", "조조", "810326-2715702")
        );
    }

    @DisplayName("유효한 사용자에 대한 회원가입 요청시 성공해야 합니다.")
    @ParameterizedTest(name = "{index} : {2} / {3}")
    @MethodSource("provideAvailableUser")
    void createMemberTest(String userId, String password, String name, String regNo) throws Exception {
        final var url = "/szs/signup";
        final var content = mapper.writeValueAsString(
                new CreateMemberController.CreateMemberRequest(
                    userId, password, name, regNo)
        );

        final ResultActions result = mockMvc.perform(
                post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("회원가입 성공"));
    }

    @DisplayName("유효하지 않은 사용자에 대한 회원가입 요청시, HTTP 400을 응답해야 합니다.")
    @Test
    void createUnavailableMemberTest() throws Exception {
        final var url = "/szs/signup";
        final var content = mapper.writeValueAsString(
                new CreateMemberController.CreateMemberRequest(
                        "userId6", "password", "손권", "921108-1582816")
        );

        final ResultActions result = mockMvc.perform(
                post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("가입할 수 없는 사용자입니다."));
    }

    private static Stream<Arguments> provideUnavailableRequest() {
        return Stream.of(
                Arguments.of(null, "password", "동탁", "921108-1582816", "아이디는 필수값입니다."),
                Arguments.of("userId2", null, "관우", "681108-1582816", "비밀번호는 필수값입니다."),
                Arguments.of("userId3", "password", null, "890601-2455116", "이름은 필수값입니다."),
                Arguments.of("userId4", "password", "유비", null, "주민등록번호는 필수값입니다."),
                Arguments.of("userId5", "password", "조조", "810326-6715702", "잘못된 주민등록번호 형식입니다.")
        );
    }

    @DisplayName("유효하지 않은 데이터를 보낼 경우, HTTP 400을 응답해야 합니다.")
    @ParameterizedTest(name = "{index} : {4}")
    @MethodSource("provideUnavailableRequest")
    void requestUnavailableRequestTest(String userId, String password, String name, String regNo, String message) throws Exception {
        final var url = "/szs/signup";
        final var content = mapper.writeValueAsString(
                new CreateMemberController.CreateMemberRequest(
                        userId, password, name, regNo)
        );

        final ResultActions result = mockMvc.perform(
                post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value(message));
    }

    @DisplayName("중복된 아이디에 대한 회원가입 요청시, HTTP 400을 응답해야 합니다.")
    @Test
    void createDuplicatedIdMemberTest() throws Exception {
        final var url = "/szs/signup";
        createMemberUseCase.create(new CreateMemberCommand("duplicatedId", "password", "동탁", "921108-1582816"));
        final var content = mapper.writeValueAsString(
                new CreateMemberController.CreateMemberRequest(
                        "duplicatedId", "password", "동탁", "921108-1582816")
        );

        final ResultActions result = mockMvc.perform(
                post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 아이디입니다."));
    }
}
