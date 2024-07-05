package com.bolingx.ai.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailDto {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    /**
     * 性别，0: 未知；1:男性；2:女性
     */
    private Byte gender;


    private String email;

    /**
     * 默认打码
     */
    private String mobile;
}
