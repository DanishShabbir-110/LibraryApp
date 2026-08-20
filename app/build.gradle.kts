plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.libraryapp"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.libraryapp"

        minSdk = 24
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    // -----------------------------
    // AndroidX Core
    // -----------------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)

    // -----------------------------
    // Material Design
    // -----------------------------
    implementation(libs.material)

    // -----------------------------
    // ConstraintLayout
    // -----------------------------
    implementation(libs.androidx.constraintlayout)

    // -----------------------------
    // RecyclerView
    // -----------------------------
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // -----------------------------
    // Navigation
    // -----------------------------
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // -----------------------------
    // Lifecycle
    // -----------------------------
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")

    // -----------------------------
    // Coroutines
    // -----------------------------
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // -----------------------------
    // Room Database
    // -----------------------------
    val roomVersion = "2.8.4"

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    ksp("androidx.room:room-compiler:$roomVersion")

    // -----------------------------
    // Splash Screen
    // -----------------------------
    implementation("androidx.core:core-splashscreen:1.2.0")

    // -----------------------------
    // Hilt
    // -----------------------------
    implementation(libs.hilt.android)

    ksp(libs.hilt.compiler)

    // -----------------------------
    // Testing
    // -----------------------------
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}