package com.bolingx.common.encrypt;

import com.bolingx.common.util.base64.XBase64;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class DigestUtil {
    public static final String MD5 = "MD5";
    public static final String SHA_256 = "SHA-256";

    private final MessageDigest md;

    public DigestUtil(MessageDigest md) {
        this.md = md;
    }


    public static DigestUtil useAlgorithm(String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return new DigestUtil(md);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("encryptionMd5 error", e);
        }
    }

    public byte[] digest(byte[] bytes) {
        return md.digest(bytes);
    }

    public byte[] digestToBytes(String plainText) {
        return digestToBytes(plainText, StandardCharsets.UTF_8);
    }

    public byte[] digestToBytes(String plainText, Charset charset) {
        return digest(plainText.getBytes(charset));
    }

    public String digestToHex(String plainText) {
        byte[] bytes = digestToBytes(plainText);
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public String digestToBase64(String plainText) {
        byte[] bytes = digestToBytes(plainText);
        return XBase64.encode(bytes);
    }
}