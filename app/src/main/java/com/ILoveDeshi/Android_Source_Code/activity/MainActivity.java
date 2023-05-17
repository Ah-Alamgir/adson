package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.ILoveDeshi.Android_Source_Code.fragment.GameListFragment;
import com.ILoveDeshi.Android_Source_Code.fragment.HomeFragment;
import com.ILoveDeshi.Android_Source_Code.fragment.MoreAppsFragment;
import com.ILoveDeshi.Android_Source_Code.fragment.SearchFragment;
import com.ILoveDeshi.Android_Source_Code.fragment.WebsitesFragment;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.BuildConfig;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.fragment.ProfileFragment;
import com.ILoveDeshi.Android_Source_Code.fragment.ReferenceCodeFragment;
import com.ILoveDeshi.Android_Source_Code.fragment.RewardPointFragment;
import com.ILoveDeshi.Android_Source_Code.fragment.SettingFragment;
import com.ILoveDeshi.Android_Source_Code.response.AppRP;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity {

    private Function function;
    private String them_mode;
    public static MaterialToolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MaterialTextView textViewAppName;
    private ConstraintLayout userLayout, rewLayout;
    public static CircleImageView userProfile;
    private boolean isAdMOb = false;
    private boolean doubleBackToExitPressedOnce = false;
    private String id, type = "", statusType, title;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);

        function = new Function(MainActivity.this);
        function.forceRTLIfSupported();
        if (getIntent().hasExtra("type")) {
            id = getIntent().getStringExtra("id");
            type = getIntent().getStringExtra("type");
            statusType = getIntent().getStringExtra("status_type");
            title = getIntent().getStringExtra("title");
        }

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        function.setStatusBarGradiant(MainActivity.this);
        setSupportActionBar(toolbar);

        userProfile = findViewById(R.id.userProfile);
        userLayout = findViewById(R.id.userLayout);
        rewLayout = findViewById(R.id.rewLayout);
        ConstraintLayout home = findViewById(R.id.conHome);
        ConstraintLayout wheel = findViewById(R.id.conSpinner);
        ConstraintLayout latestApp = findViewById(R.id.conLatestApp);
        ConstraintLayout popularApp = findViewById(R.id.conPopular);
        ConstraintLayout onlineGames = findViewById(R.id.conGames);
        ConstraintLayout featureWeb = findViewById(R.id.conWebsite);
        ConstraintLayout allWeb = findViewById(R.id.conAllWebsite);
        ConstraintLayout refer = findViewById(R.id.conReference);
        ConstraintLayout setting = findViewById(R.id.conSetting);
        ConstraintLayout logout = findViewById(R.id.conLogout);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        textViewAppName = headerLayout.findViewById(R.id.textView_name_nav);

        home.setOnClickListener(v -> {
            backStackRemove();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(), getResources().getString(R.string.homes)).commitAllowingStateLoss();
            drawer.closeDrawers();
            //AppLovinSdk.getInstance(this).showMediationDebugger();
        });
        wheel.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Spinner.class));
            drawer.closeDrawers();
        });
        latestApp.setOnClickListener(v -> {
            MoreAppsFragment moreAppsFragment = new MoreAppsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "home_latest");
            moreAppsFragment.setArguments(bundle);
            MainActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, moreAppsFragment, getResources().getString(R.string.home)).addToBackStack(getResources().getString(R.string.latestApps)).commitAllowingStateLoss();
            drawer.closeDrawers();
        });
        popularApp.setOnClickListener(v -> {
            MoreAppsFragment moreAppsFragment = new MoreAppsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "home_most");
            moreAppsFragment.setArguments(bundle);
            MainActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, moreAppsFragment, getResources().getString(R.string.home)).addToBackStack(getResources().getString(R.string.latestApps)).commitAllowingStateLoss();
            drawer.closeDrawers();
        });
        onlineGames.setOnClickListener(v -> {
            GameListFragment gameListFragment = new GameListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "home_latest");
            gameListFragment.setArguments(bundle);
            MainActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, gameListFragment, getResources().getString(R.string.home)).addToBackStack(getResources().getString(R.string.onlineGames)).commitAllowingStateLoss();
            drawer.closeDrawers();
        });
        featureWeb.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Spinner.class));
            drawer.closeDrawers();
        });
        allWeb.setOnClickListener(v -> {
            WebsitesFragment websitesFragment = new WebsitesFragment();
            Bundle bundleProfile = new Bundle();
            bundleProfile.putString("type", "home_website");
            websitesFragment.setArguments(bundleProfile);
            MainActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, websitesFragment, getResources().getString(R.string.allWebsites)).addToBackStack(getResources().getString(R.string.allWebsites)).commitAllowingStateLoss();
            drawer.closeDrawers();
        });
        refer.setOnClickListener(v -> {
            backStackRemove();
            MainActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new ReferenceCodeFragment(), getResources().getString(R.string.reference_code)).addToBackStack(getResources().getString(R.string.reference_code)).commitAllowingStateLoss();
            drawer.closeDrawers();
        });
        setting.setOnClickListener(v -> {
            MainActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new SettingFragment(), getResources().getString(R.string.setting)).addToBackStack(getResources().getString(R.string.setting)).commitAllowingStateLoss();
            drawer.closeDrawers();
        });
        logout.setOnClickListener(v -> {
            if (function.isLogin()) {
                logout();
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
                finishAffinity();
            }
            drawer.closeDrawers();
        });
        userLayout.setOnClickListener(v -> {
            if (function.isLogin()) {
                backStackRemove();
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle_profile = new Bundle();
                bundle_profile.putString("type", "user");
                bundle_profile.putString("id", function.userId());
                profileFragment.setArguments(bundle_profile);
                MainActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, profileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commitAllowingStateLoss();
            }
            drawer.closeDrawers();
        });
        rewLayout.setOnClickListener(v -> {
            if (function.isLogin()) {
                backStackRemove();
                RewardPointFragment rewardPointFragment_nav = new RewardPointFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                rewardPointFragment_nav.setArguments(bundle);
                MainActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, rewardPointFragment_nav, getResources().getString(R.string.reward_point)).addToBackStack(getResources().getString(R.string.reward_point)).commitAllowingStateLoss();
                type = "";
            }

            drawer.closeDrawers();
        });

        if (function.isNetworkAvailable()) {
            appDetail();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                String title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1).getTag();
                if (title != null) {
                    toolbar.setTitle(title);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    getWindow().clearFlags(1024);
                }
                super.onBackPressed();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.Please_click_BACK_again_to_exit), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
    }

    public void backStackRemove() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void appDetail() {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("AUM", "app_settings");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AppRP> call = apiService.getAppData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AppRP>() {
            @Override
            public void onResponse(@NotNull Call<AppRP> call, @NotNull Response<AppRP> response) {
                try {
                    Constant.appRP = response.body();
                    if (Objects.requireNonNull(Constant.appRP).getStatus().equals("1")) {
                        if (Constant.appRP.getSuccess().equals("1")) {
                            if (Constant.appRP.getApp_update_status().equals("true") && Constant.appRP.getApp_new_version() > BuildConfig.VERSION_CODE) {
                                showUpdateDialog(Constant.appRP.getApp_update_desc(),
                                        Constant.appRP.getApp_redirect_url(),
                                        Constant.appRP.getCancel_update_status());
                            }
                            if (!Constant.appRP.getInterstitial_ad_click().equals("")) {
                                Constant.adCountShow = Integer.parseInt(Constant.appRP.getInterstitial_ad_click());
                            }
                            textViewAppName.setText(Constant.appRP.getApp_name());
                            switch (type) {
                                case "payment_withdraw":
                                    try {
                                        backStackRemove();
                                        toolbar.setTitle(getResources().getString(R.string.reward_point));
                                        RewardPointFragment rewardPointFragment = new RewardPointFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", type);
                                        rewardPointFragment.setArguments(bundle);
                                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, rewardPointFragment, getResources().getString(R.string.reward_point)).commit();
                                        type = "";
                                    } catch (Exception e) {
                                        Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case "category":
                                case "single_status":
                                    break;
                                default:
                                    try {
                                        HomeFragment homeMainFragment = new HomeFragment();
                                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, homeMainFragment, getResources().getString(R.string.home)).commitAllowingStateLoss();
                                    } catch (Exception e) {
                                        Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                            if (!Constant.appRP.isLive_mode()) {
                                rewLayout.setVisibility(View.GONE);
                            }
                        } else {
                            function.alertBox(Constant.appRP.getMsg());
                        }
                    } else {
                        function.alertBox(Constant.appRP.getMessage());
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            }

            @Override
            public void onFailure(@NotNull Call<AppRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onPause() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(1024);
        super.onPause();
    }

    public void status() {

    }

    public void logout() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.DialogTitleTextStyle);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.logout_message));
        builder.setPositiveButton(getResources().getString(R.string.logout),
                (arg0, arg1) -> {
                    OneSignal.sendTag("user_id", function.userId());
                    if (function.getLoginType().equals("google")) {

                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(MainActivity.this, task -> {
                                    function.editor.putBoolean(function.prefLogin, false);
                                    function.editor.commit();
                                    startActivity(new Intent(MainActivity.this, Login.class));
                                    finishAffinity();
                                });
                    } else if (function.getLoginType().equals("facebook")) {
                        LoginManager.getInstance().logOut();
                        function.editor.putBoolean(function.prefLogin, false);
                        function.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    } else {
                        function.editor.putBoolean(function.prefLogin, false);
                        function.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                (dialogInterface, i) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showUpdateDialog(String description, String link, String isCancel) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_app);
        dialog.setCancelable(false);
        if (function.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        MaterialTextView textViewDescription = dialog.findViewById(R.id.textView_description_dialog_update);
        MaterialButton buttonUpdate = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        if (isCancel.equals("true")) {
            buttonCancel.setVisibility(View.VISIBLE);
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        textViewDescription.setText(description);

        buttonUpdate.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.status_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.download_status_menu) {
            changeTheme();
        } else if (item.getItemId() == R.id.searchThings) {
            SearchFragment searchFragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, searchFragment, getString(R.string.searc)).commitAllowingStateLoss();
        } else if (item.getItemId() == R.id.faqMenu) {
            startActivity(new Intent(MainActivity.this, Faq.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private void changeTheme() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox_them);
        if (function.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setLayout(ViewPager.LayoutParams.FILL_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_them);
        MaterialTextView textViewOk = dialog.findViewById(R.id.textView_ok_them);
        MaterialTextView textViewCancel = dialog.findViewById(R.id.textView_cancel_them);

        switch (function.getTheme()) {
            case "system":
                radioGroup.check(radioGroup.getChildAt(0).getId());
                break;
            case "light":
                radioGroup.check(radioGroup.getChildAt(1).getId());
                break;
            case "dark":
                radioGroup.check(radioGroup.getChildAt(2).getId());
                break;
            default:
                break;
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            MaterialRadioButton rb = group.findViewById(checkedId);
            if (null != rb && checkedId > -1) {
                switch (checkedId) {
                    case R.id.radioButton_system_them:
                        them_mode = "system";
                        break;
                    case R.id.radioButton_light_them:
                        them_mode = "light";
                        break;
                    case R.id.radioButton_dark_them:
                        them_mode = "dark";
                        break;
                    default:
                        break;
                }
            }
        });
        textViewOk.setOnClickListener(vTextViewOk -> {
            function.editor.putString(function.themSetting, them_mode);
            function.editor.commit();
            dialog.dismiss();
            startActivity(new Intent(MainActivity.this, SplashScreen.class));
            MainActivity.this.finishAffinity();
        });
        textViewCancel.setOnClickListener(vTextViewCancel -> dialog.dismiss());
        dialog.show();
    }
}