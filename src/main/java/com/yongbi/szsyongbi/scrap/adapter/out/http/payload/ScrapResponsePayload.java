package com.yongbi.szsyongbi.scrap.adapter.out.http.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ScrapResponsePayload {
    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "data")
    private ScrapData data;

    @JsonProperty(value = "errors")
    private Errors errors;

    @Getter
    public static class Errors {
        @JsonProperty(value = "code")
        private String code;

        @JsonProperty(value = "message")
        private String message;

        @JsonProperty(value = "validations")
        private String validations;
    }
}
