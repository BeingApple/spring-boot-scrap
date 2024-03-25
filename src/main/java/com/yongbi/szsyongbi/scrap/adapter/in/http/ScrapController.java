package com.yongbi.szsyongbi.scrap.adapter.in.http;

import com.yongbi.szsyongbi.scrap.application.port.in.ScrapSaveCommand;
import com.yongbi.szsyongbi.scrap.application.port.in.ScrapSaveUseCase;
import com.yongbi.szsyongbi.security.domain.AppUserDetails;
import com.yongbi.szsyongbi.shared.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "세액", description = "결정세액 계산 진행과 계산에 바탕이 될 종합소득과 공제 정보를 스크랩합니다.")
@Slf4j
@RestController
@RequestMapping
public class ScrapController {
    private final ScrapSaveUseCase scrapSaveUseCase;

    public ScrapController(ScrapSaveUseCase scrapSaveUseCase) {
        this.scrapSaveUseCase = scrapSaveUseCase;
    }

    @Operation(summary = "스크랩", description = "종합소득과 공제 내역을 스크랩 해온 뒤 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "스크랩 성공",
                    content = {
                        @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = {
                                        @ExampleObject(value = """
                                                {
                                                    "result": true,
                                                    "message": "스크랩 결과 저장되었습니다."
                                                }"""
                                        )
                                }
                        )
                    }
            ),
            @ApiResponse(responseCode = "400",
                    description = "스크랩 실패, 스크랩 요청에 필요한 주민등록번호 복호화가 실패했거나, 이미 기 저장된 스크랩 정보가 있으므로 overwrite 옵션이 추가로 필요합니다.",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = {
                                            @ExampleObject(value = """
                                                    {
                                                        "result": false,
                                                        "message": "주민등록번호 복호화 과정에 실패했습니다."
                                                    }"""
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(responseCode = "500",
                    description = "스크랩 실패, 스크랩을 불러오거나 저장하는 과정에서 실패했습니다.",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = {
                                            @ExampleObject(value = """
                                                    {
                                                        "result": false,
                                                        "message": "처리 중 오류가 발생했습니다."
                                                    }"""
                                            )
                                    }
                            )
                    }
            ),
    })
    @SecurityRequirement(name = "bearer-key")
    @PostMapping(value="/szs/scrap", produces = {MediaType.APPLICATION_JSON_VALUE})
    @RolesAllowed(value = {"USER"})
    public ResponseEntity<BaseResponse> scrap(
            @AuthenticationPrincipal AppUserDetails principal,
            @RequestBody(required = false) ScrapRequestPayload payload
    ) {
        final var command = new ScrapSaveCommand(principal.getId(),
                Optional.ofNullable(payload).map(ScrapRequestPayload::overwrite).orElse(false));
        try {
            scrapSaveUseCase.scrapAndSave(command);
            return ResponseEntity.ok(new BaseResponse(true, "스크랩 결과 저장되었습니다."));
        } catch (IllegalArgumentException ex) {
            log.error("Post /szs/scrap Illegal Argument Error : " + command, ex);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse(ex.getMessage()));
        } catch (RuntimeException ex) {
            log.error("Post /szs/scrap Runtime Error : " + command, ex);
            return ResponseEntity.internalServerError()
                    .body(new BaseResponse(ex.getMessage()));
        }
    }

    public record ScrapRequestPayload(
            @Schema(description = "기 저장된 스크랩 정보가 있는 경우 덮어쓰기 여부")
            boolean overwrite
    ) {
        public ScrapRequestPayload() {
            this(false);
        }
    }
}
