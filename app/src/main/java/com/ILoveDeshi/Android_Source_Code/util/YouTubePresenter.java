package com.ILoveDeshi.Android_Source_Code.util;


import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.SubscribeActivity;
import com.ILoveDeshi.Android_Source_Code.interfaces.YouTubeActivityView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;


public class YouTubePresenter {
    private final YouTubeActivityView view;
    private final SubscribeActivity activity;

    public YouTubePresenter(YouTubeActivityView view, SubscribeActivity activity) {
        this.view = view;
        this.activity = activity;
    }

    public void subscribeToYouTubeChannel(GoogleAccountCredential mCredential, String channelId) {

        new MakeRequestTask(mCredential, channelId).execute(); // creating AsyncTask for channel subscribe

    }

    @SuppressLint("StaticFieldLeak")
    private class MakeRequestTask extends AsyncTask<Object, Object, Subscription> {
        private YouTube mService = null;
        private String channelId;

        MakeRequestTask(GoogleAccountCredential credential, String channelId) {
            this.channelId = channelId;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(activity.getResources().getString(R.string.app_name))
                    .build();
        }

        @Override
        protected Subscription doInBackground(Object... params) {
            Subscription response = null;
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet");
            Subscription subscription = new Subscription();
            SubscriptionSnippet snippet = new SubscriptionSnippet();
            ResourceId resourceId = new ResourceId();
            resourceId.set("channelId", channelId);
            resourceId.set("kind", "youtube#channel");
            snippet.setResourceId(resourceId);
            subscription.setSnippet(snippet);
            YouTube.Subscriptions.Insert subscriptionsInsertRequest = null;
            try {
                subscriptionsInsertRequest = mService.subscriptions().insert(Objects.requireNonNull(parameters.get("part")), subscription);
                response = subscriptionsInsertRequest.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(Subscription subscription) {
            super.onPostExecute(subscription);
            if (subscription != null) {
                view.onSubscribetionSuccess(subscription.getSnippet().getTitle());
            } else {
                view.onSubscribetionFail();
            }
        }
    }
}
