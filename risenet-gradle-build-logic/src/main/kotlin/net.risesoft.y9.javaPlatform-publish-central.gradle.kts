import com.vanniktech.maven.publish.JavaPlatform
import com.vanniktech.maven.publish.SonatypeHost
import gradle.kotlin.dsl.accessors._19f2be23dfd768f9a4176b0c90f1941e.mavenPublishing

plugins {
    id("net.risesoft.y9.javaPlatform-publish")
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    configure(JavaPlatform())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
    signAllPublications()
    coordinates(project.group.toString(), project.name, project.extra.get("Y9BOM_VERSION") as String)
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