package com.ILoveDeshi.Android_Source_Code.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;

import androidx.multidex.MultiDex;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.SplashScreen;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class YouApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.initWithContext(this);
        OneSignal.setAppId(Constant.OneSignalAppID);
        OneSignal.setNotificationOpenedHandler(new NotificationExtenderExample());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/patua.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }

    class NotificationExtenderExample implements OneSignal.OSNotificationOpenedHandler {

        private String url, id, type, statusType, titleName;

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {

            try {
                url = result.getNotification().getAdditionalData().getString("external_link");
                type = result.getNotification().getAdditionalData().getString("type");
                switch (type) {
                    case "single_status":
                        id = result.getNotification().getAdditionalData().getString("id");
                        statusType = result.getNotification().getAdditionalData().getString("status_type");
                        titleName = result.getNotification().getAdditionalData().getString("title");
                        break;
                    case "category":
                        id = result.getNotification().getAdditionalData().getString("id");
                        titleName = result.getNotification().getAdditionalData().getString("title");
                        break;
                    case "account_status":
                        id = result.getNotification().getAdditionalData().getString("id");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent;
            if (!url.equals("false") && !url.trim().isEmpty()) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
            } else {
                intent = new Intent(YouApplication.this, SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", type);
                if (type.equals("account_status") || type.equals("single_status") || type.equals("category")) {
                    intent.putExtra("id", id);
                }
                if (type.equals("single_status")) {
                    intent.putExtra("status_type", statusType);
                    intent.putExtra("title", titleName);
                }
                if (type.equals("category")) {
                    intent.putExtra("title", titleName);
                }
            }
            startActivity(intent);

        }
    }

}
