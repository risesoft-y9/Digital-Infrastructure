plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.4")
    implementation("com.ly.smart-doc:smart-doc-gradle-plugin:3.0.8")
    implementation("io.freefair.gradle:aspectj-plugin:8.11")
    implementation("io.freefair.gradle:lombok-plugin:8.11")
    implementation("org.jreleaser:jreleaser-gradle-plugin:1.15.0")
}

group = "net.risesoft.y9"
version = "v9.7.0-SNAPSHOT"

/*gradlePlugin {
    plugins.create("ManagementConfigurationPlugin") {
        id = "net.risesoft.y9.management"
        implementationClass = "net.risesoft.y9.management.ManagementConfigurationPlugin"
    }

    website.set("https://svn.youshengyun.com:3000/risesoft/y9-build-logic")
    vcsUrl.set("https://svn.youshengyun.com:3000/risesoft/y9-build-logic.git")
}*/

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

