object Version {
    val targetSdkVersion = 31
    val minSdkVersion = 21
    val compileSdkVersion = 31

    val androidPlugin = "7.1.1"
    val kotlin = "1.6.10"
}

object Libs {
    const val kxCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:_"
    const val kxCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:_"

    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:_"
    const val xAppCompat = "androidx.appcompat:appcompat:_"
    const val xCore = "androidx.core:core-ktx:_"
    const val xConstraintLayout = "androidx.constraintlayout:constraintlayout:_"

    const val timber = "com.jakewharton.timber:timber:_"
    const val gson = "com.google.code.gson:gson:_"
    const val playServicesAds = "com.google.android.gms:play-services-ads-identifier:_"

    const val xTestRunner = "androidx.test:runner:_"
    const val xTestRules = "androidx.test:rules:_"
    const val xEspresso = "androidx.test.espresso:espresso-core:_"
    const val xTestUnit = "androidx.test.ext:junit:_"

    const val junit = "junit:junit:_"
}