import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import java.util.*

plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
    id("com.vanniktech.maven.publish") version "0.34.0"
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
    implementation(libs.com.vanniktech.gradle.maven.publish.plugin)
    implementation(libs.org.springframework.boot.org.springframework.boot.gradle.plugin)
}

group = "net.risesoft.y9"

val props = Properties().apply { load(file("../gradle.properties").inputStream()) }
version = props.get("Y9PLUGIN_VERSION").toString()

mavenPublishing {
    configure(GradlePlugin(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = true
    ))
    coordinates(project.group.toString(), project.name, project.version.toString())
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()
    pom {
        name = project.name
        description = "RiseSoft/Digital Infrastructure " + project.name
        url = props.get("PROJECT_GIT_URL").toString()
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
            connection = props.get("PROJECT_SCM_URL").toString()
            developerConnection = props.get("PROJECT_SCM_URL").toString()
            url = props.get("PROJECT_GIT_URL").toString()
        }
    }
}