import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.maven.publish)

    id("convention.aar-generator")
    id("com.codingfeline.buildkonfig") version "0.17.1"
}

kotlin {

    group = "io.github.postapi"
    version = "0.0.4"

    androidLibrary {
        namespace = "io.github.postapi"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()

        withHostTestBuilder {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    val xcfName = "PostFramework"
    val xcf = XCFramework(xcfName)
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().forEach {
        if (it.konanTarget.family.isAppleFamily) {
            it.binaries.framework {
                baseName = xcfName // Set the name
                xcf.add(this) // Add it to the XCFramework container
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.koin.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                compileOnly(libs.koin.android)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }

    // Created plugin for manually generate .aar
    aarGenerator {
        sourceAarPath = "outputs/aar/post-api.aar"
        outputAarName = "post-release.aar"
        outputDir = "outputs/aar"
    }
}

buildkonfig {
    packageName = "io.github.postapi"

    defaultConfigs {
        buildConfigField(STRING, "BASE_URL", " https://jsonplaceholder.typicode.com")
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)
    coordinates(
        group.toString(),
        project.name,
        version.toString()
    )
}