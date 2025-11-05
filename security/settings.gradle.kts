rootProject.name = "security-library"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.springframework.boot") version "3.5.7"
        id("io.spring.dependency-management") version "1.1.7"
    }
}