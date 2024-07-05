package com.bolingx.ai.security.user;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailImplMixin {

    @JsonCreator
    public UserDetailImplMixin(@JsonProperty("id") Long id,
                               @JsonProperty("username") String username,
                               @JsonProperty("password") String password) {
    }
}
