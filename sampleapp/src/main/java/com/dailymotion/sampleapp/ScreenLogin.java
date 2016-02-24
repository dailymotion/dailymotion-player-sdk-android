package com.dailymotion.sampleapp;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dailymotion.sdk.api.Api;
import com.dailymotion.sdk.api.ApiRequest;
import com.dailymotion.sdk.api.model.PagedList;
import com.dailymotion.sdk.api.model.User;
import com.dailymotion.sdk.api.model.Video;
import com.dailymotion.sdk.httprequest.HttpRequest;
import com.dailymotion.sdk.httprequest.JsonRequest;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.Map;

public class ScreenLogin extends Screen {
    private EditText mUsername;
    private EditText mPassword;
    private Button mButton;
    private ProgressDialog mDialog;
    private View mView;

    static class MeRequest extends ApiRequest<PagedList<Video>> {

        public MeRequest() {
            super(GET, "me/", new TypeToken<User>(){}.getType());
            requiresOAuth = true;
        }

        @Override
        protected Map<String, String> getGetParams() {
            Map<String, String> map = super.getGetParams();

            map.put("fields", Api.computeFlagsFor(User.class));

            return map;
        }
    }

    private HttpRequest.RequestListener mRequestListener = new HttpRequest.RequestListener<User>() {
        @Override
        public void onRequestCompleted(HttpRequest<User> request, User response, HttpRequest.Error error) {

            mDialog.dismiss();

            if (response != null && error == null) {
                mActivity.popScreen();
                mActivity.pushScreen(new ScreenBroadcast(mActivity));
            } else {
                Snackbar.make(mView, mActivity.getString(R.string.bad_username_or_password), Snackbar.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Api.setLogin(mUsername.getText().toString(), mPassword.getText().toString());

            mDialog = ProgressDialog.show(mActivity, "", mActivity.getString(R.string.loading));
            Api.queue(new MeRequest(), mRequestListener);

        }
    };

    public ScreenLogin(MainActivity activity) {
        super(activity);
    }

    @Override
    protected View onCreateView() {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.screen_login, null);
        mUsername = (EditText)mView.findViewById(R.id.username);
        mPassword = (EditText)mView.findViewById(R.id.password);

        if (BuildConfig.defaultLogin.length() > 0 && BuildConfig.defaultPassword.length() > 0) {
            mUsername.setText(BuildConfig.defaultLogin);
            mPassword.setText(BuildConfig.defaultPassword);
        }

        mButton = (Button)mView.findViewById(R.id.button);

        mButton.setOnClickListener(mClickListener);
        return mView;
    }
}
