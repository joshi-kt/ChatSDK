plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.chatsdk"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chatsdk"
        minSdk = 21
        targetSdk = 34
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(samplAppLibs.androidx.core.ktx)
    implementation(samplAppLibs.androidx.lifecycle.runtime.ktx)
    implementation(samplAppLibs.androidx.activity.compose)
    implementation(platform(samplAppLibs.androidx.compose.bom))
    implementation(samplAppLibs.androidx.ui)
    implementation(samplAppLibs.androidx.ui.graphics)
    implementation(samplAppLibs.androidx.ui.tooling.preview)
    implementation(samplAppLibs.androidx.material3)
    implementation(project(":chat-it-ui"))
    implementation(project(":chat-it"))
    testImplementation(samplAppLibs.junit)
    androidTestImplementation(samplAppLibs.androidx.junit)
    androidTestImplementation(samplAppLibs.androidx.espresso.core)
    androidTestImplementation(platform(samplAppLibs.androidx.compose.bom))
    androidTestImplementation(samplAppLibs.androidx.ui.test.junit4)
    debugImplementation(samplAppLibs.androidx.ui.tooling)
    debugImplementation(samplAppLibs.androidx.ui.test.manifest)
}