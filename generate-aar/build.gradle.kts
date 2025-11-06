plugins {
    alias(libs.plugins.android.fusedlibrary)
    `maven-publish`
}


androidFusedLibrary {
    namespace = "io.github.postapi"
    aarMetadata {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }
}


dependencies {
    // Your shared KMP library
    include(project(":post-api"))

    // Koin
    include(libs.koin.core)
    include(libs.koin.android)

    // Ktor (Common)
    include(libs.ktor.client.okhttp)
}


tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "example"
            artifactId = "post-api-lib"
            version = "1.0"
            from(components["fusedLibraryComponent"])
        }
    }
}