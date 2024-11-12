import org.jreleaser.gradle.plugin.tasks.JReleaserDeployTask
import org.jreleaser.model.Active
import org.jreleaser.model.Http
import org.jreleaser.model.Signing
import org.jreleaser.model.api.deploy.maven.MavenCentralMavenDeployer

plugins {
    `java-library`
    `maven-publish`
    id("org.jreleaser")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }

    publications.create<MavenPublication>("mavenCentral") {
        from(components["java"])
        pom {
            name = project.name
            description = project.description
            url = "https://svn.youshengyun.com:3000/risesoft/y9demo-maven-central"
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
                connection = "scm:git:https://svn.youshengyun.com:3000/risesoft/y9demo-maven-central.git"
                developerConnection = "scm:git:https://svn.youshengyun.com:3000/risesoft/y9demo-maven-central.git"
                url = "https://svn.youshengyun.com:3000/risesoft/y9demo-maven-central"
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

tasks.withType<JavaCompile> {
    //sourceCompatibility = JavaVersion.VERSION_21.toString()
    //targetCompatibility = JavaVersion.VERSION_21.toString()
    options.encoding = "UTF-8"
    val compilerArgs = options.compilerArgs
    compilerArgs.add("-parameters")
    compilerArgs.add("-Xlint:all")
    compilerArgs.add("-Xdiags:verbose")
}

tasks.withType<Javadoc> {
    //enabled = false
    isFailOnError = false
    val doclet = options as StandardJavadocDocletOptions
    doclet.encoding = "UTF-8"
    doclet.docEncoding = "UTF-8"
    doclet.addBooleanOption("html5", true)
    doclet.addStringOption("Xdoclint:all,-missing", "-quiet")
    //doclet.addStringOption("Xdoclint:none", "-quiet")  // 禁用所有文档检查
}

tasks.withType<JReleaserDeployTask> {
    dependsOn(tasks.named("publish").get())
}
