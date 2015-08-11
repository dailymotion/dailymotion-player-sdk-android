package com.dailymotion.sampleapp;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ScreenChoice extends Screen {

    public ScreenChoice(MainActivity activity) {
        super(activity);
    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                mActivity.pushScreen(new ScreenVideoList(mActivity));
            } else {
                mActivity.pushScreen(new ScreenLogin(mActivity));
            }
        }
    };

    class ChoiceAdapter extends ArrayAdapter<String> {

        public ChoiceAdapter(Context context, int resource, String[] choices) {
            super(context, resource, choices);
        }
    }

    @Override
    public View onCreateView() {

        ListView listView = new ListView(mActivity);

        String [] choices = new String[2];
        choices[0] = mActivity.getString(R.string.videoList);
        choices[1] = mActivity.getString(R.string.broadcast);

        listView.setAdapter(new ChoiceAdapter(mActivity, R.layout.item_choice, choices));
        listView.setOnItemClickListener(mOnClickListener);
        return listView;
    }

}
