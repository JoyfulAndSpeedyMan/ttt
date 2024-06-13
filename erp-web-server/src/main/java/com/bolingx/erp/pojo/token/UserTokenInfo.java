package com.bolingx.erp.pojo.token;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTokenInfo {
    private String version;
    private String platform;
    private long uid;
    private long tokenExpireTime;
    private long tokenValidTime;
    private String token;
}
