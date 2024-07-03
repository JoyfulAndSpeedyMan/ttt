import com.google.protobuf.gradle.id

plugins {
    id("com.bolingx.java-lib")
    id("com.google.protobuf") version "0.9.4"
}
group = "com.bolingx.grpc"
version = "1.0-SNAPSHOT"

dependencies {
    api("org.apache.commons:commons-lang3")
    api("org.apache.commons:commons-collections4")
    api("commons-io:commons-io")

    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-api")
    api("io.grpc:grpc-netty")

    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
}

protobuf{
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }

    plugins {
        id("grpc"){
            artifact = "io.grpc:protoc-gen-grpc-java:1.64.0"
        }
    }

    generateProtoTasks{
        all().configureEach { plugins.id("grpc") }
    }
}
