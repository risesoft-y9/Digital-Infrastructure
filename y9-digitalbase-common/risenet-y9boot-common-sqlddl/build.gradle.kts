/*
 * This file was generated by the Gradle "init" task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-common-sqlddl"
