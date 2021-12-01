import org.jetbrains.kotlin.platform.jvm.JvmPlatforms

plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}
