plugins {
    `kotlin-dsl`
}

repositories{
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("com.bolingx:plugins-platform"))
    api(project(":code"))
    implementation("io.spring.gradle:dependency-management-plugin")
    implementation("org.springframework.boot:org.springframework.boot.gradle.plugin")
}