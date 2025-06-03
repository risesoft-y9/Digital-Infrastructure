import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `maven-publish`
    signing
    `java-library`
    id("net.risesoft.y9.repository")
    id("com.vanniktech.maven.publish")
}

publishing {
    publications {
        repositories {
            maven {
                name = "y9nexus"
                val releasesRepoUrl = uri("https://svn.youshengyun.com:9900/nexus/repository/maven-releases/")
                val snapshotsRepoUrl = uri("https://svn.youshengyun.com:9900/nexus/repository/maven-snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = providers.gradleProperty("mavenUsername").get()
                    password  = providers.gradleProperty("mavenPassword").get()
                }
            }
        }
    }
}

signing {
    //useGpgCmd()
    val signingInMemoryKey: String? by project
    val signingInMemoryKeyId: String? by project
    val signingInMemoryKeyPassword: String? by project
    useInMemoryPgpKeys(signingInMemoryKeyId, signingInMemoryKey, signingInMemoryKeyPassword)

    if (!(version.toString().endsWith("SNAPSHOT"))) sign(publishing.publications)
}

mavenPublishing {
    configure(
        JavaLibrary(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true
        )
    )
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
    if (!(version.toString().endsWith("SNAPSHOT"))) signAllPublications()
    pom {
        name = project.name
        description = "RiseSoft/Digital Infrastructure " + project.name
        url = providers.gradleProperty("PROJECT_GIT_URL").get()
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
            connection = providers.gradleProperty("PROJECT_SCM_URL").get()
            developerConnection = providers.gradleProperty("PROJECT_SCM_URL").get()
            url = providers.gradleProperty("PROJECT_GIT_URL").get()
        }
    }
}

