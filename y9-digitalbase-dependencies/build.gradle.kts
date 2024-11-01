plugins {
    id("java-platform")
    id("maven-publish")
}

javaPlatform {
    allowDependencies()
}

description = "y9-digitalbase-dependencies"
group = "net.risesoft"
version = libs.versions.y9.version.get()

dependencies {
    //bom
    api(platform(libs.spring.boot.bom))
    api(platform(libs.spring.cloud.bom))

    constraints {
        // y9 component
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.common.model)
        api(libs.risenet.y9boot.common.util)
        api(libs.risenet.y9boot.common.tenant.datasource)
        api(libs.risenet.y9boot.third.jpa)
        api(libs.risenet.y9boot.common.sqlddl)
        api(libs.risenet.y9boot.idcode)
        api(libs.risenet.y9boot.starter.cache.redis)
        api(libs.risenet.y9boot.starter.idgenerator)
        api(libs.risenet.y9boot.starter.jpa.public)
        api(libs.risenet.y9boot.starter.kafka)
        api(libs.risenet.y9boot.starter.liquibase)
        api(libs.risenet.y9boot.starter.publish.kafka)
        api(libs.risenet.y9boot.starter.listener.kafka)
        api(libs.risenet.y9boot.starter.multi.tenant)
        api(libs.risenet.y9boot.starter.log)
        api(libs.risenet.y9boot.starter.permission)
        api(libs.risenet.y9boot.starter.security)
        api(libs.risenet.y9boot.starter.sso.oauth2.resource)
        api(libs.risenet.y9boot.starter.elasticsearch)
        api(libs.risenet.y9boot.starter.web)
        api(libs.risenet.y9boot.starter.openfeign)
        api(libs.risenet.y9boot.support.file.jpa.repository)
        api(libs.risenet.y9boot.support.file.service.ftp)
        api(libs.risenet.y9boot.support.file.service.local)
        api(libs.risenet.y9boot.support.file.service.rest)
        api(libs.risenet.y9boot.support.history)
        api(libs.risenet.y9boot.api.feignclient.log)
        api(libs.risenet.y9boot.apiinterface.log)
        api(libs.risenet.y9boot.support.log.elasticsearch.repository)
        api(libs.risenet.y9boot.support.log.jpa.repository)
        api(libs.risenet.y9boot.support.log.service)
        api(libs.risenet.y9boot.support.log.web)
        api(libs.risenet.y9boot.api.feignclient.platform)
        api(libs.risenet.y9boot.apiinterface.platform)
        api(libs.risenet.y9boot.support.platform.jpa.repository)
        api(libs.risenet.y9boot.support.platform.service)
        api(libs.risenet.y9boot.support.platform.web)

        // Third Party
        api(libs.cn.hutool.hutool.all)

    }
}

publishing {
    publications {
        repositories {
            maven {
                val releasesRepoUrl = uri("https://svn.youshengyun.com:9900/nexus/repository/maven-releases/")
                val snapshotsRepoUrl = uri("https://svn.youshengyun.com:9900/nexus/repository/maven-snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = findProperty("mavenUsername").toString()
                    password  = findProperty("mavenPassword").toString()
                }
            }
        }
        create<MavenPublication>("mavenJavaPlatform") {
            from(components["javaPlatform"])
            artifactId = "y9-digitalbase-dependencies"
            pom {
                // 设置打包类型为pom
                packaging = "pom"
                name = "y9-digitalbase-dependencies"
                description = "RiseSoft/Digital Infrastructure dependencies"
                url = "https://gitee.com/risesoft-y9/y9-core/tree/main/y9-digitalbase-dependencies"
                licenses {
                    license {
                        name = "GNU General Public License (GPL) version 3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                    }
                }
                developers {
                    developer {
                        name = "dingzhaojun"
                        email = "dingzhaojun@risesoft.net"
                    }
                }
                scm {
                    connection = "scm:git:https://gitee.com/risesoft-y9/y9-core.git"
                    developerConnection = "scm:git:https://gitee.com/risesoft-y9/y9-core.git"
                    url = "https://gitee.com/risesoft-y9/y9-core"
                }
            }
        }
    }
}
