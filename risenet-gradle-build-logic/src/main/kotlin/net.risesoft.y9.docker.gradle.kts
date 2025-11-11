import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("com.google.cloud.tools.jib")
}

val myDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
val dateTimeStr = myDateTimeFormatter.format(LocalDateTime.now())

interface Y9DockerPluginExtension {
    val appName: Property<String>
    val fromImage: Property<String>
    val toImage: Property<String>
}

val extension = project.extensions.create<Y9DockerPluginExtension>("y9Docker")
//extension.appName.convention("app")

jib {
    from {
        image = "docker.youshengyun.com/base/tomcat:10.1.48-jdk21-temurin"
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "docker-internal.youshengyun.com/v97x/${project.name}"
        auth {
            username = findProperty("dockerUsername").toString()
            password = findProperty("dockerPassword").toString()
        }
        tags = setOf("9.7.0-SNAPSHOT", "9.7.x", "9.7.0-SNAPSHOT-${dateTimeStr}")
    }
//    container{
//        appRoot = "/usr/local/tomcat/webapps/${extension.appName.get()}"
//    }
}

project.afterEvaluate {
    if (!extension.appName.isPresent) {
        throw GradleException("y9Docker.appName must be set.")
    }
    jib.container.appRoot = "/usr/local/tomcat/webapps/${extension.appName.get()}"

    if (extension.fromImage.isPresent) {
        jib.from.image = extension.fromImage.get()
    }

    if (extension.toImage.isPresent) {
        jib.to.image = extension.toImage.get()
    }
}
