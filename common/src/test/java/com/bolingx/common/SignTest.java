package com.bolingx.common;

import com.bolingx.common.util.sign.SignUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class SignTest {
    @Test
    public void testDefaultSignStrategy() {
        String appKey = "nea1234";
        String keySecret = "ignore8913";
        Map<String, String> map = Map.of(
                "p1", "v1",
                "p2", "v2",
                "p3", "v3");
        String sign = SignUtils.generateSignCode(map, appKey, keySecret);
        Assertions.assertEquals(sign, "727082F9A1FFBBDD");
    }

}
