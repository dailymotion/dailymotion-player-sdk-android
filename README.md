Dailymotion Player SDK Android
===========================
[ ![Download](https://api.bintray.com/packages/dailymotion/com.dailymotion.dailymotion-sdk-android/sdk/images/download.svg) ](https://bintray.com/dailymotion/com.dailymotion.dailymotion-sdk-android/sdk/_latestVersion)

This SDK aims at easily embedding Dailymotion videos on your Android application using WebView. It supports Android 3.0.x and superior.
The SDK is bundled with a sample application

Features
--------

- Dead simple to use. No need to specify a layout container for the VideoView
- Supports Android 3.0.x and superior

How to use
----------

### Add the SDK to your project
You can either import the SDK using your IDE or integrate PlayerWebView.java in your project.

You can import the sdk with :
```
compile 'com.dailymotion.dailymotion-sdk-android:sdk:0.1.22'
```

```
android:hardwareAccelerated="true"

<uses-permission android:name="android.permission.INTERNET" />
```

### Use in your Activity or Fragment
First, add the PlayerWebView in your layout in place of the regular WebView.

```xml
        <com.dailymotion.android.player.sdk.PlayerWebView
               android:id="@+id/dm_player_web_view"
               android:layout_width="match_parent"
               android:layout_height="215dp">
       </com.dailymotion.android.player.sdk.PlayerWebView>
```

Then in your Activity code just launch your content.
Get your PlayerWebView then call load("id").


```java
    private PlayerWebView mVideoView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_sample);

        mVideoView = (PlayerWebView) findViewById(R.id.dm_player_web_view);
        mVideoView.load("x26hv6c");
    }
```

You can load the video with your parameters.
```java
    private PlayerWebView mVideoView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_sample);

        mVideoView = (PlayerWebView) findViewById(R.id.dm_player_web_view);
        Map<String, String> playerParams = new HashMap<>();
        playerParams.put("key", "value");
        mVideoView.load("x26hv6c", playerParams);
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

```java
    @Override
    protected void onPause() {
        super.onPause();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mVideoView.onPause();
        }
    }
        

    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mVideoView.onResume();
        }
    }
```

### Play Services
The SDK uses Google Play Services to get the [Advertising Id](http://www.androiddocs.com/google/play-services/id.html)
If your app also uses play services, you may want to override the `play-services-ads` version to avoid conflicting with other play services artifacts.

```java
dependencies {
    implementation "com.google.android.gms:play-services-ads:[your_play_services_version]"
}
```

### Publish your own sdk on Bintray

Update your local.properties files with this lines and replace <user> and <api.key> values`

```
bintray.user=<user>
bintray.apikey=<api.key>
```

In your terminal call `gradlew install` then `gradlew bintrayUpload` 