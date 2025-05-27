plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.mapsplatform.secrets.gradle.plugin)

    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    kotlin("plugin.serialization") version "2.0.10"
}

android {
    namespace = "com.nmheir.kanicard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nmheir.kanicard"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
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
    implementation(libs.navigation)


    //Window Width Size Class
    implementation(libs.androidx.compose.materialWindow)

    //Material Theme
    implementation(libs.material)

    //Splash Screen
    implementation(libs.androidx.core.splashscreen)

    //Hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    //Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    // Moshi converter for parsing JSON responses
    implementation(libs.moshi.converter)

    //Retrofit
    implementation(libs.retrofit)
    // OkHttp logging interceptor for debugging HTTP traffic
    implementation(libs.okHttp3.logging)

    //Coil
    implementation(libs.coil)
    implementation(libs.coil.network)

    //DataStore
    implementation(libs.datastore)

    //Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    //Timber
    implementation(libs.timber)

    //Supbase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.storage)
    implementation(libs.ktor.client.cio)
    implementation(libs.supabase.serializer.moshi)

    //Kotlin Collection Immutable
    implementation(libs.kotlinx.collections.immutable)

    //Material Motion Compose
    implementation (libs.motion.compose.core)

//    Shimmer
    implementation(libs.shimmer)

    //Serialization
    implementation(libs.kotlinx.serialization.json)

    // CommonMark, for markdown rendering and parsing
    implementation(libs.commonmark.ext.autolink)
    implementation(libs.commonmark.ext.footnotes)
    implementation(libs.commonmark.ext.ins)
    implementation(libs.commonmark.ext.task.list.items)
    implementation(libs.commonmark.ext.gfm.strikethrough)
    implementation(libs.commonmark.ext.gfm.tables)
    implementation(libs.commonmark.ext.heading.anchor)
    implementation(libs.commonmark.ext.image.attributes)
    implementation(libs.commonmark)

    //Compose Webview
    implementation(libs.compose.webview)

    //Chart
    implementation(libs.vico.compose)
    implementation(libs.charts.mahu)
}