import kotlinx.coroutines.runBlocking

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka").version("1.4.32").apply(false)
}

apply (plugin = "com.android.library")
apply (plugin = "kotlin-android")
apply(plugin = "org.jetbrains.dokka")
apply(plugin = "maven-publish")
apply(plugin = "signing")

android {
    compileSdkVersion(Version.compileSdkVersion)

    defaultConfig {
        minSdkVersion(Version.minSdkVersion)
        targetSdkVersion(Version.targetSdkVersion)
        buildConfigField("String", "SDK_VERSION", "\"${LibraryProject.libraryVersionName}\"")
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.google.android.gms:play-services-ads-identifier:17.0.1")

    implementation("androidx.core:core-ktx:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
    dokkaSourceSets {
        configureEach {
            reportUndocumented.set(false)
        }
    }
}

fun Project.configureMavenPublish() {
    val sourcesJarTaskProvider = tasks.register("sourcesJar", org.gradle.jvm.tasks.Jar::class.java) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    val javadocJarTaskProvider = tasks.register("docJar", org.gradle.jvm.tasks.Jar::class.java) {
        dependsOn("dokkaJavadoc")
        archiveClassifier.set("javadoc")
        from("$buildDir/dokka/javadoc")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("default") {
                from(components.getByName("release"))

                artifact(sourcesJarTaskProvider.get())
                artifact(javadocJarTaskProvider.get())

                pom {
                    groupId = "com.dailymotion.dailymotion-sdk-android"
                    artifactId = "sdk"
                    version = LibraryProject.libraryVersionName
                    name.set("DailymotionPlayerSDKAndroid")
                    packaging = "jar"
                    description.set("This SDK aims at easily embedding Dailymotion videos on your Android application using WebView.")
                    url.set("https://github.com/dailymotion/dailymotion-player-sdk-android")

                    scm {
                        url.set("https://github.com/dailymotion/dailymotion-player-sdk-android")
                        connection.set("https://github.com/dailymotion/dailymotion-player-sdk-android")
                        developerConnection.set("https://github.com/dailymotion/dailymotion-player-sdk-android")
                    }

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/dailymotion/dailymotion-player-sdk-android/blob/master/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set("Dailymotion")
                            name.set("Dailymotion")
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                name = "ossStaging"
                setUrl {
                    uri(rootProject.getOssStagingUrl())
                }
                credentials {
                    username = System.getenv("SONATYPE_NEXUS_USERNAME")
                    password = System.getenv("SONATYPE_NEXUS_PASSWORD")
                }
            }
            maven {
                name = "ossSnapshots"
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = System.getenv("SONATYPE_NEXUS_USERNAME")
                    password = System.getenv("SONATYPE_NEXUS_PASSWORD")
                }
            }
        }
    }

    configure<SigningExtension> {
        // GPG_PRIVATE_KEY should contain the armoured private key that starts with -----BEGIN PGP PRIVATE KEY BLOCK-----
        // It can be obtained with gpg --armour --export-secret-keys KEY_ID
        useInMemoryPgpKeys(System.getenv("DAILYMOTION_GPG_PRIVATE_KEY"), System.getenv("DAILYMOTION_GPG_PRIVATE_KEY_PASSWORD"))
        val publicationsContainer = (extensions.getByName("publishing") as PublishingExtension).publications
        sign(publicationsContainer)
    }
    tasks.withType(Sign::class.java).configureEach {
        isEnabled = !System.getenv("DAILYMOTION_GPG_PRIVATE_KEY").isNullOrBlank()
    }
}

fun Project.getOssStagingUrl(): String {
    val url = try {
        this.extensions.extraProperties["ossStagingUrl"] as String?
    } catch (e: ExtraPropertiesExtension.UnknownPropertyException) {
        null
    }
    if (url != null) {
        return url
    }
    val client = net.mbonnin.vespene.lib.NexusStagingClient(
        baseUrl = "https://s01.oss.sonatype.org/service/local/",
        username = System.getenv("SONATYPE_NEXUS_USERNAME"),
        password = System.getenv("SONATYPE_NEXUS_PASSWORD")
    )
    val repositoryId = runBlocking {
        client.createRepository(
            profileId = System.getenv("DAILYMOTION_STAGING_PROFILE_ID"),
            description = "$group $name $version"
        )
    }
    println("publishing to '$repositoryId")
    return "https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/${repositoryId}/".also {
        this.extensions.extraProperties["ossStagingUrl"] = it
    }
}

tasks.register<Task>("deployArtifacts") {
    project.logger.lifecycle("Upload to OSSStaging needed.")
    dependsOn("publishDefaultPublicationToOssStagingRepository")
}

task("tagAndBump"){
    doLast {
        LibraryProject.tagAndIncrement(LibraryProject.libraryVersionCode + 1)
    }
}

afterEvaluate {
    configureMavenPublish()
}