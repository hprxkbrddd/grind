plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.grind"
version = "0.0.1-SNAPSHOT"
description = "Statistics microservice for Grind app"

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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.clickhouse:clickhouse-jdbc:0.9.4")
    implementation("org.springframework.kafka:spring-kafka")
	compileOnly("org.projectlombok:lombok")
//	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    implementation("io.github.hprxkbrddd:security-autoconfiguration:0.1.0") // v0.0.4 has NO endpoint protection, v0.0.5 HAS endpoint protection
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
