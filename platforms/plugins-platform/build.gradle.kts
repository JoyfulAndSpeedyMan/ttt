plugins {
    `java-platform`
    `maven-publish`
}

group = "com.bolingx"
version = "0.0.1"

dependencies {
    constraints {
        api("io.spring.gradle:dependency-management-plugin:1.1.4")
        api("org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:0.9.27")
        api("org.springframework.boot:org.springframework.boot.gradle.plugin:3.2.2")
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
        }
    }
}
