plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("aarGenerator") {
            id = "convention.aar-generator"
            implementationClass = "convention.AarGeneratorPlugin"
        }
    }
}

kotlin {
    jvmToolchain(17)
}