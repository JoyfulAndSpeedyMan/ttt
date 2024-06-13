package com.bolingx.common.web.secret;

import com.alibaba.fastjson2.JSON;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 人群站秘钥
 * </p>
 *
 * @author p
 * @since 2023-12-15
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppSecret implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final byte STATUS_NORMAL = 1;

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
