package com.ILoveDeshi.Android_Source_Code.fragment;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.ILoveDeshi.Android_Source_Code.activity.Login;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.activity.ViewImage;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.ProfileRP;
import com.ILoveDeshi.Android_Source_Code.response.UserFollowStatusRP;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Function function;
    private Animation myAnim;
    private String type, getUserId;
    private CoordinatorLayout coordinatorLayout;
    private MaterialButton buttonFollow, buttonLogin;
    private CircleImageView imageViewProfile;
    private ConstraintLayout conNoData, conFollowings, conFollower;
    private ImageView imageViewData, imageViewLoginType, imageViewYoutube, imageViewInstagram;
    private MaterialTextView textViewData, textViewFollowing, textViewFollower, textViewTotalVideo, textViewUserName;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment, container, false);



        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.profile));
        }

        type = requireArguments().getString("type");
        getUserId = requireArguments().getString("id");

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        function = new Function(requireActivity());

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout_pro);
        conNoData = view.findViewById(R.id.con_not_login);
        imageViewData = view.findViewById(R.id.imageView_not_login);
        buttonLogin = view.findViewById(R.id.button_not_login);
        textViewData = view.findViewById(R.id.textView_not_login);
        textViewUserName = view.findViewById(R.id.textView_name_pro);
        imageViewProfile = view.findViewById(R.id.imageView_pro);
        imageViewLoginType = view.findViewById(R.id.imageView_loginType_pro);
        imageViewYoutube = view.findViewById(R.id.imageView_youtube_pro);
        imageViewInstagram = view.findViewById(R.id.imageView_instagram_pro);
        conFollowings = view.findViewById(R.id.con_followings_pro);
        conFollower = view.findViewById(R.id.con_follower_pro);
        textViewTotalVideo = view.findViewById(R.id.textView_video_pro);
        textViewFollowing = view.findViewById(R.id.textView_following_pro);
        textViewFollower = view.findViewById(R.id.textView_followers_pro);
        buttonFollow = view.findViewById(R.id.button_follow_pro);

        coordinatorLayout.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
        imageViewLoginType.setVisibility(View.GONE);
        data(false, false);

        if (function.isDarkMode()) {
            imageViewInstagram.setImageDrawable(getResources().getDrawable(R.drawable.insta_ic));
            imageViewYoutube.setImageDrawable(getResources().getDrawable(R.drawable.youtube_ic));
        } else {
            imageViewInstagram.setImageDrawable(getResources().getDrawable(R.drawable.insta_ic_pro));
            imageViewYoutube.setImageDrawable(getResources().getDrawable(R.drawable.youtube_ic_pro));
        }

        buttonLogin.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Login.class));
            requireActivity().finishAffinity();
        });

        callData();

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.search_profile){
            SearchFragment searchFragment = new SearchFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, searchFragment, getString(R.string.searc)).commitAllowingStateLoss();
        }
        return super.onOptionsItemSelected(item);
    }
    private void callData() {
        if (getActivity() != null) {
            if (function.isNetworkAvailable()) {
                if (function.isLogin()) {
                    profile(function.userId(), getUserId);
                } else {
                    if (!type.equals("user")) {
                        profile("", getUserId);
                    } else {
                        data(true, true);
                    }
                }
            } else {
                data(true, false);
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void data(boolean isShow, boolean isLogin) {
        if (isShow) {
            if (isLogin) {
                buttonLogin.setVisibility(View.VISIBLE);
                textViewData.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                buttonLogin.setVisibility(View.GONE);
                textViewData.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    public void profile(final String id, final String otherUserId) {

        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            if (id.equals(getUserId)) {
                jsObj.addProperty("AUM", "user_profile");
            } else {
                jsObj.addProperty("AUM", "other_user_profile");
                jsObj.addProperty("other_user_id", otherUserId);
            }
            jsObj.addProperty("user_id", id);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProfileRP> call = apiService.getUserReferenceCode(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProfileRP>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(@NotNull Call<ProfileRP> call, @NotNull Response<ProfileRP> response) {

                    if (getActivity() != null) {

                        try {
                            ProfileRP profileRP = response.body();
                            if (Objects.requireNonNull(profileRP).getStatus().equals("1")) {
                                if (profileRP.getSuccess().equals("1")) {
                                    if (id.equals(getUserId)) {
                                        function.editor.putString(function.userImage, profileRP.getUser_image());
                                        function.editor.commit();
                                    }

                                    if (function.isLogin()) {
                                        if (id.equals(otherUserId)) {
                                            buttonFollow.setText(getResources().getString(R.string.edit_profile));
                                            if (function.getLoginType().equals("google")) {
                                                imageViewLoginType.setVisibility(View.VISIBLE);
                                                imageViewLoginType.setImageDrawable(getResources().getDrawable(R.drawable.google_user_pro));
                                            } else if (function.getLoginType().equals("facebook")) {
                                                imageViewLoginType.setVisibility(View.VISIBLE);
                                                imageViewLoginType.setImageDrawable(getResources().getDrawable(R.drawable.fb_user_pro));
                                            } else {
                                                imageViewLoginType.setVisibility(View.GONE);
                                            }
                                        } else {
                                            if (profileRP.getAlready_follow().equals("true")) {
                                                buttonFollow.setText(getResources().getString(R.string.unfollow));
                                            } else {
                                                buttonFollow.setText(getResources().getString(R.string.follow));
                                            }
                                        }
                                    } else {
                                        buttonFollow.setText(getResources().getString(R.string.follow));
                                    }

                                    if (profileRP.getUser_image().contains(".jpg") || profileRP.getUser_image().contains(".png")) {
                                        Glide.with(getActivity().getApplicationContext()).load(profileRP.getUser_image())
                                                .placeholder(R.drawable.user_profile).into(imageViewProfile);
                                    }
                                    textViewUserName.setText(profileRP.getName());
                                    if (profileRP.getIs_verified().equals("true")) {
                                        textViewUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verification, 0);
                                    }
                                    textViewFollower.setText(function.format(Double.parseDouble(profileRP.getTotal_followers())));
                                    textViewFollowing.setText(function.format(Double.parseDouble(profileRP.getTotal_following())));
                                    textViewTotalVideo.setText(function.format(Double.parseDouble(profileRP.getTotal_status())));

                                    imageViewProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), ViewImage.class)
                                            .putExtra("path", profileRP.getUser_image())));

                                    imageViewYoutube.setOnClickListener(v -> {
                                        imageViewYoutube.startAnimation(myAnim);
                                        String string = profileRP.getUser_youtube();
                                        if (string.equals("")) {
                                            function.alertBox(getResources().getString(R.string.user_not_youtube_link));
                                        } else {
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(string));
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                function.alertBox(getResources().getString(R.string.wrong));
                                            }
                                        }
                                    });

                                    imageViewInstagram.setOnClickListener(v -> {
                                        imageViewInstagram.startAnimation(myAnim);
                                        String string = profileRP.getUser_instagram();
                                        if (string.equals("")) {
                                            function.alertBox(getResources().getString(R.string.user_not_instagram_link));
                                        } else {
                                            Uri uri = Uri.parse(string);
                                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                                            likeIng.setPackage("com.instagram.android");
                                            try {
                                                startActivity(likeIng);
                                            } catch (ActivityNotFoundException e) {
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse(string)));
                                                } catch (Exception e1) {
                                                    function.alertBox(getResources().getString(R.string.wrong));
                                                }
                                            }
                                        }
                                    });

                                    buttonFollow.setOnClickListener(v -> {
                                        if (function.isNetworkAvailable()) {
                                            if (function.isLogin()) {
                                                if (id.equals(otherUserId)) {
                                                    getActivity().getSupportFragmentManager().beginTransaction()
                                                            .add(R.id.frameLayout_main, new EditProfileFragment(), getResources().getString(R.string.edit_profile))
                                                            .addToBackStack(getResources().getString(R.string.edit_profile)).commitAllowingStateLoss();
                                                } else {
                                                    follow(id, otherUserId);
                                                }
                                            } else {
                                                function.alertBox(getResources().getString(R.string.you_have_not_login));
                                            }
                                        } else {
                                            function.alertBox(getResources().getString(R.string.internet_connection));
                                        }
                                    });

                                    conFollowings.setOnClickListener(v -> {
                                        if (!profileRP.getTotal_following().equals("0")) {
                                            UserFollowFragment userFollowFragment = new UserFollowFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("type", "following");
                                            bundle.putString("user_id", profileRP.getUser_id());
                                            bundle.putString("search", "");
                                            userFollowFragment.setArguments(bundle);
                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, userFollowFragment, getResources().getString(R.string.following)).addToBackStack(getResources().getString(R.string.following)).commitAllowingStateLoss();
                                        } else {
                                            function.alertBox(getResources().getString(R.string.not_following));
                                        }

                                    });

                                    conFollower.setOnClickListener(v -> {
                                        if (!profileRP.getTotal_followers().equals("0")) {
                                            UserFollowFragment userFollowFragment = new UserFollowFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("type", "follower");
                                            bundle.putString("user_id", profileRP.getUser_id());
                                            bundle.putString("search", "");
                                            userFollowFragment.setArguments(bundle);
                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, userFollowFragment, getResources().getString(R.string.following)).addToBackStack("sub").commitAllowingStateLoss();
                                        } else {
                                            function.alertBox(getResources().getString(R.string.not_follower));
                                        }
                                    });
                                    coordinatorLayout.setVisibility(View.VISIBLE);

                                } else {
                                    data(true, false);
                                    function.alertBox(profileRP.getMsg());
                                }
                            } else if (profileRP.getStatus().equals("2")) {
                                function.suspend(profileRP.getMessage());
                            } else {
                                data(true, false);
                                function.alertBox(profileRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<ProfileRP> call, @NotNull Throwable t) {
                    function.hideProgressDialog(requireActivity());                    Log.e("fail", t.toString());
                    data(true, false);
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void follow(final String userId, final String otherUser) {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "user_follow");
            jsObj.addProperty("user_id", otherUser);
            jsObj.addProperty("follower_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserFollowStatusRP> call = apiService.getUserFollowStatus(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<UserFollowStatusRP>() {
                @Override
                public void onResponse(@NotNull Call<UserFollowStatusRP> call, @NotNull Response<UserFollowStatusRP> response) {
                    if (getActivity() != null) {
                        try {
                            UserFollowStatusRP userFollowStatusRP = response.body();
                            if (Objects.requireNonNull(userFollowStatusRP).getStatus().equals("1")) {
                                if (userFollowStatusRP.getSuccess().equals("1")) {
                                    if (userFollowStatusRP.getActivity_status().equals("1")) {
                                        buttonFollow.setText(getResources().getString(R.string.unfollow));
                                    } else {
                                        buttonFollow.setText(getResources().getString(R.string.follow));
                                    }
                                    getUserId = otherUser;
                                    profile(userId, otherUser);
                                } else {
                                    function.alertBox(userFollowStatusRP.getMsg());
                                }
                            } else if (userFollowStatusRP.getStatus().equals("2")) {
                                function.suspend(userFollowStatusRP.getMessage());
                            } else {
                                function.alertBox(userFollowStatusRP.getMessage());
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<UserFollowStatusRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }
    }
}
