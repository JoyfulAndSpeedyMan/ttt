package com.bolingx.seller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyUserInfoDto {

    private String username;

    private String nickname;

    /**
     * 性别，0: 未知；1:男性；2:女性
     */
    private Byte gender;
}
