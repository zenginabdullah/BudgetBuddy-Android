plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.budgetbuddy.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.budgetbuddy.app"
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
    }
}

dependencies {
    // Core KTX - Android X Core Kütüphanesi, Kotlin desteği
    implementation(libs.androidx.core.ktx)
    // Lifecycle KTX - Lifecycle komponentleri için
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Activity Compose - Jetpack Compose için Activity desteği
    implementation(libs.androidx.activity.compose)
    // AndroidX Compose BOM (Bill Of Materials)
    implementation(platform(libs.androidx.compose.bom))
    // Compose UI - Jetpack Compose temel UI bileşenleri
    implementation(libs.androidx.ui)
    // Compose Grafik Kütüphanesi
    implementation(libs.androidx.ui.graphics)
    // Compose Önizleme (Preview) desteği
    implementation(libs.androidx.ui.tooling.preview)
    // Material3 UI Kütüphanesi
    implementation(libs.androidx.material3)
    // Unit Test bağımlılığı
    testImplementation(libs.junit)
    // Android Test bağımlılıkları
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Compose UI için Test Bağımlılıkları
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Debugging için Compose araçları
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Jetpack Compose için Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // Material Icons (Extended) - Compose ile kullanılan ikonlar
    implementation("androidx.compose.material:material-icons-extended")
    // WorkManager - Arka planda işler yapmak için
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    // Activity Compose entegrasyonu
    implementation("androidx.activity:activity-compose:1.6.0")

}