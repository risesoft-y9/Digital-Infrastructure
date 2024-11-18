@file:Suppress("SpellCheckingInspection")

import gradle.kotlin.dsl.accessors._7b186f4cc9ec3db7e784e2bd695d97b7.versionCatalogs
import gradle.kotlin.dsl.accessors._c6e11ff2df00e6849127de4bac6afe79.ext
import gradle.kotlin.dsl.accessors._c6e11ff2df00e6849127de4bac6afe79.java
import gradle.kotlin.dsl.accessors._c6e11ff2df00e6849127de4bac6afe79.jreleaser
import gradle.kotlin.dsl.accessors._c6e11ff2df00e6849127de4bac6afe79.publishing
import org.jreleaser.gradle.plugin.tasks.JReleaserDeployTask
import org.jreleaser.model.Active
import org.jreleaser.model.Http
import org.jreleaser.model.Signing
import org.jreleaser.model.api.deploy.maven.MavenCentralMavenDeployer

plugins {
    `java-platform`
    `maven-publish`
    id("org.jreleaser")
}

val versionCatalog = versionCatalogs.named("libs")
val y9version = versionCatalog.findVersion("y9-version")
if(y9version.isPresent) {
    version = y9version.get().displayName
} else {
    version = "v9.7.0-SNAPSHOT"
}

ext.set("PROJECT_GIT_URL", "https://github.com/risesoft-y9/Digital-Infrastructure")
ext.set("PROJECT_SCM_URL", "scm:git:https://github.com/risesoft-y9/Digital-Infrastructure.git")

publishing {
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
                username = findProperty("mavenUsername").toString()
                password  = findProperty("mavenPassword").toString()
            }
        }
    }

    publications.create<MavenPublication>("mavenJavaPlatform") {
        from(components["javaPlatform"])
        pom {
            packaging = "pom"
            name = project.name
            description = project.description
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
    //dependsOnAssemble = true

    project {
        copyright = "Copyright © 2023-2024 risesoft. All rights reserved."
        author("risesoft")
    }

    signing {
        active = Active.ALWAYS
        armored = true
        artifacts = true
        verify = true
        checksums = true
        mode = Signing.Mode.FILE
        passphrase = findProperty("gpg.passphrase") as String? ?: System.getenv("GPG_PASSPHRASE")
        publicKey = findProperty("gpg.public.key") as String? ?: System.getenv("GPG_PUBLIC_KEY")
        secretKey = findProperty("gpg.secret.key") as String? ?: System.getenv("GPG_SECRET_KEY")

    }

    deploy {
        maven {
            mavenCentral {
                create("y9") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                    snapshotSupported = false
                    authorization = Http.Authorization.BEARER
                    username = findProperty("mavencentral.username") as String? ?: System.getenv("MAVENCENTRAL_USERNAME")
                    password = findProperty("mavencentral.password") as String? ?: System.getenv("MAVENCENTRAL_PASSWORD")
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
