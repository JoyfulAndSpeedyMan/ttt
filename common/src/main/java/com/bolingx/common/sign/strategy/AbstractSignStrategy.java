package com.bolingx.common.sign.strategy;

import com.bolingx.common.encrypt.DigestUtil;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractSignStrategy implements SignStrategy {

    protected static final Set<String> ignoreSignParam = Set.of("sign");

    protected DigestUtil digestUtil = DigestUtil.useAlgorithm(DigestUtil.MD5);

    @Override
    public String generateSignCode(Map<String, String> parameterMap, String appKey, String keySecret, Set<String> ignoreParam) {
        return generateSignCode(parameterMap, appKey, keySecret, ignoreParam, parameterMap::get);
    }

    @Override
    public String generateSignCodeArrayMap(Map<String, String[]> parameterMap, String appKey, String keySecret, Set<String> ignoreParam) {
        return generateSignCode(parameterMap, appKey, keySecret, ignoreParam, k -> {
            String[] vs = parameterMap.get(k);
            if (vs == null || vs.length == 0) {
                return null;
            }
            return vs[0];
        });
    }

    protected String generateSignCode(Map<String, ?> parameterMap, String appKey, String keySecret, Set<String> ignoreParam, Function<String, String> valueGetter) {
        parameterMap = new HashMap<>(parameterMap);
        for (String s : ignoreSignParam) {
            parameterMap.remove(s);
        }
        for (String s : ignoreParam) {
            parameterMap.remove(s);
        }

        // 去除空值
        LinkedList<String> emptyValueList = new LinkedList<>();
        for (String k : parameterMap.keySet()) {
            String v = valueGetter.apply(k);
            if (v == null) {
                emptyValueList.add(k);
            }
        }
        for (String ek : emptyValueList) {
            parameterMap.remove(ek);
        }

        // 拼接body体
        ArrayList<String> keyList = new ArrayList<>(parameterMap.keySet());
        keyList.sort(String::compareTo);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyList.size(); i++) {
            String k = keyList.get(i);
            sb.append(k);
            sb.append(getEqualSymbol());
            sb.append(valueGetter.apply(k));
            if (i != keyList.size() - 1) {
                sb.append(getSeparatorSymbol());
            }
        }
        return generateSignCode(sb.toString(), appKey, keySecret);
    }

    @Override
    public String generateSignCode(String signData, String appKey, String keySecret) {
        signData = appKey + signData + keySecret;
        String md5 = digestUtil.digestToHex(signData);
        String sb = md5.substring(15, 18) +
                md5.substring(0, 4) +
                md5.substring(8, 13) +
                md5.substring(26, 30);
        return sb.toUpperCase();
    }

    protected abstract String getEqualSymbol();

    protected abstract String getSeparatorSymbol();
}
