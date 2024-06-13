import com.bolingx.code.MybatisCodeGenerateTask

plugins {
    id("com.bolingx.java-lib")
    id("org.springframework.boot")
}

dependencies {
    annotationProcessor(platform("com.bolingx:product-platform"))

    implementation("org.springframework.boot:spring-boot-starter-logging")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.register<MybatisCodeGenerateTask>("codeGenerate") {
    configFile = "generator-config.yml"
}
