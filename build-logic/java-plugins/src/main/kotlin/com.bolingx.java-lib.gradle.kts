import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.`maven-publish`

plugins {
    java
    id("io.spring.dependency-management")
    `java-library`
    `maven-publish`
}
group = "com.bolingx"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

dependencies {
    api(platform("com.bolingx:product-platform"))

    api("org.slf4j:slf4j-api")

    annotationProcessor(platform("com.bolingx:product-platform"))
    testAnnotationProcessor(platform("com.bolingx:product-platform"))

    val lombokPos = "org.projectlombok:lombok"
    compileOnly(lombokPos)
    annotationProcessor(lombokPos)
    testCompileOnly(lombokPos)
    testAnnotationProcessor(lombokPos)

    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("jar") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}
tasks.test {
    useJUnitPlatform()
}
