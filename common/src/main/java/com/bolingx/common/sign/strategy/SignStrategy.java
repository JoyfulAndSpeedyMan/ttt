package com.bolingx.common.sign.strategy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface SignStrategy {

    String getName();

    default String generateSignCode(Map<String, String> parameterMap, String appKey, String keySecret) {
        return generateSignCode(parameterMap, appKey, keySecret, Collections.emptySet());
    }

    default String generateSignCodeArrayMap(Map<String, String[]> parameterMap, String appKey, String keySecret) {
        return generateSignCodeArrayMap(parameterMap, appKey, keySecret, Collections.emptySet());
    }

    String generateSignCode(Map<String, String> parameterMap, String appKey, String keySecret, Set<String> ignoreParam);

    String generateSignCodeArrayMap(Map<String, String[]> parameterMap, String appKey, String keySecret, Set<String> ignoreParam);

    String generateSignCode(String signData, String appKey, String keySecret);
}
