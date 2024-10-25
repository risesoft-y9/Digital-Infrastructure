plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.io.spring.dependency.management)
}
description = "y9-digitalbase-dependencies"

dependencyManagement {

    dependencies {
        dependency("com.alibaba:fastjson:2.0.49")
        dependency("com.github.oshi:oshi-core-java11:6.6.0")
        dependency("com.google.guava:guava:33.1.0-jre")
        dependency("commons-io:commons-io:2.13.0")
        dependency("commons-net:commons-net:3.10.0")
        dependency("cn.hutool:hutool-all:5.8.27")
        dependency("org.apache.commons:commons-lang3:3.13.0")
        dependency("org.apache.commons:commons-pool2:2.12.0")
        dependency("org.jodd:jodd-bean:5.1.6")
        dependency("org.jodd:jodd-mail:7.0.1")
        dependency("org.jodd:jodd-http:6.3.0")
        dependency("org.jodd:jodd-lagarto:6.0.6")
        dependencySet("org.apache.httpcomponents:4.5.14") {
            entry("httpmime")
            entry("httpclient") {
                exclude("commons-logging:commons-logging")
            }
        }
        dependencySet("org.javers:7.4.2") {
            entry("javers-spring-boot-starter-mongo")
            entry("javers-spring-boot-starter-sql")
        }
        dependency("org.dom4j:dom4j:2.1.4")
        dependencySet("org.aspectj:1.9.22") {
            entry("aspectjrt")
            entry("aspectjtools")
            entry("aspectjweaver")
        }
        dependency("org.liquibase:liquibase-core:4.27.0")
        dependencySet("net.javacrumbs.shedlock:5.13.0") {
            entry("shedlock-spring")
            entry("shedlock-provider-redis-spring")
            entry("shedlock-provider-jdbc-template")
        }
        dependency("com.mysql:mysql-connector-j:8.2.0")
        dependency("org.mariadb.jdbc:mariadb-java-client:3.1.4")
        dependency("com.oracle.database.jdbc:ojdbc11:21.5.0.0")
        dependency("org.postgresql:postgresql:42.7.3")
        dependency("com.dameng:DmDialect-for-hibernate5.6:8.1.3.140")
        dependency("com.dameng:DmJdbcDriver18:8.1.3.140")
        dependency("cn.com.kingbase:kingbase8:8.6.0")
        dependency("cn.com.kingbase:hibernate-dialect:5.4.6.Finaldialect")
        dependency("cn.com.kingbase:hibernate-jpa-api:2.1.0.Final")
        dependency("com.oceanbase:oceanbase-client:2.4.0")

        dependencySet("net.risesoft:v9.7.0-SNAPSHOT") {
            entry("risenet-y9boot-properties")
            entry("risenet-y9boot-common-model")
            entry("risenet-y9boot-common-util")
            entry("risenet-y9boot-common-tenant-datasource")
            entry("risenet-y9boot-3rd-jpa")
            entry("risenet-y9boot-common-model")
            entry("risenet-y9boot-common-sqlddl")
            entry("risenet-y9boot-idcode")
            entry("risenet-y9boot-starter-cache-redis")
            entry("risenet-y9boot-starter-idgenerator")
            entry("risenet-y9boot-starter-jpa-public")
            entry("risenet-y9boot-starter-kafka")
            entry("rrisenet-y9boot-starter-liquibase")
            entry("risenet-y9boot-starter-publish-kafka")
            entry("risenet-y9boot-starter-listener-kafka")
            entry("risenet-y9boot-starter-multi-tenant")
            entry("risenet-y9boot-starter-log")
            entry("risenet-y9boot-starter-permission")
            entry("risenet-y9boot-starter-security")
            entry("risenet-y9boot-starter-sso-oauth2-resource")
            entry("risenet-y9boot-starter-elasticsearch")
            entry("risenet-y9boot-starter-web")
            entry("risenet-y9boot-starter-openfeign")
            entry("risenet-y9boot-support-file-jpa-repository")
            entry("risenet-y9boot-support-file-service-ftp")
            entry("risenet-y9boot-support-file-service-local")
            entry("risenet-y9boot-support-file-service-rest")
            entry("risenet-y9boot-support-history")
            entry("risenet-y9boot-api-feignclient-log")
            entry("risenet-y9boot-api-interface-log")
            entry("risenet-y9boot-support-log-elasticsearch-repository")
            entry("risenet-y9boot-support-log-jpa-repository")
            entry("risenet-y9boot-support-log-service")
            entry("risenet-y9boot-support-log-web")
            entry("risenet-y9boot-api-feignclient-platform")
            entry("risenet-y9boot-api-interface-platform")
            entry("risenet-y9boot-support-platform-jpa-repository")
            entry("risenet-y9boot-support-platform-service")
            entry("risenet-y9boot-support-platform-web")
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            // 设置打包类型为pom
            pom {
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