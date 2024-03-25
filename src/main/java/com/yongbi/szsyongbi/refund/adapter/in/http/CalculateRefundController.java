package com.yongbi.szsyongbi.refund.adapter.in.http;

import com.yongbi.szsyongbi.refund.application.port.in.CalculateRefundCommand;
import com.yongbi.szsyongbi.refund.application.port.in.CalculateRefundResponse;
import com.yongbi.szsyongbi.refund.application.port.in.CalculateRefundUseCase;
import com.yongbi.szsyongbi.security.domain.AppUserDetails;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "세액", description = "결정세액 계산 진행과 계산에 바탕이 될 종합소득과 공제 정보를 스크랩합니다.")
@Slf4j
@RestController
@RequestMapping
public class CalculateRefundController {
    private final CalculateRefundUseCase calculateRefundUseCase;

    public CalculateRefundController(CalculateRefundUseCase calculateRefundUseCase) {
        this.calculateRefundUseCase = calculateRefundUseCase;
    }

    @Operation(summary = "결정세액 산출", description = "저장된 종합소득과 공제 내역을 바탕으로 결정세액을 산출합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "결정세액 산출 성공",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CalculateRefundResponse.class),
                                    examples = {
                                            @ExampleObject(value = """
                                                    {
                                                        "결정세액": "959,999",
                                                        "result": true,
                                                        "message": "결정세액 산출에 성공했습니다."
                                                    }"""
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(responseCode = "400",
                    description = "결정세액 산출 실패, 기 저장된 종합소득 내역이 없거나 계산 과정에 실패했습니다.",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CalculateRefundResponse.class),
                                    examples = {
                                            @ExampleObject(value = """
                                                    {
                                                         "결정세액": "",
                                                         "result": false,
                                                         "message": "직전년도 종합소득이 존재하지 않습니다."
                                                     }"""
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(responseCode = "500",
                    description = "결정세액 산출 실패, 결정세액을 저장하는 과정에서 실패했습니다.",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CalculateRefundResponse.class),
                                    examples = {
                                            @ExampleObject(value = """
                                                    {
                                                         "결정세액": "",
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
    @GetMapping(value="/szs/refund", produces = {MediaType.APPLICATION_JSON_VALUE})
    @RolesAllowed(value = {"USER"})
    public ResponseEntity<CalculateRefundResponse> scrap(
            @AuthenticationPrincipal AppUserDetails principal
    ) {
        final var command = new CalculateRefundCommand(principal.getId());
        try {
            final var response = calculateRefundUseCase.calculate(command);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.error("Post /szs/refund Illegal Argument Error : " + command, ex);
            return ResponseEntity.badRequest()
                    .body(CalculateRefundResponse.fail(ex.getMessage()));
        } catch (RuntimeException ex) {
            log.error("Post /szs/refund Runtime Error : " + command, ex);
            return ResponseEntity.internalServerError()
                    .body(CalculateRefundResponse.fail(ex.getMessage()));
        }
    }
}
