plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // <--- ADD THIS LINE
}

android {
    namespace = "com.example.financemanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.financemanager"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Enable View Binding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    //implementation(libs.androidx.room.common.jvm)
    //implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.core.splashscreen)
    // Add fragment-ktx dependency
    //implementation("androidx.fragment:fragment-ktx:1.6.2")

    // --- ADD THESE ROOM DEPENDENCIES ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    // Try explicitly referencing the configuration
    add("kapt", "androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
}