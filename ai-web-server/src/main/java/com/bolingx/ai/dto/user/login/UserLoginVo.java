package com.bolingx.ai.dto.user.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginVo {

    private UserLoginInfo userLoginInfo;

    private Boolean newUser;
}
