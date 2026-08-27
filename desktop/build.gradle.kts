plugins {
    alias(libs.plugins.jvm)
    application
}

val gdxVersion = "1.13.5"
val lwjglVersion = "3.3.3"

repositories {
    mavenCentral()
}

dependencies {

    // =================================================
    // OPEN DASH CORE
    // =================================================

    implementation(
        project(":core")
    )

    // =================================================
    // LIBGDX DESKTOP
    // =================================================

    implementation(
        "com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion"
    )

    runtimeOnly(
        "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
    )

    // =================================================
    // LWJGL NATIVES - LINUX
    // =================================================

    runtimeOnly(
        "org.lwjgl:lwjgl:$lwjglVersion:natives-linux"
    )

    runtimeOnly(
        "org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-linux"
    )

    runtimeOnly(
        "org.lwjgl:lwjgl-openal:$lwjglVersion:natives-linux"
    )

    runtimeOnly(
        "org.lwjgl:lwjgl-stb:$lwjglVersion:natives-linux"
    )
}

java {
    toolchain {
        languageVersion.set(
            JavaLanguageVersion.of(21)
        )
    }
}

application {
    mainClass.set(
        "com.voxelteamgames.opendash.DesktopLauncherKt"
    )
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to
                "com.voxelteamgames.opendash.DesktopLauncherKt"
        )
    }
}