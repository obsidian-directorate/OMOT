plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.osd.omot_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.osd.omot_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-ALPHA"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "IS_DEBUG", "false")
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            buildConfigField("boolean", "IS_DEBUG", "true")
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Android Core
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.activity)

    // Material Design Components (For modern, themable UI widgets)
    implementation(libs.material)

    // Security Crypto (For EncryptedSharedPreferences and more)
    implementation(libs.security.crypto)

    // Biometric Library (Standardized prompt)
    implementation(libs.biometric)

    // Lifecycle & ViewModel (Essential for modern Android architecture)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.viewmodel.savedstate)

    // Splash screen API
    implementation(libs.core.splashscreen)

    // Unit Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}