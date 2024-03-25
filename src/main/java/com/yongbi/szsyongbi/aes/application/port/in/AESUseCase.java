package com.yongbi.szsyongbi.aes.application.port.in;

public interface AESUseCase {
    String tryEncryptAES256(String message);
    String tryDecryptAES256(String message);
}
