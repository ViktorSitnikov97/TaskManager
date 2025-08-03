import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	id("org.sonarqube") version "6.2.0.5505"
	id("com.github.ben-manes.versions") version "0.48.0"
	id("application")
	id("checkstyle")
	id("jacoco")
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	id("io.freefair.lombok") version "8.4"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application {
	mainClass.set("hexlet.code.AppApplication")
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

sonar {
	properties {
		property("sonar.projectName", "TaskManager")
		property("sonar.projectKey", "ViktorSitnikov97_TaskManager")
		property("sonar.organization", "viktorsitnikov97")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.ci.autoconfig.disabled", "true")
	}
}

checkstyle {
	configFile = file("config/checkstyle/checkstyle.xml")
	toolVersion = "10.13.0"    // your choice here
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	implementation("org.instancio:instancio-junit:3.3.1")
	implementation("net.datafaker:datafaker:2.0.2")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.2.0")
	testImplementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.2.0")
}

tasks.withType<Test>() {
	finalizedBy(tasks.jacocoTestReport)
	useJUnitPlatform()
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
		showStandardStreams = true
	}
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
	}
}

tasks.register<JavaExec>("runTaskManagerApp") {
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("hexlet.code.AppApplication")
	args("--server.port=8080")
}