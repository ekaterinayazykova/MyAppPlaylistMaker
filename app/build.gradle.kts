plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
//    id ("kotlin-android")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.myappplaylistmaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myappplaylistmaker"
        minSdk = 29
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
        viewBinding = true
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
    resolutionStrategy.eachDependency {
        if (requested.group == "androidx.room") {
            useVersion("2.6.1")
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    // AndroidX и Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.viewpager2)
    implementation(libs.jetbrains.annotations)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.peko)
    implementation(libs.kotlinx.metadata.jvm)

    // Интеграции
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler) // Для Glide
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.okhttp)
    implementation(libs.gson)

    // Навигация
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.fragment.ktx)

    // Live data
    implementation(libs.lifecycle.livedata.ktx)

    // Room
    implementation(libs.room.runtime) {
        exclude(group = "com.intellij", module = "annotations")
    }
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    // DI
    implementation(libs.koin)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Splash Screen
    implementation(libs.androidx.splashscreen)

    // Тестирование
    androidTestImplementation(libs.androidx.test.ext.junit) // AndroidJUnit4
    androidTestImplementation(libs.androidx.test.core) // Основной фреймворк тестирования
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.junit)

    // Корутины
    implementation(libs.kotlinx.coroutines.android)
}