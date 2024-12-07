plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)


  id("kotlin-kapt")
  id("com.google.dagger.hilt.android")
}

android {
  namespace = "com.example.pdf_maker"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.example.pdf_maker"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  val nav_version = "2.8.3"
  implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
  implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
  implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")
  androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")
  implementation("androidx.navigation:navigation-compose:$nav_version")
  implementation("androidx.navigation:navigation-compose:$nav_version")

  //Dagger Hilt
  kapt("androidx.hilt:hilt-compiler:1.2.0")
  kapt("com.google.dagger:hilt-android-compiler:2.51.1")
  implementation("com.google.dagger:hilt-android:2.51.1")
  implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
  implementation("androidx.hilt:hilt-work:1.2.0")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  val roomVersion = "2.6.1"
  implementation("androidx.room:room-runtime:$roomVersion")
  annotationProcessor("androidx.room:room-compiler:$roomVersion")
  kapt("androidx.room:room-compiler:$roomVersion")
  implementation("androidx.room:room-ktx:$roomVersion")
  testImplementation("androidx.room:room-testing:$roomVersion")
  implementation("androidx.room:room-paging:$roomVersion")

  //Icons
  implementation("androidx.compose.material:material-icons-extended:1.7.5")

  //Coil
  implementation("io.coil-kt:coil-compose:2.7.0")

  implementation ("com.google.android.gms:play-services-mlkit-document-scanner:16.0.0-beta1")


}