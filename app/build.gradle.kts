plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    id ("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin") // Safe Args
    id("kotlin-parcelize") // Parcelize
    id ("dagger.hilt.android.plugin")
//    id ("androidx.navigation.safeargs.kotlin")

}

android {
    namespace = "com.example.outdoorsy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.outdoorsy"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        dataBinding = true

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
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    // To use constraintlayout in compose
//    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")

    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.firebase.dataconnect)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation (libs.material.v190)
    implementation(libs.google.firebase.auth.ktx)
    implementation (libs.androidx.recyclerview)
    implementation (libs.androidx.lifecycle.runtime.ktx.v261)
    implementation (libs.androidx.navigation.fragment.ktx.v260)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation (libs.glide)
    kapt ("com.github.bumptech.glide:compiler:4.16.0")
    implementation (libs.github.glide.v4120)
    annotationProcessor (libs.glide.compiler)

    implementation (libs.material.v140)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
//    implementation (libs.androidx.hilt.lifecycle.viewmodel)
    kapt (libs.androidx.hilt.compiler)

    implementation (libs.material)

    implementation (libs.cloudinary.android)

    var room_version ="2.6.1"
    implementation(libs.androidx.room.runtime)
    kapt("androidx.room:room-compiler:$room_version")
    implementation(libs.androidx.room.ktx)

    // KAPT for Hilt annotation processing
}