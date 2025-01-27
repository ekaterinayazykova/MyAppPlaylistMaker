plugins {
    alias(libs.plugins.android.application) // Использование alias для AGP
    alias(libs.plugins.jetbrains.kotlin.android) // Использование alias для Kotlin
    id("kotlin-kapt") // Оставляем как есть
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

dependencies {
    // AndroidX и Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.viewpager2)

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

    // Room
    implementation(libs.androidx.room.ktx)

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
}