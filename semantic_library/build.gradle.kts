plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.kemas.semantic"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
//    publishing {
//        singleVariant("release") {
//            withSourcesJar()
//            withJavadocJar()
//        }
//    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("net.java.dev.jna:jna:5.18.1@aar")
}
//
//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                from(components["release"])
//
//                // Ganti identitas library kamu di sini
//                groupId = "com.kemas"
//                artifactId = "semantic-library"
//                version = "1.0.0"
//            }
//        }
//    }
//}


mavenPublishing {
    coordinates("io.github.herros27", "semantic-library", "1.0.0")

    pom {
        name.set("Semantic Library")
        description.set("Library validasi semantik menggunakan Rust dan AI untuk Android")
        inceptionYear.set("2025")

        url.set("https://github.com/herros27/SemanticValidationLibrary")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        // 3. Data Developer (Sesuai data GPG Key Anda)
        developers {
            developer {
                id.set("herros27")
                name.set("Kemas Khairunsyah")
                url.set("https://github.com/herros27")
            }
        }

        // 4. Source Control Management (SCM) - Wajib untuk validasi Central
        scm {
            url.set("https://github.com/herros27/SemanticValidationLibrary/")
            connection.set("scm:git:git://github.com/herros27/SemanticValidationLibrary.git")
            developerConnection.set("scm:git:ssh://git@github.com/herros27/SemanticValidationLibrary.git")
        }
    }
    publishToMavenCentral()

    signAllPublications()
}

