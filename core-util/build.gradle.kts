plugins {
  id("com.android.library")
  id("signal-library")
  id("com.squareup.wire")
}

android {
  namespace = "org.signal.core.util"
  compileSdk = (rootProject.extra["signalCompileSdkVersion"] as String).substringAfter("android-").toInt()
  buildToolsVersion = rootProject.extra["signalBuildToolsVersion"] as String

  defaultConfig {
    minSdk = rootProject.extra["signalMinSdkVersion"] as Int
  }

  buildFeatures {
    buildConfig = false
  }

  // Explicitly disable aidl generation
  buildFeatures.aidl = false
  sourceSets.getByName("main").aidl.setSrcDirs(emptyList<String>())
  
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = rootProject.extra["signalJavaVersion"] as JavaVersion
    targetCompatibility = rootProject.extra["signalJavaVersion"] as JavaVersion
  }
}

dependencies {
  coreLibraryDesugaring(libs.android.tools.desugar)
  api(project(":core-util-jvm"))

  implementation(libs.androidx.annotation)
  implementation(libs.androidx.sqlite)
  implementation(libs.androidx.documentfile)
  implementation(libs.wire.runtime.jvm)

  testImplementation(testLibs.junit.junit)
  testImplementation(testLibs.robolectric.robolectric)
}

wire {
  kotlin {
    javaInterop = true
  }

  sourcePath {
    srcDir("src/main/protowire")
  }
}
