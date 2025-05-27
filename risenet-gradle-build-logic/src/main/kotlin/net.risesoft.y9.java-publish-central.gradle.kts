import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("net.risesoft.y9.java-publish")
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    configure(JavaLibrary(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = true,
    ))
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
