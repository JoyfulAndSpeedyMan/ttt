plugins {
    `java-platform`
    `maven-publish`
}

group = "com.bolingx"
version = "0.0.1"

// allow the definition of dependencies to other platforms like the Spring Boot BOM
javaPlatform.allowDependencies()

dependencies {
    val springBootVersion = "3.1.12"

    api(platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}"))
    constraints {
        // 基础库
        api("org.apache.commons:commons-lang3:3.12.0")
        api("org.apache.commons:commons-collections4:4.4")
        api("commons-io:commons-io:2.16.1")
        api("com.github.ben-manes.caffeine:caffeine:3.1.5")
        api("com.alibaba.fastjson2:fastjson2:2.0.41")

        // redis
        api("io.lettuce:lettuce-core:6.2.7.RELEASE")

        // 数据库
        val mybatisPlusVersion = "3.5.5"
        api("com.alibaba:druid:1.2.20")
        api("com.baomidou:mybatis-plus:${mybatisPlusVersion}")
        api("com.baomidou:mybatis-plus-annotation:${mybatisPlusVersion}")
        api("com.baomidou:mybatis-plus-boot-starter:${mybatisPlusVersion}")
        api("com.baomidou:mybatis-plus-generator:${mybatisPlusVersion}")
        api("com.baomidou:dynamic-datasource-spring-boot-starter:4.2.0")

        // 模板引擎
        api("org.freemarker:freemarker:2.3.32")

        // api
        api("org.hibernate.validator:hibernate-validator:8.0.1.Final")
        api("jakarta.validation:jakarta.validation-api:3.0.2")
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
        }
    }
}

