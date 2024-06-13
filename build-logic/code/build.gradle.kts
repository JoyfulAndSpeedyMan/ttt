plugins {
    `kotlin-dsl`
}

group = "com.bolingx"
repositories{
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("com.bolingx:product-platform"))

    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-collections4")

    implementation("com.baomidou:mybatis-plus-generator")
    implementation("com.baomidou:mybatis-plus")
    implementation("org.freemarker:freemarker")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
}
