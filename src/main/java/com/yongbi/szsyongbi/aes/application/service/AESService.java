package com.yongbi.szsyongbi.aes.application.service;

import com.yongbi.szsyongbi.aes.application.port.in.AESUseCase;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

@Slf4j
@Service
public class AESService implements AESUseCase {
    private final String key;

    public AESService(@Value("${aes.secret-key}") String key) {
        this.key = key;
    }

    private String encryptAES256(byte[] msg) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), bytes, 70000, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();

        byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(msg);
        byte[] buffer = new byte[bytes.length + ivBytes.length + encryptedTextBytes.length];

        System.arraycopy(bytes, 0, buffer, 0, bytes.length);
        System.arraycopy(ivBytes, 0, buffer, bytes.length, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, buffer, bytes.length + ivBytes.length, encryptedTextBytes.length);

        return new String(Base64.encodeBase64(buffer, false));
    }

    @Override
    @Nullable
    public String tryEncryptAES256(String message) {
        try {
            return encryptAES256(message.getBytes());
        } catch (Exception ex) {
            log.error("Encryption Error : " + message, ex);
        }

        return null;
    }

    private byte[] decryptAES256(String msg) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ByteBuffer buffer = ByteBuffer.wrap(Base64.decodeBase64(msg));

        byte[] saltBytes = new byte[20];
        buffer.get(saltBytes, 0, saltBytes.length);
        byte[] ivBytes = new byte[cipher.getBlockSize()];
        buffer.get(ivBytes, 0, ivBytes.length);
        byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes.length];

        buffer.get(encryptedTextBytes);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, 70000, 256);

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

        return cipher.doFinal(encryptedTextBytes);
    }

    @Override
    @Nullable
    public String tryDecryptAES256(String message) {
        try {
            return new String(decryptAES256(message));
        } catch (Exception ex) {
            log.error("Decryption Error : " + message, ex);
        }

        return null;
    }
}
