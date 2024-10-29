group = "net.risesoft"
version = "v9.7.0-SNAPSHOT"

repositories {
    mavenLocal()

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
