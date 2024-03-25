package com.yongbi.szsyongbi.shared;
public record BaseResponse(boolean result, String message) {
    public BaseResponse(String message) {
        this(false, message);
    }
}
