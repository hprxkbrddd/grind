plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.grind"
version = "0.0.1-SNAPSHOT"
description = "Gateway for Grind app"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
    // --- Web ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-webflux")

    // --- Kafka ---
    implementation("org.springframework.kafka:spring-kafka")

    // --- Security ---
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    implementation("org.keycloak:keycloak-spring-boot-starter:25.0.3")

    // --- OpenAPI ---
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.15")

    // --- Lombok ---
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // --- Configuration metadata ---
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // --- Tests ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test:3.1.0.RELEASE")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}