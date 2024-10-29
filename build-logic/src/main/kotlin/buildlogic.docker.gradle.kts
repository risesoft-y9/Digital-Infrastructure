import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("com.google.cloud.tools.jib")
}

val myDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
val dateTimeStr = myDateTimeFormatter.format(LocalDateTime.now())

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
            //username = findProperty("dockerUsername")
            //password = findProperty("dockerPassword")
        }
        tags = setOf("v9.7.0-SNAPSHOT", "v9.7.x", "v9.7.0-SNAPSHOT-${dateTimeStr}")
    }
}