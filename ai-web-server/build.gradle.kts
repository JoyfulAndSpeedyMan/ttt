
plugins {
    id("com.bolingx.spring-boot-application")
}

group = "com.bolingx"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
//    implementation(platform("com.bolingx:product-platform"))
    implementation("com.bolingx:common")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.session:spring-session-data-redis")

    implementation("io.lettuce:lettuce-core")

    implementation("com.bolingx.grpc:core")
    runtimeOnly("io.grpc:grpc-netty-shaded:1.64.0")

    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-collections4")
    implementation("com.alibaba.fastjson2:fastjson2")
    implementation("com.github.ben-manes.caffeine:caffeine")

    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("com.alibaba:druid")
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter")

    implementation("org.hibernate.validator:hibernate-validator")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    exclude("logback-test-spring.xml")
    exclude("generator.yml")
}