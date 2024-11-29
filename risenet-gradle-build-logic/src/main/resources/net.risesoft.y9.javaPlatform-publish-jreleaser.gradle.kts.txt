@file:Suppress("SpellCheckingInspection")

import org.jreleaser.gradle.plugin.tasks.JReleaserDeployTask
import org.jreleaser.model.Active
import org.jreleaser.model.Http
import org.jreleaser.model.Signing
import org.jreleaser.model.api.deploy.maven.MavenCentralMavenDeployer

plugins {
    `java-platform`
    `maven-publish`
    id("net.risesoft.y9.repository")
    id("org.jreleaser")
}

// 在gradle.properties中定义
//extra.set("PROJECT_GIT_URL", "https://github.com/risesoft-y9/Digital-Infrastructure")
//extra.set("PROJECT_SCM_URL", "scm:git:https://github.com/risesoft-y9/Digital-Infrastructure.git")

publishing {
    repositories {
        maven {
            name = "mavenStaging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }

    publications.create<MavenPublication>("mavenJavaPlatform") {
        from(components["javaPlatform"])
        pom {
            packaging = "pom"
            name = project.name
            description = project.name
            // 不知道为什么，project.description为null，project.version获取不到
            version = findProperty("Y9BOM_VERSION").toString()
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

jreleaser {
    //gitRootSearch = true
    dependsOnAssemble = true

    project {
        copyright = "Copyright © 2023-2024 risesoft. All rights reserved."
        author("risesoft")
    }

    signing {
        active = Active.ALWAYS
        armored = true
        artifacts = true
        verify = false   // Verify signature files，设置为false就可以不设置publicKey，只用secretKey加密
        checksums = true
        mode = Signing.Mode.MEMORY
        passphrase = findProperty("signingInMemoryKeyPassword") as String? ?: System.getenv("GPG_PASSPHRASE")
        //publicKey = findProperty("signingInMemoryPublicKey") as String? ?: System.getenv("GPG_PUBLIC_KEY")
        secretKey = findProperty("signingInMemoryKey") as String? ?: System.getenv("GPG_SECRET_KEY")

    }

    deploy {
        maven {
            mavenCentral {
                create("y9MavenCentral") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                    snapshotSupported = false
                    authorization = Http.Authorization.BEARER
                    username = findProperty("mavenCentralUsername") as String? ?: System.getenv("MAVENCENTRAL_USERNAME")
                    password = findProperty("mavenCentralPassword") as String? ?: System.getenv("MAVENCENTRAL_PASSWORD")
                    sign = true
                    applyMavenCentralRules = true

                    stage = MavenCentralMavenDeployer.Stage.FULL
                    namespace = "net.risesoft"
                    deploymentId = "{{projectJavaArtifactId}}-{{projectJavaVersion}}"
                    connectTimeout = 20
                    readTimeout = 60
                    retryDelay = 30
                    maxRetries = 60
                }
            }
        }
    }
}

tasks.withType<JReleaserDeployTask> {
    dependsOn(tasks.named("publish").get())
    //excludedDeployerNames = listOf("risenet-y9demo-cache","risenet-y9demo-data-jpa")
}
