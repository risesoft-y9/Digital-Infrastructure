plugins {
    id("maven-publish")
    id("signing")
    id("net.risesoft.y9.y9-repository")
}

var PROJECT_GIT_URL = "https://gitee.com/risesoft-y9/y9-core"
val PROJECT_SCM_URL = "scm:git:${PROJECT_GIT_URL}.git"

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
                name = project.name
                description = "RiseSoft/Digital Infrastructure： " + project.name
                url = PROJECT_GIT_URL
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
                    connection = PROJECT_SCM_URL
                    developerConnection = PROJECT_SCM_URL
                    url = PROJECT_GIT_URL
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


