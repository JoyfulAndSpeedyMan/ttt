package com.bolingx.erp.entity;

import java.io.Serial;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;
import com.alibaba.fastjson2.JSON;
/**
 * <p>
 * 秘钥
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
@TableName("app_secret")
public class AppSecretEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 平台id
     */
    private String platformId;

    private String appKey;

    private String secret;

    private String algorithm;

    /**
     * 1: 有效
     */
    private Byte status;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
