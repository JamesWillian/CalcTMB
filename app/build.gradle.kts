plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.jammes.calctmb"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.jammes.calctmb"
        minSdk = 24
        targetSdk = 37
        versionCode = 5
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Relatórios de falhas do Crashlytics, lidos pelo meta-data do manifesto.
        manifestPlaceholders["crashlyticsEnabled"] = true
        // Números de uso do Analytics, no outro meta-data: true desativa a coleta.
        manifestPlaceholders["analyticsDeactivated"] = false
    }

    buildTypes {
        release {
            isDebuggable = false
            // O R8 ofusca, encolhe e otimiza o código. O plugin do Crashlytics envia o
            // mapping de cada build, e o painel mostra as falhas com os nomes originais.
            isMinifyEnabled = true
            // Tira do APK os recursos que ficaram sem uso depois do R8.
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            // Debug e release usam o mesmo pacote (o mesmo app no Firebase): as falhas
            // e o uso durante o desenvolvimento ficam fora dos painéis.
            manifestPlaceholders["crashlyticsEnabled"] = false
            manifestPlaceholders["analyticsDeactivated"] = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
}

ksp {
    // Schemas versionados permitem escrever migrações do Room com segurança.
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Cartão de avaliação da Google Play, aberto por cima do app.
    implementation(libs.play.review.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.tooling)
}
