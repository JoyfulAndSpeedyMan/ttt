package com.bolingx.code

import com.baomidou.mybatisplus.generator.AutoGenerator
import com.baomidou.mybatisplus.generator.config.*
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Path

abstract class MybatisCodeGenerateTask : DefaultTask() {

    @get:Input
    abstract val configFile: Property<String>

    @TaskAction
    fun generate() {
        val generateConfig = parseConfig()
        Class.forName(generateConfig.driver)
        val dataSourceConfig: DataSourceConfig =
            DataSourceConfig.Builder(generateConfig.jdbcUrl, generateConfig.username, generateConfig.password)
                .build()
        val generator = AutoGenerator(dataSourceConfig).apply {
            val strategyConfig = StrategyConfig.Builder().run {
                entityBuilder().run {
                    enableLombok() // 使用lombok
                    if(generateConfig.entityOverride){
                        enableFileOverride()
                    }
                    formatFileName("%sEntity")
                }
                serviceBuilder().run {
                    formatServiceFileName("%sService")
                    if(generateConfig.serviceOverride){
                        enableFileOverride()
                    }
                }
                mapperBuilder().run {
                    if(generateConfig.mapperOverride){
                        enableFileOverride()
                    }
                }

                addInclude(generateConfig.tables)
                build()
            }

            val globalConfig = GlobalConfig.Builder().run {
                val pcUserName = System.getProperty("user.name")
                author(pcUserName)
                outputDir(findOutputPath().toAbsolutePath().toString())
                disableOpenDir()
                build()
            }

            val packageConfig = PackageConfig.Builder().run {
                parent(generateConfig.packageName)

                entity(generateConfig.entityPackage)
                service(generateConfig.servicePackage)
                serviceImpl(generateConfig.serviceImplPackage)
                mapper(generateConfig.mapperPackage)
                xml(generateConfig.xmlPackage)
                build()
            }

            val templateConfig = TemplateConfig.Builder().run {
                service(if (generateConfig.service) "/service.java" else "")
                serviceImpl(if (generateConfig.serviceImpl) "/serviceImpl.java" else "")
                entity(if (generateConfig.entity) "/entity.java" else "")

                if (!generateConfig.controller) {
                    controller("")
                }
                if (!generateConfig.mapper) {
                    mapper("")
                }
                if (!generateConfig.xml) {
                    xml("")
                }
                build()
            }

            strategy(strategyConfig)
            global(globalConfig)
            packageInfo(packageConfig)
            template(templateConfig)
        }
        val templateEngine = FreemarkerTemplateEngine()
        generator.execute(templateEngine)
    }

    private fun getSourceClassPath(): Path? {
        val sourceSet = getMainSourceSet();
        for (srcDir in sourceSet.resources.srcDirs) {
            val file = File(srcDir, configFile.get())
            if (file.exists()) {
                return file.toPath()
            }
        }
        return null;
    }

    private fun getMainSourceSet(): SourceSet {
        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
        return sourceSets.getByName("main")
    }

    private fun findOutputPath(): Path {
        val sourceSet = getMainSourceSet();
        for (srcDir in sourceSet.java.srcDirs) {
            return srcDir.toPath();
        }
        throw RuntimeException("未找到合适的输出的路径")
    }

    private fun parseConfig(): GenerateConfig {
        val sourceSet = getMainSourceSet();
        for (srcDir in sourceSet.resources.srcDirs) {
            val file = File(srcDir, configFile.get())
            if (file.exists()) {
                val mapper = ObjectMapper(YAMLFactory())
                return mapper.readValue(file, GenerateConfig::class.java)
            }
        }
        throw RuntimeException("没有可用的配置文件")
    }

}

class GenerateConfig {
    var jdbcUrl = ""
    var driver = "com.mysql.cj.jdbc.Driver"
    var username = ""
    var password = ""
    var tables: List<String> = arrayListOf()

    var packageName = "com.matugang"
    var entityPackage = "entity"
    var mapperPackage = "mapper"
    var xmlPackage = "mapper.xml"
    var servicePackage = "service"
    var serviceImplPackage = "service.impl"

    var controller = false
    var service = true
    var serviceImpl = true
    var entity = true
    var mapper = true
    var xml = false

    var entityOverride = true
    var serviceOverride = false
    var mapperOverride = false
}