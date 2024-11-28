import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

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
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.4")
    implementation("com.ly.smart-doc:smart-doc-gradle-plugin:3.0.8")
    implementation("io.freefair.gradle:aspectj-plugin:8.11")
    implementation("io.freefair.gradle:lombok-plugin:8.11")
    implementation("tech.yanand.gradle:maven-central-publish:1.3.0")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.13.0")
}

group = "net.risesoft.y9"
version = "0.0.1"

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
        sourcesJar = true,
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