plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))
    
    api(libs.httpcomponents.httpmime)
    api("com.fasterxml.jackson.core:jackson-databind")
    api(libs.commons.beanutils)
   api("org.apache.commons:commons-lang3")
    api("org.slf4j:slf4j-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}

description = "risenet-y9boot-idcode"
