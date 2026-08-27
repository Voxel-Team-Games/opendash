plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlin.serialization)
}

val gdxVersion = "1.13.5"

repositories {
    mavenCentral()
}

dependencies {

    // =================================================
    // LIBGDX CORE
    // =================================================

    implementation(
        "com.badlogicgames.gdx:gdx:$gdxVersion"
    )

    // =================================================
    // SERIALIZAÇÃO
    // =================================================

    implementation(
        libs.kotlinx.serialization.json
    )

    // =================================================
    // TESTES
    // =================================================

    testImplementation(
        "org.jetbrains.kotlin:kotlin-test-junit5"
    )

    testImplementation(
        libs.junit.jupiter.engine
    )

    testRuntimeOnly(
        "org.junit.platform:junit-platform-launcher"
    )
}

java {
    toolchain {
        languageVersion.set(
            JavaLanguageVersion.of(21)
        )
    }
}