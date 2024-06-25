package com.bolingx.ai.util.spring;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class EnvUtils {
    private Environment environment;

    private boolean isProduction;

    @PostConstruct
    public void init() {
        Profiles prod = Profiles.of("prod");
        isProduction = environment.acceptsProfiles(prod);
    }

    public boolean isProduction() {
        return isProduction;
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
