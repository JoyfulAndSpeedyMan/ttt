package com.bolingx.common.util.base64;


import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class XBase64 {

    private static final Base64.Encoder encoder = Base64.getEncoder();

    private static final Base64.Decoder decoder = Base64.getDecoder();

    private static final Object t = new Object();

    public static byte[] decode(String base64) {
        return decoder.decode(base64.getBytes(StandardCharsets.UTF_8));
    }


    public static String encode(byte[] binaryData) {
        byte[] encoded = encoder.encode(binaryData);
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static byte[] decodeBytes(byte[] bytes) {
        return decoder.decode(bytes);
    }

    public static byte[] encodeToBytes(byte[] binaryData) {
        return encoder.encode(binaryData);
    }

    public static String encode(String s) {
        return encode(s, StandardCharsets.UTF_8);
    }

    public static String encode(String s, Charset charset) {
        return encode(s.getBytes(charset));
    }

    public static String decodeToString(String base64) {
        return decodeToString(base64, StandardCharsets.UTF_8);
    }

    public static String decodeToString(String base64, Charset charset) {
        return new String(decode(base64), charset);
    }

    /**
     * 把+还成-， 把/换成_
     */
    public static String enReplace(String base64Str) {
        return StringUtils.isNotBlank(base64Str) ? StringUtils.replace(base64Str, "+", "-").replace("/", "_") : base64Str;
    }


    /**
     * 把-还成+， 把_换成/
     */
    public static String deReplace(String base64Str) {
        return StringUtils.isNotBlank(base64Str) ? StringUtils.replace(base64Str, "-", "+").replace("_", "/") : base64Str;
    }
}
