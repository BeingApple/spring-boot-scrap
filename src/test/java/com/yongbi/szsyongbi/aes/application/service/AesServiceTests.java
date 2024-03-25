package com.yongbi.szsyongbi.aes.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AesServiceTests {
    @DisplayName("문자열에 대해 암호화할 수 있어야 합니다.")
    @Test
    void encryptTest() {
        final var service = new AESService("key");

        final var encryption = service.tryEncryptAES256("TEST");

        assertThat(encryption).isNotNull();
    }

    @DisplayName("문자열에 대해 암호화한 결과와 복호화 한 결과는 동일해야 합니다.")
    @Test
    void encryptAndDecryptTest() {
        final var service = new AESService("key");
        final var string = "TEST";

        final var encryption = service.tryEncryptAES256(string);
        final var decryption = service.tryDecryptAES256(encryption);

        assertThat(string).isEqualTo(decryption);
    }
}
