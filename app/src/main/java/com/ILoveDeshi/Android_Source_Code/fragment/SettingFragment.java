package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ILoveDeshi.Android_Source_Code.activity.AVStatus;
import com.ILoveDeshi.Android_Source_Code.activity.AboutUs;
import com.ILoveDeshi.Android_Source_Code.activity.AccountVerification;
import com.ILoveDeshi.Android_Source_Code.activity.ContactUs;
import com.ILoveDeshi.Android_Source_Code.activity.DeleteAccount;
import com.ILoveDeshi.Android_Source_Code.activity.Faq;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.activity.PointDetail;
import com.ILoveDeshi.Android_Source_Code.activity.PrivacyPolicy;
import com.ILoveDeshi.Android_Source_Code.activity.SplashScreen;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.ProfileStatusRP;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingFragment extends Fragment {

    private Function function;
    private String them_mode;

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.setting));
        }

        function = new Function(requireActivity());
        SwitchMaterial switchMaterial = view.findViewById(R.id.switch_setting);
        MaterialTextView textViewShareApp = view.findViewById(R.id.textView_shareApp_setting);
        MaterialTextView textViewRateApp = view.findViewById(R.id.textView_rateApp_setting);
        MaterialTextView textViewMoreApp = view.findViewById(R.id.textView_moreApp_setting);
        MaterialTextView textViewPrivacyPolicy = view.findViewById(R.id.textView_privacy_policy_setting);
        MaterialTextView textViewAboutUs = view.findViewById(R.id.textView_aboutUs_setting);
        MaterialTextView textViewLanguage = view.findViewById(R.id.textView_language_setting);
        MaterialTextView textViewContactUs = view.findViewById(R.id.textView_contactUs_setting);
        MaterialTextView textViewFaq = view.findViewById(R.id.textView_faq_setting);
        MaterialTextView textViewPoint = view.findViewById(R.id.textView_point_setting);
        MaterialTextView textViewVerification = view.findViewById(R.id.textView_verification_setting);
        MaterialTextView textViewDeleteAccount = view.findViewById(R.id.textView_deleteAccount_setting);
        MaterialTextView textViewThemType = view.findViewById(R.id.textView_themType_setting);
        ConstraintLayout conThem = view.findViewById(R.id.rel_them_setting);
        View viewDeleteAccount = view.findViewById(R.id.view_deleteAccount_setting);
        View viewVerification = view.findViewById(R.id.view_verification_setting);
        final MaterialTextView textViewSize = view.findViewById(R.id.textView_size_setting);
        ImageView imageViewClear = view.findViewById(R.id.imageView_clear_setting);
        ImageView imageView = view.findViewById(R.id.imageView_them_setting);

        if (function.isDarkMode()) {
            Glide.with(requireActivity().getApplicationContext()).load(R.drawable.mode_dark)
                    .placeholder(R.drawable.placeholder_portable)
                    .into(imageView);
        } else {
            Glide.with(requireActivity().getApplicationContext()).load(R.drawable.mode_icon)
                    .placeholder(R.drawable.placeholder_portable)
                    .into(imageView);
        }
        switch (function.getTheme()) {
            case "system":
                textViewThemType.setText(getResources().getString(R.string.system_default));
                break;
            case "light":
                textViewThemType.setText(getResources().getString(R.string.light));
                break;
            case "dark":
                textViewThemType.setText(getResources().getString(R.string.dark));
                break;
            default:
                break;
        }

        if (function.isLogin()) {
            textViewDeleteAccount.setVisibility(View.VISIBLE);
            viewDeleteAccount.setVisibility(View.VISIBLE);
            textViewVerification.setVisibility(View.VISIBLE);
            viewVerification.setVisibility(View.VISIBLE);
        } else {
            textViewDeleteAccount.setVisibility(View.GONE);
            viewDeleteAccount.setVisibility(View.GONE);
            textViewVerification.setVisibility(View.GONE);
            viewVerification.setVisibility(View.GONE);
        }

        double total = 0;
        String root = requireActivity().getExternalCacheDir().getAbsolutePath();
        try {
            File file = new File(root);
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String aChildren : children) {
                    File name = new File(root + "/" + aChildren);
                    total += getFileSizeMegaBytes(name);
                }
            }
        } catch (Exception e) {
            textViewSize.setText("Size " + "0.0" + " mb");
        }
        textViewSize.setText(getResources().getString(R.string.size) + " "
                + new DecimalFormat("##.##").format(total) + " "
                + getResources().getString(R.string.mb));

        imageViewClear.setOnClickListener(v -> {
            String root1 = getActivity().getExternalCacheDir().getAbsolutePath();
            File file = new File(root1);
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String aChildren : children) {
                    new File(file, aChildren).delete();
                }
                Toast.makeText(getActivity(), getResources().getString(R.string.locally_cached_data), Toast.LENGTH_SHORT).show();
                textViewSize.setText(getResources().getString(R.string.size) + " "
                        + "0.0" + " "
                        + getResources().getString(R.string.mb));
            }
        });

        switchMaterial.setChecked(function.pref.getBoolean(function.notification, true));

        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            OneSignal.unsubscribeWhenNotificationsAreDisabled(isChecked);
            function.editor.putBoolean(function.notification, isChecked);
            function.editor.commit();
        });

        textViewShareApp.setOnClickListener(v -> shareApp());

        textViewRateApp.setOnClickListener(v -> rateApp());

        textViewMoreApp.setOnClickListener(v -> moreApp());

        textViewAboutUs.setOnClickListener(v -> startActivity(new Intent(getActivity(), AboutUs.class)));

        textViewPrivacyPolicy.setOnClickListener(v -> startActivity(new Intent(getActivity(), PrivacyPolicy.class)));

        textViewContactUs.setOnClickListener(v -> startActivity(new Intent(getActivity(), ContactUs.class)));

        textViewFaq.setOnClickListener(v -> startActivity(new Intent(getActivity(), Faq.class)));

        textViewPoint.setOnClickListener(v -> startActivity(new Intent(getActivity(), PointDetail.class)));

        textViewVerification.setOnClickListener(v -> {
            if (function.isNetworkAvailable()) {
                if (function.isLogin()) {
                    request(function.userId());
                } else {
                    function.alertBox(getResources().getString(R.string.you_have_not_login));
                }
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        });

        textViewDeleteAccount.setOnClickListener(v -> {
            if (function.isLogin()) {
                startActivity(new Intent(getActivity(), DeleteAccount.class));
            } else {
                function.alertBox(getResources().getString(R.string.you_have_not_login));
            }
        });

        conThem.setOnClickListener(v -> {

            Dialog dialog = new Dialog(getActivity());
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
                startActivity(new Intent(getActivity(), SplashScreen.class));
                getActivity().finishAffinity();

            });
            textViewCancel.setOnClickListener(vTextViewCancel -> dialog.dismiss());
            dialog.show();
        });

        setHasOptionsMenu(false);
        return view;

    }

    private void request(String userId) {

        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("AUM", "profile_status");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProfileStatusRP> call = apiService.getProfileStatus(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProfileStatusRP>() {
                @Override
                public void onResponse(@NotNull Call<ProfileStatusRP> call, @NotNull Response<ProfileStatusRP> response) {

                    try {
                        ProfileStatusRP profileStatusRP = response.body();
                        if (Objects.requireNonNull(profileStatusRP).getStatus().equals("1")) {
                            if (profileStatusRP.getSuccess().equals("1")) {
                                switch (profileStatusRP.getProfile_status()) {
                                    case "0":
                                    case "1":
                                    case "2":
                                        startActivity(new Intent(getActivity(), AVStatus.class));
                                        break;
                                    case "3":
                                        startActivity(new Intent(getActivity(), AccountVerification.class)
                                                .putExtra("name", profileStatusRP.getName()));
                                        break;
                                }

                            } else {
                                function.alertBox(profileStatusRP.getMsg());
                            }

                        } else if (profileStatusRP.getStatus().equals("2")) {
                            function.suspend(profileStatusRP.getMessage());
                        } else {
                            function.alertBox(profileStatusRP.getMessage());
                        }

                    } catch (Exception e) {
                        Log.d("exception_error", e.toString());
                        function.alertBox(getResources().getString(R.string.failed_try_again));
                    }
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<ProfileStatusRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }

    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + requireActivity().getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName())));
        }
    }

    private void moreApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.play_more_app))));
    }

    private void shareApp() {

        try {

            String string = "\n" + getResources().getString(R.string.Let_me_recommend_you_this_application) + "\n\n" + "https://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, string);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_one)));

        } catch (Exception e) {
            //e.toString();
        }

    }

    private static double getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }
    }
}
