plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

repositories {
    google()
    mavenCentral()
}

val gdxVersion = "1.13.5"

configurations {
    create("natives")
}

android {
    namespace = "com.voxelteamgames.morphjump"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.voxelteamgames.morphjump"
        minSdk = 23
        targetSdk = 35

        versionCode = 1
        versionName = "0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":core"))

    implementation(
        "com.badlogicgames.gdx:gdx-backend-android:$gdxVersion"
    )

    implementation(
        "com.badlogicgames.gdx:gdx:$gdxVersion"
    )

    add(
        "natives",
        "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a"
    )
}

tasks.register("copyAndroidNatives") {
    val natives = configurations.getByName("natives")
    val outputDir = file("$projectDir/src/main/jniLibs/arm64-v8a")

    doLast {
        outputDir.mkdirs()

        natives.files.forEach { jar ->
            copy {
                from(zipTree(jar))
                include("**/*.so")
                into(outputDir)
            }
        }
    }
}

tasks.named("preBuild") {
    dependsOn("copyAndroidNatives")
}