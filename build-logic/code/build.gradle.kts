plugins {
    `kotlin-dsl`
}

group = "com.bolingx"
repositories{
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(platform("com.bolingx:product-platform"))

    api("org.apache.commons:commons-lang3")
    api("org.apache.commons:commons-collections4")

    api("com.baomidou:mybatis-plus-generator")
    api("com.baomidou:mybatis-plus")
    api("org.freemarker:freemarker")
    runtimeOnly("com.mysql:mysql-connector-j")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
}
