Dailymotion Android Web-SDK
===========================

This SDK aims at easily embedding Dailymotion videos on your Android application using WebView. It supports Android 2.3 and superior. Although it is Android Studio and Gradle based, it can easily be used in Eclipse.
The SDK is bundled with a sample application

Features
--------

- Dead simple to use. No need to specify a layout container for the VideoView
- Supports Android 2.3 and superior

How to use
----------

### Add the SDK to your project
You can either import the SDK using your IDE or integrate DMWebVideoView.java in your project.

### Edit your AndroidManifest.xml file
Add
        android:hardwareAccelerated="true"
        <uses-permission android:name="android.permission.INTERNET" />

to the application tag.

### Use in your Activity or Fragment
First, add the DMWebVideoView in your layout in place of the regular WebView.

	   <com.dailymotion.websdk.DMWebVideoView
            android:layout_width="300dip"
            android:layout_height="200dip"
            android:id="@+id/dmWebVideoView"
        />

Then in your Activity code just launch your content. You have two options.
If your web page contains more than just a video, load the url content like this :

	  	private DMWebVideoView mVideoView;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.screen_sample);

	        mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView));
	        mVideoView.loadUrl("http://orange.jobs/jobs/mobi.do?do=getOffer&lang=FR&id=28866");

	    }

If you have the id of a video on Dailymotion, then you can set it directly :

		private DMWebVideoView mVideoView;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.screen_sample);

	        mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView));
	        mVideoView.setVideoId("x10iisk");


	    }

### Handling back button
Your activity must take care of the back button so as to leave fullscreen and not the current Activity :

		 @Override
            public void onBackPressed() {
                mVideoView.handleBackPress(this);
            }


### Handle screen rotation
For the screen rotation to be handled correctly, you need to add

	android:configChanges="orientation|screenSize"

to any activity using DMWebVideoView, in your AndroidManifest.xml

### Handling automatic fullscreen
You can prevent the application to automatically switch to native fullscreen with setAllowAutomaticNativeFullscreen(boolean) method
Please note that this only has effect Android 3.x and superior

### Auto play
You can make the video to start as soon as the player is loaded. To do so, either call setAutoPlay(true) before setting the id of your
video or call setVideoId(<video_id>, true)

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
