group = "net.risesoft"

repositories {
    mavenLocal()
    /*maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }*/
    // 主要稳定版本来源（最高优先级）
    mavenCentral()
    //  Gradle 插件仓库
    gradlePluginPortal()

    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }

    maven {
        url = uri("https://svn.youshengyun.com:9900/nexus/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.spring.io/snapshot")
    }

    // maven {
    //     url = uri("https://repo.spring.io/milestone")
    // }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    //   Shibboleth 仓库（CAS 相关依赖）
    maven {
        url = uri("https://build.shibboleth.net/maven/releases/")
    }

    maven {
        url = uri("https://build.shibboleth.net/maven/snapshots/")
    }

}
