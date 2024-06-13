package com.bolingx.common.web.servlet.filter;


import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RequestValidationConfig {

    private String errorPage = "/error";

    private Set<String> ignorePathSet = new HashSet<>();
}
