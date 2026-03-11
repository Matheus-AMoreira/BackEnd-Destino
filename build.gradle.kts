plugins {
    java
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-rest")
    testImplementation("io.quarkus:quarkus-junit")
    testImplementation("io.rest-assured:rest-assured")

    // Web e Validação
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-hibernate-validator")

    // Banco de Dados (PostgreSQL)
    implementation ("io.quarkus:quarkus-hibernate-orm-panache")
    implementation ("io.quarkus:quarkus-jdbc-postgresql")

    // Segurança e JWT
    implementation ("io.quarkus:quarkus-smallrye-jwt")
    implementation ("io.quarkus:quarkus-smallrye-jwt-build")

    // Documentação (Swagger)
    implementation ("io.quarkus:quarkus-smallrye-openapi")

    // Testes
    testImplementation ("io.quarkus:quarkus-junit5")
    testImplementation ("io.rest-assured:rest-assured")

    // Cryptography
    implementation("org.mindrot:jbcrypt:0.4")
}

group = "org.tech6"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
