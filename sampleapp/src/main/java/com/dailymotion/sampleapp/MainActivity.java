package com.dailymotion.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.LinkedList;

public class MainActivity extends ToolbarActivity {
    private LinkedList<Screen> mActiveScreens = new LinkedList<>();

    private MenuItem.OnMenuItemClickListener mAboutClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
    };
    private MenuItem.OnMenuItemClickListener mPreferencesClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Screen screen = new ScreenChoice(this);
        pushScreen(screen);

    }

    public void pushScreen(Screen screen) {
        if (mActiveScreens.size() > 0) {
            mActiveScreens.getLast().onStop();
            View v = mContainer.getChildAt(mContainer.getChildCount() - 1);
            v.setVisibility(View.INVISIBLE);
        }

        mActiveScreens.push(screen);

        mContainer.addView(screen.getView());

        screen.onStart();
        updateBackButtonAndMenu();
    }

    public void popScreen() {
        if (mActiveScreens.size() <= 1) {
            return;
        }

        Screen screen = mActiveScreens.pop();
        screen.onStop();
        mContainer.removeViewAt(mContainer.getChildCount() - 1);
        screen.onDestroy();

        View v = mContainer.getChildAt(mContainer.getChildCount() - 1);
        v.setVisibility(View.VISIBLE);

        mActiveScreens.getLast().onStart();
    }

    @Override
    public void onBackPressed() {
        if (mActiveScreens.size() == 1) {
            finish();
        } else {
            popScreen();
        }

        updateBackButtonAndMenu();
    }

    private void updateBackButtonAndMenu() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(mActiveScreens.size() > 1);
        Menu menu = getToolbar().getMenu();
        menu.clear();
        MenuItem item = menu.add(getString(R.string.action_about));
        item.setOnMenuItemClickListener(mAboutClickListener);
        item = menu.add(getString(R.string.action_settings));
        item.setOnMenuItemClickListener(mPreferencesClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
