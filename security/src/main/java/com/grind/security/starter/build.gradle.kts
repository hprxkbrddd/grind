package com.grind.security.starter

import org.gradle.kotlin.dsl.dependencies

plugins {
    id("java-library")
//    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.7")
    }
}

group = "com.grind"
version = "0.0.2-SNAPSHOT"
description = "Security microservice for Grind app"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}


//tasks {
//    val cleanJar by registering(Jar::class) {
//        ->
//        archiveBaseName.set("security-lib-clean")
//        from(sourceSets.main.get().output)
//        exclude("META-INF/*.SF")
//        exclude("META-INF/*.DSA")
//        exclude("META-INF/*.RSA")
//        exclude("META-INF/*.MF")
//    }
//}

// Или проще - настройка основного task jar
tasks.jar {
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/*.MF")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.projectlombok:lombok")
//	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.keycloak:keycloak-spring-boot-starter:25.0.3")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // https://mvnrepository.com/artifact/io.projectreactor/reactor-test
    testImplementation("io.projectreactor:reactor-test:3.1.0.RELEASE")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
