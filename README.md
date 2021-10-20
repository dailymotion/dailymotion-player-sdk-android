Dailymotion Player SDK Android
===========================
[ ![Download](https://api.bintray.com/packages/dailymotion/com.dailymotion.dailymotion-sdk-android/sdk/images/download.svg) ](https://bintray.com/dailymotion/com.dailymotion.dailymotion-sdk-android/sdk/_latestVersion)
[![Build Status](https://travis-ci.org/dailymotion/dailymotion-player-sdk-android.svg?branch=master)](https://travis-ci.org/dailymotion/dailymotion-player-sdk-android)

Our Android SDK allows for effortless embedding of the Dailymotion video player in your Android application using a WebView. It supports api level 21+.
This SDK that we provide to our publishers is the same tool we use to create our flagship Dailymotion applications. It provides access to the Player API and gives you full control of the player. To learn more please check out official Dailymotion Android developer doc [here](https://developer.dailymotion.com/player/#embed-mobile-android).

Features
--------

- Simple SDK to integrate, provides access to the Player API, no need to specify a layout container for the VideoView
- The SDK is bundled with a sample application
- Supports Android 5.0.x (API level 21) and superior
- Fully in kotlin. If your project is still in JAVA, you will need to add the kotlin dependencies: https://developer.android.com/kotlin/add-kotlin

How to use
----------

### Add the SDK to your project
You can either import the SDK using your IDE or integrate PlayerWebView.kt in your project.

Our artefacts are hosted on `mavenCentral` <br/>
Using gradle, you can import the sdk with :
```
implementation 'com.dailymotion.dailymotion-sdk-android:sdk:0.2.8'
```

The sdk will need the following permission and attributes inside your `AndroidManifest.xml`:
```
<uses-permission android:name="android.permission.INTERNET" />
```

```
android:hardwareAccelerated="true"
```

### Use in your Activity or Fragment
First, add the PlayerWebView in your layout in place of the regular WebView.

```xml
    <com.dailymotion.android.player.sdk.PlayerWebView
        android:id="@+id/dm_player_web_view"
        android:layout_width="match_parent"
        android:layout_height="215dp" />
```

Then in your Activity code just launch your content.
To play a video, simply call the `load(...)` method of `PlayerWebView`.


```kotlin
    lateinit var playerWebView: PlayerWebView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_sample);

        playerWebView = findViewById(R.id.playerWebview)
        val params = mapOf("video" to "x26hv6c")
        playerWebView.load(loadParams = params);
    }
```

The `load(...)` method can also take additionnal parameters:
```kotlin
    lateinit var playerWebView: PlayerWebView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_sample);

        playerWebView = findViewById(R.id.playerWebview)
        val params = mapOf(
                "video" to "videoXId",
                "key2" to "value2",
                "key3" to "value3",
        )
        playerWebView.load(loadParams = params);
    }
```

### Handle screen rotation
For the screen rotation to be handled correctly, you need to add

```xml
    android:configChanges="orientation|screenSize"
```

to any activity using PlayerWebView, in your AndroidManifest.xml

### Lifecycle
You have to call onPause and onResume when these events occur in your lifecycle :

```kotlin
    override fun onPause() {
        super.onPause()
        playerWebview.onPause()
    }

    override fun onResume() {
        super.onResume()
        playerWebview.onResume()
    }
```

### OM SDK integration
Open Measurement SDK from IAB is designed to facilitate third party viewability and verification measurement for ads served to web video and mobile app environments. See https://iabtechlab.com/standards/open-measurement-sdk/ for more details

We have integrated the SDK in our Dailymotion Player SDK and it does more or less everything out of the box :
- Ad session management
- Ad main signals (play, buffer_start, buffer_end, pause, resume, quartiles, click)
- Device and Player volume management
- ⚠️ Basic Player state handling. NORMAL or FULLSCREEN based on player fullscreen state.<br/><br/>
It's **STRONGLY** recommended to update at all time the player state if your app has more player layout variety, such as mini-player, picture-in-picture, etc...<br/>
To do it, simply update the OMHelper `playerState` field : 
```kotlin
    OMHelper.playerState = PlayerState.COLLAPSED
```
![image](https://user-images.githubusercontent.com/6400030/125312203-5ba0c700-e334-11eb-979f-6dd7e5d924ad.png)

### IAB TCF2
The SDK follows the [IAB TCF2 standard](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework) to [access](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20CMP%20API%20v2.md#how-do-third-party-sdks-vendors-access-the-consent-information-in-app) the stored consent string.
In order to pass the consent string to the player, just use those lines **after the consent string was generated** by the CMP:

```kotlin
    // Instantiate the TCF2Handler
    val tcf2Handler = TCF2Handler()
    
    // Make the player load the consent string stored at the location determined by IAB TCF2 Standard
    val didLoadConsentString = tcf2Handler.loadConsentString(context = this)
    
    if (didLoadConsentString) {
        // Successfully loaded the consent string
    } else {
        // Failed to load the consent string: check logs
    }
```

### Play Services
The SDK uses Google Play Services to get the [Advertising Id](https://developer.android.com/training/articles/ad-id)
If your app also uses play services, you may want to override the `play-services-ads-identifier` version to avoid conflicting with other play services artifacts.

```
dependencies {
    implementation 'com.google.android.gms:play-services-ads-identifier:[your_play_services_version]'
}
```

Faq
---

**Q.** I have an only Java project and I don’t plan to move to Kotlin. How can use your sdk ?\
**A.** Starting from 0.2.0, the sdk is Kotlin only however you can still use the previous sdk version which are in Java and still available.\
For instance, [0.1.31](https://bintray.com/dailymotion/com.dailymotion.dailymotion-sdk-android/sdk/0.1.31) is the latest sdk using only Java.
