import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost
import java.util.*

plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
    id("com.vanniktech.maven.publish") version "0.30.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.com.google.cloud.tools.jib.gradle.plugin)
    implementation(libs.com.ly.smart.doc.smart.doc.gradle.plugin)
    implementation(libs.io.freefair.gradle.aspectj.plugin)
    implementation(libs.io.freefair.gradle.lombok.plugin)
    implementation(libs.tech.yanand.gradle.maven.central.publish)
    implementation(libs.com.vanniktech.gradle.maven.publish.plugin)
    implementation(libs.org.jreleaser.jreleaser.gradle.plugin)
    implementation(libs.org.springframework.boot.org.springframework.boot.gradle.plugin)
}

group = "net.risesoft.y9"

val rootProjectProperties = Properties().apply { load(file("../gradle.properties").inputStream()) }
version = rootProjectProperties.get("Y9PLUGIN_VERSION") as String? ?: "9.7.0-01"

signing {
    //useGpgCmd()
    val signingInMemoryKey: String? by project
    val signingInMemoryKeyId: String? by project
    val signingInMemoryKeyPassword: String? by project
    useInMemoryPgpKeys(signingInMemoryKeyId, signingInMemoryKey, signingInMemoryKeyPassword)

    sign(publishing.publications)
}

mavenPublishing {
    configure(GradlePlugin(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = true
    ))
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
    signAllPublications()
    coordinates(project.group.toString(), project.name, project.version.toString())
    pom {
        name = project.name
        description = project.name
        url = "https://github.com/risesoft-y9/Digital-Infrastructure"
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
            connection = "scm:git:https://github.com/risesoft-y9/Digital-Infrastructure.git"
            developerConnection = "scm:git:https://github.com/risesoft-y9/Digital-Infrastructure.git"
            url = "https://github.com/risesoft-y9/Digital-Infrastructure"
        }
    }
}