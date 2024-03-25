package com.yongbi.szsyongbi.configuration;

import com.yongbi.szsyongbi.shared.BaseResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionAdvisor {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse> processValidationError(MethodArgumentNotValidException ex) {
        final var bindingResult = ex.getBindingResult();
        final var message = bindingResult.getFieldErrors().stream().findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("");

        return ResponseEntity.badRequest()
                .body(new BaseResponse(message));
    }
}
