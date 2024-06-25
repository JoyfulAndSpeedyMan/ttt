package com.bolingx.ai.dto.user.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginInfo {

    private String username;

    private String nickname;

    private Byte tempRole;

    private String avatar;

    /**
     * 性别，0: 未知；1:男性；2:女性
     */
    private Byte gender;

    private String token;

    private Long tokenExpireTime;

}
