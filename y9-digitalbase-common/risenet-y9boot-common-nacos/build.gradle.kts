plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.com.alibaba.cloud.spring.cloud.starter.alibaba.nacos.config)
    api(libs.com.alibaba.cloud.spring.cloud.starter.alibaba.nacos.discovery)
    api(libs.com.alibaba.nacos.nacos.client)
    api(libs.org.springframework.boot.spring.boot.starter.web)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-common-nacos"