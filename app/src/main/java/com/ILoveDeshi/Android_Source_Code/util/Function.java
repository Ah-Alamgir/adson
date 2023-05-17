package com.ILoveDeshi.Android_Source_Code.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.Login;
import com.ILoveDeshi.Android_Source_Code.interfaces.FullScreen;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.interfaces.VideoAd;
import com.ILoveDeshi.Android_Source_Code.response.SubmitSecureWorldRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Function {

    private final Activity activity;
    private OnClick onClick;
    private VideoAd videoAd;
    public static Dialog customDialog;
    public int positions;
    public String titles;
    public String types;
    public String statusTypes;
    public String ids;
    private int random;
    public String tags;
    public static boolean loginBack = false;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "status";
    public String prefLogin = "pref_login";
    public String profileId = "profileId";
    public String userName = "userName";
    public String userImage = "userImage";
    public String loginType = "loginType";
    public String showLogin = "show_login";
    public String notification = "notification";
    public String verificationCode = "verification_code";
    public String isVerification = "is_verification";
    public String themSetting = "them";
    public String IS_WELCOME = "is_welcome";
    public String IS_LANGUAGE = "is_language";
    public String regName = "reg_name";
    public String regEmail = "reg_email";
    public String regPassword = "reg_password";
    public String regPhoneNo = "reg_phoneNo";
    public String regReference = "reg_reference";
    public String languageIds = "language_ids";

    @SuppressLint("CommitPrefEdits")
    public Function(Activity activity) {
        this.activity = activity;
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Function(Activity activity, VideoAd videoAd) {
        this.activity = activity;
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
        this.videoAd = videoAd;
    }

    @SuppressLint("CommitPrefEdits")
    public Function(Activity activity, OnClick onClick) {
        this.activity = activity;
        this.onClick = onClick;
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Function(Activity activity, OnClick onClick, VideoAd videoAd, FullScreen fullScreen) {
        this.activity = activity;
        this.onClick = onClick;
        this.videoAd = videoAd;
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    public void login() {
        String firstTime = "firstTime";
        if (!pref.getBoolean(firstTime, false)) {
            editor.putBoolean(prefLogin, false);
            editor.putBoolean(firstTime, true);
            editor.commit();
        }
    }

    public void setFirstWelcome(boolean isFirstTime) {
        editor.putBoolean(IS_WELCOME, isFirstTime);
        editor.commit();
    }

    public boolean isWelcome() {
        return pref.getBoolean(IS_WELCOME, true);
    }

    public boolean isLanguage() {
        return pref.getBoolean(IS_LANGUAGE, true);
    }

    public boolean isLogin() {
        return pref.getBoolean(prefLogin, false);
    }

    public String userId() {
        return pref.getString(profileId, "");
    }

    public String getLoginType() {
        return pref.getString(loginType, "");
    }

    public String getLanguageIds() {
        return pref.getString(languageIds, "");
    }

    public String getTheme() {
        return pref.getString(themSetting, "system");
    }

    @SuppressLint("HardwareIds")
    public String getDeviceId() {
        String deviceId;
        try {
            deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            deviceId = "NotFound";
        }
        return deviceId;
    }

    public void forceRTLIfSupported() {
        if (activity.getResources().getString(R.string.isRTL).equals("true")) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public boolean isRtl() {
        return activity.getResources().getString(R.string.isRTL).equals("true");
    }

    public void changeStatusBarColor() {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarGradiant(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        point.x = display.getWidth();
        point.y = display.getHeight();
        columnWidth = point.x;
        return columnWidth;
    }

    public String okGoogle(String videoUrl) {
        try {
            final String reg = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";
            if (videoUrl == null || videoUrl.trim().length() <= 0)
                return null;
            Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(videoUrl);
            if (matcher.find())
                return matcher.group(1);
        } catch (Exception e) {
            alertBox("coming soon...");
        }
        return null;
    }

    public void VideoAdDialog(String type) {
        if (Constant.appRP != null) {
            positions = 0;
            titles = "";
            types = type;
            statusTypes = "";
            ids = "";
            tags = "";
        } else {
            callVideoAdData(type);
        }
    }

    private void callVideoAdData(String videoAdType) {
        videoAd.videoAdClick(videoAdType);
    }

    public void onClickData(int position, String title, String type, String statusType, String id, String tag) {
        showProgressDialog(activity);
        if (Constant.appRP != null) {
            Constant.adCount = Constant.adCount + 1;
            if (Constant.adCount == Constant.adCountShow) {
                Constant.adCount = 0;
                positions = position;
                titles = title;
                types = type;
                statusTypes = statusType;
                ids = id;
                tags = tag;
            } else {
                hideProgressDialog(activity);
                onClick.position(position, title, type, statusType, id, tag);
            }
        } else {
            hideProgressDialog(activity);
            onClick.position(position, title, type, statusType, id, tag);
        }

    }

    public void alertBox(String message) {

        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                    builder.setMessage(Html.fromHtml(message));
                    builder.setCancelable(false);
                    builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                            (arg0, arg1) -> {

                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }
    }

    public boolean isVPN(Activity ac) {
        boolean vpnInUse = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) ac.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            @SuppressLint("MissingPermission") Network activeNetwork = connectivityManager.getActiveNetwork();
            @SuppressLint("MissingPermission") NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
            return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        }
        @SuppressLint("MissingPermission") Network[] networks = connectivityManager.getAllNetworks();
        for (Network network : networks) {
            @SuppressLint("MissingPermission") NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(network);
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                vpnInUse = true;
                break;
            }
        }
        return vpnInUse;
    }

    public void suspend(String message) {
        if (isLogin()) {
            String type_login = pref.getString(loginType, "");
            if (type_login.equals("google")) {
                GoogleSignInClient mGoogleSignInClient;
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
            } else if (type_login.equals("facebook")) {
                LoginManager.getInstance().logOut();
            }

            editor.putBoolean(prefLogin, false);
            editor.commit();
        }

        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                    builder.setMessage(Html.fromHtml(message));
                    builder.setCancelable(false);
                    builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                            (arg0, arg1) -> {
                                activity.startActivity(new Intent(activity, Login.class));
                                activity.finishAffinity();
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }

    }

    public boolean isDarkMode() {
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            default:
                return false;
        }
    }

    public String webViewText() {
        String color;
        if (isDarkMode()) {
            color = Constant.webTextDark;
        } else {
            color = Constant.webTextLight;
        }
        return color;
    }

    public String webViewLink() {
        String color;
        if (isDarkMode()) {
            color = Constant.webLinkDark;
        } else {
            color = Constant.webLinkLight;
        }
        return color;
    }

    public String isWebViewTextRtl() {
        String isRtl;
        if (isRtl()) {
            isRtl = "rtl";
        } else {
            isRtl = "ltr";
        }
        return isRtl;
    }

    public String imageGalleryToolBar() {
        String color;
        if (isDarkMode()) {
            color = Constant.darkGallery;
        } else {
            color = Constant.lightGallery;
        }
        return color;
    }

    public String imageGalleryProgressBar() {
        String color;
        if (isDarkMode()) {
            color = Constant.progressBarDarkGallery;
        } else {
            color = Constant.progressBarLightGallery;
        }
        return color;
    }

    public boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean verifyDeviceId(Activity context) {
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && validInstallers.contains(installer);
    }

    public boolean ImReallyFast(Activity context, String fast) {
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installer = context.getPackageManager().getInstallerPackageName(fast);
        return installer != null && validInstallers.contains(installer);
    }

    public boolean isGoodMan(Activity con, String SuperMan) {
        PackageManager pm = con.getPackageManager();
        return isPackageInstalled(SuperMan, pm);
    }

    public boolean isAppSecure(final Context ctx, final String packageName) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public void rateApp(String id) {
        Uri uri = Uri.parse("market://details?id=" + id);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
//            activity.startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://play.google.com/store/apps/details?id=" + id)));
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(id));
            i.setPackage("com.android.vending");
            activity.startActivity(i);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(goToMarket);
        }
    }

    public void appWorld(Activity act, String packageName) {
        Intent i = act.getPackageManager().getLaunchIntentForPackage(packageName);
        act.startActivity(i);
        // launch from play store on 1st june 2022 // enable below lines

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setData(Uri.parse("market://details?id=" + packageName));
//        act.startActivity(intent);
    }

    public void secureThisWorld(String userId, String type, String id, int point) {
        showProgressDialog(activity);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("type", type);
        jsObj.addProperty("id", id);
        jsObj.addProperty("ponints", String.valueOf(point));
        jsObj.addProperty("AUM", "secureWorld");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubmitSecureWorldRP> call = apiService.submitSecureWorld(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubmitSecureWorldRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Response<SubmitSecureWorldRP> response) {
                try {
                    SubmitSecureWorldRP submitSpinnerRP = response.body();
                    if (Objects.requireNonNull(submitSpinnerRP).getSuccess().equals("1")) {
                        alertBox(submitSpinnerRP.getMsg());
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    alertBox(activity.getResources().getString(R.string.failed_try_again));
                }
                hideProgressDialog(activity);
            }

            @Override
            public void onFailure(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                hideProgressDialog(activity);
                alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void secureAllWorld(String userId, String type, String point) {
        showProgressDialog(activity);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("type", type);
        jsObj.addProperty("ponints", point);
        jsObj.addProperty("AUM", "secureWorlds");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubmitSecureWorldRP> call = apiService.submitSecureWorld(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubmitSecureWorldRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Response<SubmitSecureWorldRP> response) {
                try {
                    SubmitSecureWorldRP submitSpinnerRP = response.body();
                    if (Objects.requireNonNull(submitSpinnerRP).getSuccess().equals("1")) {
                        alertBox(submitSpinnerRP.getMsg());
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    alertBox(activity.getResources().getString(R.string.failed_try_again));
                }
                hideProgressDialog(activity);
            }

            @Override
            public void onFailure(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                hideProgressDialog(activity);
                alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void secureTheWorld(String userId, String type, String point, String id) {
        showProgressDialog(activity);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("id", id);
        jsObj.addProperty("type", type);
        jsObj.addProperty("ponints", point);
        jsObj.addProperty("AUM", "secureTheWorld");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubmitSecureWorldRP> call = apiService.submitSecureWorld(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubmitSecureWorldRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Response<SubmitSecureWorldRP> response) {
                try {
                    SubmitSecureWorldRP submitSpinnerRP = response.body();
                    if (Objects.requireNonNull(submitSpinnerRP).getSuccess().equals("1")) {
                        alertBox(submitSpinnerRP.getMsg());
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    alertBox(activity.getResources().getString(R.string.failed_try_again));
                }
                hideProgressDialog(activity);
            }

            @Override
            public void onFailure(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                hideProgressDialog(activity);
                alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void secureTheSubs(String userId, String type, String point, String id) {
        showProgressDialog(activity);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("id", id);
        jsObj.addProperty("type", type);
        jsObj.addProperty("ponints", point);
        jsObj.addProperty("AUM", "secureTheSubs");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubmitSecureWorldRP> call = apiService.submitSecureWorld(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubmitSecureWorldRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Response<SubmitSecureWorldRP> response) {
                try {
                    SubmitSecureWorldRP submitSpinnerRP = response.body();
                    if (Objects.requireNonNull(submitSpinnerRP).getSuccess().equals("1")) {
                        alertBox(submitSpinnerRP.getMsg());
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    alertBox(activity.getResources().getString(R.string.failed_try_again));
                }
                hideProgressDialog(activity);
            }

            @Override
            public void onFailure(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                hideProgressDialog(activity);
                alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void secureTheVdo(String userId, String type, String point, String id) {
        showProgressDialog(activity);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("id", id);
        jsObj.addProperty("type", type);
        jsObj.addProperty("ponints", point);
        jsObj.addProperty("AUM", "secureTheVdo");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubmitSecureWorldRP> call = apiService.submitSecureWorld(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubmitSecureWorldRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Response<SubmitSecureWorldRP> response) {
                try {
                    SubmitSecureWorldRP submitSpinnerRP = response.body();
                    if (Objects.requireNonNull(submitSpinnerRP).getSuccess().equals("1")) {
                        alertBox(submitSpinnerRP.getMsg());
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    alertBox(activity.getResources().getString(R.string.failed_try_again));
                }
                hideProgressDialog(activity);
            }

            @Override
            public void onFailure(@NotNull Call<SubmitSecureWorldRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                hideProgressDialog(activity);
                alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void showProgressDialog(Activity activity) {
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
        customDialog = new Dialog(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        @SuppressLint("InflateParams") View mView = inflater.inflate(R.layout.progress_dialog, null);
        customDialog.setCancelable(false);
        customDialog.setContentView(mView);
        if (!customDialog.isShowing() && !activity.isFinishing()) {
            customDialog.show();
        }
    }

    public void hideProgressDialog(Activity activity) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

    public void minimizeApp(Activity ac) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ac.startActivity(startMain);
    }

    public void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public void gameBox(String message) {
        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                    builder.setMessage(Html.fromHtml(message));
                    builder.setCancelable(false);
                    builder.setPositiveButton(activity.getResources().getString(R.string.collectPoints),
                            (arg0, arg1) -> {

                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }
    }

    public static void setToast(Context _mContext, String str) {
        Toast toast = Toast.makeText(_mContext, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void getOut(int position, String title, String type, String statusType, String id, String tag) {
        hideProgressDialog(activity);
        if (type.equals("btn_click")) {
            callVideoAdData(type);
        } else {
            onClick.position(position, title, type, statusType, id, tag);
        }
    }

    public int generateAdsCode() {
        final int min = 1;
        final int max = 9;
        random = new Random().nextInt((max - min) + 1) + min;
        return random;
    }

}
