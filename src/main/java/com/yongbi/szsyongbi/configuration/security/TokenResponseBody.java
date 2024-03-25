package com.yongbi.szsyongbi.configuration.security;

public record TokenResponseBody(boolean result, String accessToken, String message){

    public static TokenResponseBody success(final String accessToken) {
        return new TokenResponseBody(true, accessToken, "로그인에 성공했습니다.");
    }

    public static TokenResponseBody failure(String message) {
        return new TokenResponseBody(false, "", message);
    }
}
