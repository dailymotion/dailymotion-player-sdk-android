plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.android.extensions")
}

dependencies {
    api(project(":sdk"))

    implementation(Libs.kotlin)
    implementation(Libs.xAppCompat)
    implementation(Libs.timber)
    implementation(Libs.xConstraintLayout)

    androidTestImplementation(Libs.xEspresso)
    androidTestImplementation(Libs.xTestRunner)
    androidTestImplementation(Libs.xTestRules)
    androidTestImplementation(Libs.xTestUnit)
}

android {
    compileSdk = Version.compileSdkVersion

    defaultConfig {

        buildConfigField("String", "PLAYER_SDK_VERSION", "\"${LibraryProject.libraryVersionName}\"")

        minSdk = Version.minSdkVersion
        targetSdk = Version.targetSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

repositories {
    mavenCentral()
}
