import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("application")
    id("com.google.cloud.tools.jib")
}

val myDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
val dateTimeStr = myDateTimeFormatter.format(LocalDateTime.now())
println("dateTimeStr" + dateTimeStr)

jib {
    from {
        image = "docker-internal.youshengyun.com/tomcat:10.1-jre21-temurin"
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
        image = "docker-registry-internal.youshengyun.com/${project.name}"
        auth {
            username = findProperty("dockerUsername").toString()
            password = findProperty("dockerPassword").toString()
        }
        tags = setOf("v9.7.0-SNAPSHOT", "v9.7.x", "v9.7.0-SNAPSHOT-${dateTimeStr}")
    }
}