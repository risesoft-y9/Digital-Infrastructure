plugins {
    `maven-publish`
    signing
    `java-platform`
    id("net.risesoft.y9.repository")
}

val versionCatalog = versionCatalogs.named("libs")
val y9version = versionCatalog.findVersion("y9-version")
if(y9version.isPresent) {
    version = y9version.get().displayName
} else {
    version = "v9.7.0-SNAPSHOT"
}

extra.set("PROJECT_GIT_URL", "https://github.com/risesoft-y9/Digital-Infrastructure")
extra.set("PROJECT_SCM_URL", "scm:git:https://github.com/risesoft-y9/Digital-Infrastructure.git")

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
        create<MavenPublication>("mavenJavaPlatform") {
            from(components["javaPlatform"])
            artifactId = project.name
            pom {
                // 设置打包类型为pom
                packaging = "pom"
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
    if (project.hasProperty("isSigned") && project.property("isSigned") == true) {
        useGpgCmd()
        sign(publishing.publications["mavenJavaPlatform"])
    }
}


