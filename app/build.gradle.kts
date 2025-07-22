plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.local_loop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.local_loop"
        minSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }

    android.applicationVariants.all {
        if (buildType.name == "debug") {
            outputs.all {
                (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                    "Group12_debug.apk"
            }
        }
    }
}

dependencies {
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        implementation(libs.annotation)
        implementation(libs.lifecycle.livedata.ktx)
        implementation(libs.lifecycle.viewmodel.ktx)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
}