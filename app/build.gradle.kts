plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.3"

val os = System.getProperty("os.name").lowercase()
val arch = System.getProperty("os.arch").lowercase()

val lwjglNatives = when {
    os.contains("windows") -> "natives-windows"
    os.contains("linux") -> "natives-linux"
    os.contains("mac") || os.contains("darwin") -> {
        if (arch.contains("aarch64") || arch.contains("arm64")) {
            "natives-macos-arm64"
        } else {
            "natives-macos"
        }
    }
    else -> error("Sistema operacional não suportado: $os")
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation(libs.kotlinx.serialization.json)

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("com.voxelteamgames.opendash.AppKt")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.voxelteamgames.opendash.AppKt"
        )
    }
}