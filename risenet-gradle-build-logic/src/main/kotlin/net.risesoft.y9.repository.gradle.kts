group = "net.risesoft"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()

    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }

    maven {
        url = uri("https://svn.youshengyun.com:9900/nexus/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.spring.io/snapshot")
    }

    maven {
        url = uri("https://repo.spring.io/milestone")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://build.shibboleth.net/maven/releases/")
    }

    maven {
        url = uri("https://build.shibboleth.net/maven/snapshots/")
    }

}
