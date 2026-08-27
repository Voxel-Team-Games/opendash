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

// =====================================================
// NORMAL JAR
// =====================================================

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to
                "com.voxelteamgames.opendash.DesktopLauncherKt"
        )
    }
}

// =====================================================
// OPEN DASH FAT JAR
// =====================================================

val fatJar =
    tasks.register<Jar>("fatJar") {

        archiveBaseName.set("OpenDash")
        archiveVersion.set("0.1")
        archiveClassifier.set("")

        duplicatesStrategy =
            DuplicatesStrategy.EXCLUDE

        manifest {
            attributes(
                "Main-Class" to
                    "com.voxelteamgames.opendash.DesktopLauncherKt"
            )
        }

        // Inclui as classes e recursos do próprio Desktop
        from(sourceSets.main.get().output)

        // Inclui TODAS as dependências do runtime:
        // Core + LibGDX + LWJGL + natives
        dependsOn(configurations.runtimeClasspath)

        from(
            configurations.runtimeClasspath.get().map { file ->
                if (file.isDirectory) {
                    file
                } else {
                    zipTree(file)
                }
            }
        )

        // Arquivos de assinatura de JARs externos
        // não podem ser carregados depois de misturados.
        exclude(
            "META-INF/*.SF",
            "META-INF/*.RSA",
            "META-INF/*.DSA"
        )
    }

tasks.build {
    dependsOn(fatJar)
}