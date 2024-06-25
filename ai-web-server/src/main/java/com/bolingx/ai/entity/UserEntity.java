package com.bolingx.ai.entity;

import java.io.Serial;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;
import com.alibaba.fastjson2.JSON;
/**
 * <p>
 * 
 * </p>
 *
 * @author p
 * @since 2024-6-14
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private String mobile;

    private String nickname;

    private String avatar;

    /**
     * 性别，0: 未知；1:男性；2:女性
     */
    private Byte gender;

    /**
     * 临时的角色字段，判断是不是管理员，早晚得去掉
     */
    private Byte tempRole;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
