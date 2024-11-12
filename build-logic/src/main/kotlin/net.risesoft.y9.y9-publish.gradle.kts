plugins {
    id("maven-publish")
    id("signing")
    id("net.risesoft.y9.y9-repository")
}

publishing {
    publications {
        repositories {
            maven {
                val releasesRepoUrl = uri("https://svn.youshengyun.com:9900/nexus/repository/maven-releases/")
                val snapshotsRepoUrl = uri("https://svn.youshengyun.com:9900/nexus/repository/maven-snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = findProperty("mavenUsername") as String? ?: ""
                    password  = findProperty("mavenPassword") as String? ?: ""
                }
            }
        }
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = project.name
            pom {
                // 设置打包类型为pom
                //packaging = "pom"
                //name = "y9-digitalbase-dependencies"
                //description = "RiseSoft/Digital Infrastructure dependencies"
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

signing {
    if (project.hasProperty("isSigned") && project.property("isSigned") == true) {
        useGpgCmd()
        sign(publishing.publications["mavenJava"])
    }
}


