package com.bolingx.erp.config.beans;

import com.bolingx.common.encrypt.EncryptUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static com.bolingx.common.encrypt.EncryptUtil.AES_ECB_PKCS5Padding;
import static com.bolingx.common.encrypt.EncryptUtil.ALGORITHM_AES;

@Configuration
public class EncryptBeans {

    @Bean
    public EncryptUtil encryptUtil() {
        byte[] keyBytes = "mJT!w*kk&jfkXe5G".getBytes(StandardCharsets.UTF_8);
        SecretKeySpec key = new SecretKeySpec(keyBytes, ALGORITHM_AES);
        return EncryptUtil.useCipherPair(AES_ECB_PKCS5Padding, key);
    }

}
