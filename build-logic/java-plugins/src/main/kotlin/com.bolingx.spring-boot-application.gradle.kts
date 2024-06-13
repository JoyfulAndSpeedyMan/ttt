import com.bolingx.code.MybatisCodeGenerateTask
import gradle.kotlin.dsl.accessors._b5a19b0b4c30c7b2ccf6ef4cbc530f65.api

plugins {
    id("com.bolingx.java-lib")
    id("org.springframework.boot")
}

dependencies {
    implementation(platform("com.bolingx:product-platform"))
    annotationProcessor(platform("com.bolingx:product-platform"))

    implementation("org.springframework.boot:spring-boot-starter-logging")

//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
//    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.register<MybatisCodeGenerateTask>("codeGenerate") {
    configFile = "generator-config.yml"
}
