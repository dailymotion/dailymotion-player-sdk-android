plugins {
    id("com.android.application").version(Version.androidPlugin).apply(false)
    id("org.jetbrains.kotlin.jvm").version(Version.kotlin).apply(false)
    id("org.jetbrains.kotlin.android").version(Version.kotlin).apply(false)
    id("org.jetbrains.kotlin.android.extensions").version(Version.kotlin).apply(false)
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

