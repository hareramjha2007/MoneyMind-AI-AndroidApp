plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.finly.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.finly.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
        }
    }

    signingConfigs {
        create("hastradarRelease") {
            storeFile = file("${rootDir}/moneymind-release.jks")
            storePassword = "moneymind123"
            keyAlias = "moneymind"
            keyPassword = "moneymind123"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("hastradarRelease")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("hastradarRelease")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INDEX/AL2.0"
            excludes += "/META-INDEX/LGPL2.1"
        }
    }
}

dependencies {
    implementation(project(":core-domain"))
    implementation(project(":core-data"))
    implementation(project(":core-ui"))
    implementation(project(":feature-home"))
    implementation(project(":feature-insights"))
    implementation(project(":feature-goals"))
    implementation(project(":feature-coach"))
    implementation(project(":feature-profile"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.biometric)
    implementation(libs.workmanager.runtime.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
