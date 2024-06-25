package com.bolingx.ai.util.user;

import com.bolingx.common.encrypt.EncryptUtil;
import com.bolingx.ai.dto.user.login.UserTokenInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTokenUtils {

    private EncryptUtil encryptUtil;

    private final long TOKEN_VALID_TIME = 180L * 24 * 60 * 60 * 1000;


    public UserTokenInfo createUserToken(String platform, long uid) {
        long cur = System.currentTimeMillis();
        long tokenExpireTime = cur + TOKEN_VALID_TIME;
        UserTokenInfo tokenInfo = UserTokenInfo.builder()
                .version("1")
                .platform(platform)
                .uid(uid)
                .tokenExpireTime(tokenExpireTime)
                .tokenValidTime(TOKEN_VALID_TIME)
                .build();
        String tokenRawString = StringUtils.joinWith(":",
                tokenInfo.getVersion(),
                tokenInfo.getPlatform(),
                tokenInfo.getUid(),
                tokenInfo.getTokenExpireTime(),
                tokenInfo.getTokenValidTime());
        String token = encryptUtil.createSecret(tokenRawString);
        tokenInfo.setToken(token);
        return tokenInfo;
    }

    public UserTokenInfo parseUserToken(String token) {
        String[] strings = encryptUtil.parseSecret(token);
        return UserTokenInfo.builder()
                .version(strings[0])
                .platform(strings[1])
                .uid(Long.parseLong(strings[2]))
                .tokenExpireTime(Long.parseLong(strings[3]))
                .tokenValidTime(Long.parseLong(strings[4]))
                .token(token)
                .build();
    }

    @Autowired
    public void setEncryptUtil(EncryptUtil encryptUtil) {
        this.encryptUtil = encryptUtil;
    }
}
