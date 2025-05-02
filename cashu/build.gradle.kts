plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

val signalJavaVersion: JavaVersion by rootProject.extra
val signalKotlinJvmTarget: String by rootProject.extra
val signalMinSdkVersion: Int by rootProject.extra

android {
    namespace = "org.signal.cashu"
    compileSdk = 34

    defaultConfig {
        minSdk = signalMinSdkVersion
//        targetSdk = 34 // Deprecated for libraries
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    testOptions {
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = signalJavaVersion
        targetCompatibility = signalJavaVersion
    }

    kotlinOptions {
        jvmTarget = signalKotlinJvmTarget
    }
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.square.okhttp3)
    implementation(libs.gson)
    implementation(libs.bouncycastle.bcprov.jdk15on)
    implementation(libs.bouncycastle.bcpkix.jdk15on)
}