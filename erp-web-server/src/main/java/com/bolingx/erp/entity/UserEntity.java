package com.bolingx.erp.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 
 * </p>
 *
 * @author p
 * @since 2024-05-06
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

    private String mobile;

    private String nickname;

    private String avatar;

    /**
     * 性别，0: 未知；1:男性；2:女性
     */
    private Byte gender;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
