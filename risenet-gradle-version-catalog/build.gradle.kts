import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.VersionCatalog
import java.util.*

plugins {
    signing
    id("version-catalog")
    id("com.vanniktech.maven.publish") version "0.32.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

catalog {
    versionCatalog {
        //from(files("libs.versions.toml"))
        from(files("../gradle/libs.versions.toml"))
    }
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
    configure(VersionCatalog())
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
