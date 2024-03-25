package com.yongbi.szsyongbi.member.adapter.in.http;

import com.yongbi.szsyongbi.member.application.port.in.CreateMemberCommand;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberUseCase;
import com.yongbi.szsyongbi.shared.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "회원", description = "회원가입과 로그인입니다.")
@Slf4j
@RestController
@RequestMapping
public class CreateMemberController {
    private final CreateMemberUseCase createMemberUseCase;

    public CreateMemberController(CreateMemberUseCase createMemberUseCase) {
        this.createMemberUseCase = createMemberUseCase;
    }

    @Operation(summary = "회원가입", description = "회원가입입니다. 가입 가능한 유저는 다음과 같습니다<br />" +
            "<table border=1>" +
            "<tr><td>동탁</td><td>921108-1582816</td></tr>" +
            "<tr><td>관우</td><td>681108-1582816</td></tr>" +
            "<tr><td>손권</td><td>890601-2455116</td></tr>" +
            "<tr><td>유비</td><td>790411-1656116</td></tr>" +
            "<tr><td>조조</td><td>810326-2715702</td></tr>" +
            "</table>")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 요청 형식이 잘못되었습니다. "),
            @ApiResponse(responseCode = "500", description = "회원가입에 실패했습니다."),
    })
    @PostMapping(value="/szs/signup", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BaseResponse> createMember(
            HttpServletRequest request,
            @RequestBody @Valid CreateMemberRequest payload
    ) {
        final var command = payload.command();
        try {
            final var result = createMemberUseCase.create(command);
            final var location = URI.create(request.getRequestURI());
            return ResponseEntity.created(location)
                    .body(new BaseResponse(result, result ? "회원가입 성공" : "회원가입 실패"));
        } catch (IllegalArgumentException ex) {
            log.error("Post /szs/signup Illegal Argument Error : " + command, ex);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse(ex.getMessage()));
        } catch (RuntimeException ex) {
            log.error("Post /szs/signup Runtime Error : " + command, ex);
            return ResponseEntity.internalServerError()
                    .body(new BaseResponse(ex.getMessage()));
        }
    }

    @Schema(description = "회원가입 요청에 사용되는 객체입니다.")
    public record CreateMemberRequest(
            @Schema(description = "사용자 아이디", example = "kw68")
            @NotBlank(message = "아이디는 필수값입니다.")
            String userId,
            @Schema(description = "사용자 비밀번호", example = "123456")
            @NotBlank(message = "비밀번호는 필수값입니다.")
            String password,
            @Schema(description = "사용자 이름", example = "관우")
            @NotBlank(message = "이름은 필수값입니다.")
            String name,
            @Schema(description = "사용자 주민등록번호, 대한민국 주민등록번호의 형식을 따릅니다.", example = "681108-1582816")
            @NotBlank(message = "주민등록번호는 필수값입니다.")
            @Pattern(regexp = "\\d{2}([0]\\d|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])-[1-4]\\d{6}", message = "잘못된 주민등록번호 형식입니다.")
            String regNo
    ) {
        public CreateMemberCommand command() {
            return new CreateMemberCommand(this.userId,
                    this.password,
                    this.name,
                    this.regNo);
        }
    }
}
