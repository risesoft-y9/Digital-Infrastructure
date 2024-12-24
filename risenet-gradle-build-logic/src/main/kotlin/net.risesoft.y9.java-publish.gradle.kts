plugins {
    `maven-publish`
    signing
    java
    `java-library`
    id("net.risesoft.y9.repository")
}

publishing {
    publications {
        repositories {
            maven {
                name = "mavenStaging"
                url = uri(layout.buildDirectory.dir("staging-deploy"))
            }
            maven {
                name = "y9nexus"
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
                name = project.name
                description = "RiseSoft/Digital Infrastructure " + project.name
                url = findProperty("PROJECT_GIT_URL").toString()
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
                    developer {
                        name = "qinman"
                        email = "qinman@risesoft.net"
                    }
                    developer {
                        name = "mengjuhua"
                        email = "mengjuhua@risesoft.net"
                    }
                    developer {
                        name = "shidaobang"
                        email = "shidaobang@risesoft.net"
                    }
                }
                scm {
                    connection = findProperty("PROJECT_SCM_URL").toString()
                    developerConnection = findProperty("PROJECT_SCM_URL").toString()
                    url = findProperty("PROJECT_GIT_URL").toString()
                }
            }
        }
    }
}

signing {
    //useGpgCmd()
    val signingInMemoryKey: String? by project
    val signingInMemoryKeyId: String? by project
    val signingInMemoryKeyPassword: String? by project
    useInMemoryPgpKeys(signingInMemoryKeyId, signingInMemoryKey, signingInMemoryKeyPassword)

    sign(publishing.publications["mavenJava"])
}
