import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}
// 2. Load file local.properties DI SINI (Di luar blok android)
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

// Ambil Key (Gunakan elvis operator ?: "" untuk jaga-jaga jika null)
val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""

android {
    namespace = "com.kemas.semantic_validation_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kemas.semantic_validation_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"


        // Masukkan ke BuildConfig
        buildConfigField("String", "GEMINI_KEY", "\"$geminiApiKey\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.tools.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation(project(":semantic_library"))
    implementation("com.kemas:semantic-library:1.0.0")
}