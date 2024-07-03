plugins {
    id("com.bolingx.java-lib")
}
group = "com.bolingx.grpc"
version = "1.0-SNAPSHOT"
dependencies {
    api(project(":core"))
    implementation("ch.qos.logback:logback-classic")
}
