import org.gradle.kotlin.dsl.dependencies

plugins {
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.grind"
version = "0.0.3-SNAPSHOT"
description = "Security microservice for Grind app"

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
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.projectlombok:lombok")
//	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
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

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar"){
    enabled = false
}

tasks.getByName<Jar>("jar"){
    enabled = true
    archiveClassifier.set("")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

// Публикация в Maven Local (или репозиторий)
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Security grind library")
                description.set("Security beans provider")
                url.set("https://github.com/hprxkbrddd/grind/tree/main/security")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("developer")
                        name.set("Your Name")
                        email.set("developer@example.com")
                    }
                }
            }
        }
    }
    repositories{
        mavenLocal()
    }
}

// Генерация источников JAR
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
}

//// Генерация javadoc JAR
//tasks.register<Jar>("javadocJar") {
//    archiveClassifier.set("javadoc")
//    from(tasks.javadoc)
//}

// Добавляем дополнительные артефакты в публикацию
publishing {
    publications {
        named<MavenPublication>("mavenJava") {
            artifact(tasks["sourcesJar"])
//            artifact(tasks["javadocJar"])
        }
    }
}
