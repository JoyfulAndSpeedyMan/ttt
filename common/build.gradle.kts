plugins {
    id("com.bolingx.java-lib")
}
group = "com.bolingx"

dependencies {


    api("org.apache.commons:commons-lang3")
    api("org.apache.commons:commons-collections4")
    api("commons-io:commons-io")

    compileOnly("com.github.ben-manes.caffeine:caffeine")
    compileOnly("com.alibaba.fastjson2:fastjson2")

    compileOnly("com.baomidou:mybatis-plus-annotation")

    compileOnly("org.hibernate.orm:hibernate-core")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core")

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-web")
}

