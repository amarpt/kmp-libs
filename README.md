---

# KMP Libraries Integration Guide

Complete integration guide for using `post-api` and `calc` Kotlin Multiplatform (KMP) modules in Android and iOS applications.

## Project Overview

This project demonstrates multi-module KMP library integration with sample Android and iOS applications. The libraries are published using local Maven for Android and XCFrameworks for iOS.

## Architecture Challenges



**AAR Generation Limitation**: Initially attempted using a `generate-aar` module with fused Android plugin for `.aar` generation and manual .aar generation, but encountered crashes the application if the dependent Gradle/library is not included in the Android application. Multi-module dependencies are not automatically included in `.aar` files, requiring separate publication of each module.

## Android Integration

## Publish to Local Maven

Clean and publish each module to your local Maven repository (`~/.m2/repository`):

```bash
# Publish post-api module*
./gradlew clean :post-api:publishToMavenLocal --no-build-cache

# Publish calc module*
./gradlew clean :calc:publishToMavenLocal --no-build-cache

# Optional: Generate AAR (if still needed for legacy integration)*
./gradlew clean :generate-aar:assemble --no-build-cache --refresh-dependencies
```

## Configure Android App Dependencies

Update `sample/androidApp/build.gradle.kts`:

```kotlin
dependencies {
    // Local Maven dependencies (recommended)*
    implementation("com.example:post-api:1.0.0")
    implementation("com.example:calc:1.0.0")
    
    // Alternative: Direct AAR files
    // implementation(libs.ktor.client.okhttp)
    // implementation(libs.ktor.client.content.negotiation)
    // implementation(libs.ktor.serialization.kotlinx.json)
    // implementation(files("../generate-aar/build/outputs/aar/post-api-release.aar"))// implementation(files("../generate-aar/build/outputs/aar/calc-release.aar"))*
}
```

## Initialize Koin in Android App

**Important Note**: Koin can be initialized in `androidMain`, but if your Android app already uses Koin separately, it may create conflicts. Initialize it once in the Application class.

```kotlin
@HiltAndroidApp
class AndroidSampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDefaultKoin()  *// Initialize shared Koin modules*
    }
}
```

## Inject Dependencies with `kmp-libs`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePostRepository(): PostRepository {
        return inject<PostRepository>()
    }
}
```

---

## iOS Integration

## Build XCFrameworks

Generate XCFrameworks for iOS consumption:

```bash
# Build post-api XCFramework*
./gradlew :post-api:assemblePostFrameworkXCFramework

# Build calc XCFramework*
./gradlew :calc:assemblePostFrameworkXCFramework

# Verify frameworks were created*
ls -la post-api/build/XCFrameworks/
ls -la calc/build/XCFrameworks/
```

## Step 2: Configure Xcode Project

## Framework Search Paths

1. Open `sample/iosApp/iosApp.xcodeproj` in Xcode
2. Select your target → **Build Settings**
3. Search for **"Framework Search Paths"**
4. Add the following paths (both formats required for nested frameworks):

```text
$(SRCROOT)/../../../post-api/build/XCFrameworks
$(SRCROOT)/../../../post-api/build/XCFrameworks/**
$(SRCROOT)/../../../calc/build/XCFrameworks
$(SRCROOT)/../../../calc/build/XCFrameworks/**
```

## Header Search Paths

Add the same paths to **Header Search Paths**:

```text
$(SRCROOT)/../../../post-api/build/XCFrameworks
$(SRCROOT)/../../../calc/build/XCFrameworks
```

## Link Frameworks

1. Go to target → **Build Phases** → **Link Binary With Libraries**
2. Click **+** and add:
    - `post-api.xcframework`
    - `calc.xcframework`

## Initialize Koin in iOS App

Initialize Koin at app launch:

```swift
@main
struct iOSAppApp: App {
    
    init() {
        KoinKt.doInitDefaultKoin()  *// Initialize shared Koin DI*
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

## Access Repository in SwiftUI

Use the custom Flow collector extension to handle Kotlin Flow in Swift:

```swift
class PostViewModel: ObservableObject {
    @Published var posts: [Post] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    func loadPosts() {
        let repository = KoinManager.shared.getPostRepository() as! PostRepository
        let postsFlow = repository.getAllPost()
        
        FlowExtKt.collectResult(
            postsFlow,
            onSuccess: { [weak self] postList in
                self?.posts = postList as! [Post]
                self?.isLoading = false
            },
            onLoading: { [weak self] in
                self?.isLoading = true
            },
            onError: { [weak self] errorMsg in
                self?.errorMessage = errorMsg
                self?.isLoading = false
            }
        )
    }
}
```

---
