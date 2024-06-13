package com.bolingx.common.model.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebugConfig {

    private boolean corsAllOrigin = false;

    private boolean enableDetailedErrorMessages = false;

}
