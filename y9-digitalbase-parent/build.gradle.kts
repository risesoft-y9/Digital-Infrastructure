plugins {
    id("net.risesoft.y9.java-conventions")
    alias(libs.plugins.io.spring.dependency.management)
}
description = "y9-digitalbase-parent"

dependencyManagement {
    imports {
        mavenBom("net.risesoft:y9-digitalbase-dependencies:v9.7.0-SNAPSHOT")
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.3")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            // 设置打包类型为pom
            pom {
                packaging = "pom"
                name = "y9-digitalbase-parent"
                description = "RiseSoft/Digital Infrastructure dependencies parent"
                url = "https://gitee.com/risesoft-y9/y9-core/tree/main/y9-digitalbase-parent"
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