Dailymotion Android Web-SDK
===========================

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
compile 'com.dailymotion.dailymotion-sdk-android:sdk:0.1.3'
```

```
android:hardwareAccelerated="true"

<uses-permission android:name="android.permission.INTERNET" />
```

### Use in your Activity or Fragment
First, add the PlayerWebView in your layout in place of the regular WebView.

	   <com.dailymotion.sdkandroid.PlayerWebView
               android:id="@+id/dm_player_web_view"
               android:layout_width="match_parent"
               android:layout_height="215dp">
       </com.dailymotion.sdkandroid.PlayerWebView>

Then in your Activity code just launch your content.
Get your PlayerWebView then call playVideo("id").

		private PlayerWebView mVideoView;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.screen_sample);

	        mVideoView = (PlayerWebView) findViewById(R.id.dm_player_web_view);
            mVideoView.playVideo("x26hv6c");


	    }

### Handle screen rotation
For the screen rotation to be handled correctly, you need to add

	android:configChanges="orientation|screenSize"

to any activity using PlayerWebView, in your AndroidManifest.xml

### Lifecycle
On Android 3.0+, you have to call onPause and onResume when these events occur in your lifecycle :

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

### Publish your own sdk on Bintray

Update your local.properties files with this lines and replace <user> and <api.key> values`

```
bintray.user=<user>
bintray.apikey=<api.key>
```

In your terminal call `gradlew install` then `gradlew bintrayUpload` 