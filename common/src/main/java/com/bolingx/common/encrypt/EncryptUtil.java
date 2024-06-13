package com.bolingx.common.encrypt;


import com.bolingx.common.util.base64.XBase64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
public class EncryptUtil {

    public static final String ALGORITHM_AES = "AES";
    public static final String AES_ECB_PKCS5Padding = "AES/ECB/PKCS5Padding";

    private static final String DEFAULT_KEYS_SEPARATOR = ":";

    private final Pair<Cipher, Cipher> cipherPair;

    public static EncryptUtil useAES(String key) {
        return useCipherPair(AES_ECB_PKCS5Padding, createAESKey(key));
    }

    public static EncryptUtil useAES(Key key) {
        return useCipherPair(AES_ECB_PKCS5Padding, key);
    }

    public static EncryptUtil useCipherPair(String algorithm, Key key) {
        Cipher encipher;
        Cipher decipher;
        try {
            encipher = Cipher.getInstance(algorithm);
            encipher.init(Cipher.ENCRYPT_MODE, key);
            decipher = Cipher.getInstance(algorithm);
            decipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            throw new RuntimeException("AESUtils init encipher error", e);
        }
        ImmutablePair<Cipher, Cipher> pair = ImmutablePair.of(encipher, decipher);
        return new EncryptUtil(pair);
    }

    public static SecretKeySpec createAESKey(String key) {
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM_AES);
    }

    public EncryptUtil(Pair<Cipher, Cipher> cipherPair) {
        this.cipherPair = cipherPair;
    }


    public String createSecret(String... keys) {
        return createSecretWith(keys, DEFAULT_KEYS_SEPARATOR);
    }

    public String[] parseSecret(String secret) {
        return parseSecret(secret, DEFAULT_KEYS_SEPARATOR);
    }

    public String createSecretWith(String[] keys, String separator) {
        String key = StringUtils.join(keys, separator);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] bytesSecret = doCreateBytesSecret(keyBytes);
        return XBase64.encode(bytesSecret);
    }

    public String[] parseSecret(String secret, String separator) {
        byte[] bytesSecret = XBase64.decode(secret);
        byte[] keyBytes = doParseBytesSecret(bytesSecret);
        String key = new String(keyBytes, StandardCharsets.UTF_8);
        return key.split(separator);
    }

    protected byte[] doCreateBytesSecret(byte[] bytes) {
        try {
            return cipherPair.getLeft().doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected byte[] doParseBytesSecret(byte[] bytes) {
        try {
            return cipherPair.getRight().doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
