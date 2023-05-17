package com.ILoveDeshi.Android_Source_Code.fragment;

import static com.ILoveDeshi.Android_Source_Code.activity.MainActivity.userProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ILoveDeshi.Android_Source_Code.activity.GoogleSignInActivity;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.activity.ViewYouTubeApp;
import com.ILoveDeshi.Android_Source_Code.adapter.AppsAdapter;
import com.ILoveDeshi.Android_Source_Code.adapter.AppsRatingAdapter;
import com.ILoveDeshi.Android_Source_Code.adapter.GameAdapter;
import com.ILoveDeshi.Android_Source_Code.adapter.MostAppsAdapter;
import com.ILoveDeshi.Android_Source_Code.adapter.ProductsAdapter;
import com.ILoveDeshi.Android_Source_Code.adapter.SliderAdapter;
import com.ILoveDeshi.Android_Source_Code.adapter.SubscribeAdapter;
import com.ILoveDeshi.Android_Source_Code.adapter.VideoAdapter;
import com.ILoveDeshi.Android_Source_Code.adapter.WebsiteAdapter;
import com.ILoveDeshi.Android_Source_Code.item.AppsList;
import com.ILoveDeshi.Android_Source_Code.item.AppsRatingList;
import com.ILoveDeshi.Android_Source_Code.item.GameList;
import com.ILoveDeshi.Android_Source_Code.item.MostAppsList;
import com.ILoveDeshi.Android_Source_Code.item.ProductList;
import com.ILoveDeshi.Android_Source_Code.item.SubscribeList;
import com.ILoveDeshi.Android_Source_Code.item.VideoList;
import com.ILoveDeshi.Android_Source_Code.item.WebsiteList;
import com.ILoveDeshi.Android_Source_Code.response.AppsRP;
import com.ILoveDeshi.Android_Source_Code.response.AppsRatingRP;
import com.ILoveDeshi.Android_Source_Code.response.GameRP;
import com.ILoveDeshi.Android_Source_Code.response.MostAppsRP;
import com.ILoveDeshi.Android_Source_Code.response.ProductsRP;
import com.ILoveDeshi.Android_Source_Code.response.ProfileRP;
import com.ILoveDeshi.Android_Source_Code.response.HomeTopUserRP;
import com.ILoveDeshi.Android_Source_Code.response.SubscribeRP;
import com.ILoveDeshi.Android_Source_Code.response.VideoRP;
import com.ILoveDeshi.Android_Source_Code.response.WebsiteRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.response.HomeRP;
import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Function function;
    public static Activity activity;
    private OnClick onClick, onClicks, onClickUser, onClickGames, onClickSubs, onClickProducts, onClickWebs, onClickVideos, onClickRating;
    private ViewPager viewPager;
    private SliderAdapter sliderAdapter;
    private Boolean isOver = false;
    private RecyclerView rvLatest, rvMost, rvGames, rvSubs, rvWeb, rvVideo, rvRating, rvProducts;
    private List<AppsList> appsLists;
    private List<AppsRatingList> appsRatingLists;
    private List<MostAppsList> mostAppsLists;
    private List<ProductList> productLists;
    private AppsAdapter appsAdapter;
    private MostAppsAdapter mostAppsAdapter;
    private ProductsAdapter productsAdapter;
    private WebsiteAdapter websiteAdapter;
    private VideoAdapter videoAdapter;
    private AppsRatingAdapter ratingAdapter;
    private List<WebsiteList> websiteLists;
    private List<VideoList> videoLists;
    private GameAdapter gameAdapter;
    private SubscribeAdapter subscribeAdapter;
    private ArrayList<GameList> gameLists;
    private ArrayList<SubscribeList> subscribeLists;
    private int oldPosition = 0, paginationIndex = 1;
    private LayoutAnimationController animation;
    private MaterialCardView cardProfile, cardReward, cardRefer;
    private CircleImageView imageView, userPic;
    private CircleImageView imgPost1, imgPost2, imgPost3;
    private TextView tvPost1, tvPost2, tvPost3, tvScore1, tvScore2, tvScore3;
    private NumberProgressBar progressPointsC, progressPointsB, progressPointsA;
    private MaterialTextView tvName, tvCoins, tvLatest, viewProducts, tvGames, tvInstalled, viewTopUser, viewVideo, viewWebs, viewSubs;
    private ConstraintLayout conNoData, conMain, conSlider;
    private Timer timer;
    private Runnable Update;
    private final long DELAY_MS = 600;
    private final long PERIOD_MS = 3000;
    private final Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_fragment, container, false);
        activity = requireActivity();
        appsLists = new ArrayList<>();
        mostAppsLists = new ArrayList<>();
        gameLists = new ArrayList<>();
        subscribeLists = new ArrayList<>();
        websiteLists = new ArrayList<>();
        videoLists = new ArrayList<>();
        appsRatingLists = new ArrayList<>();
        productLists = new ArrayList<>();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }
        function = new Function(requireActivity(), onClick);
        onClicks = (position, title, type, status_type, id, tag) -> {
            ViewAppFragment viewAppFragment = new ViewAppFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("type", "install");
            bundle.putString("mode", "no");
            viewAppFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, viewAppFragment, title).addToBackStack(title).commitAllowingStateLoss();
        };
        function = new Function(activity, onClicks);

        onClickUser = (position, title, type, status_type, id, tag) -> {
            if (activity != null) {
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundleProfile = new Bundle();
                bundleProfile.putString("type", "other_user");
                bundleProfile.putString("id", id);
                profileFragment.setArguments(bundleProfile);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, profileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commitAllowingStateLoss();
            } else {
                function.alertBox(getResources().getString(R.string.wrong));
            }
        };
        function = new Function(activity, onClickUser);
        onClickGames = (position, title, type, status_type, id, tag) -> {
            if (activity != null) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                customTabsIntent.launchUrl(requireActivity(), Uri.parse(id));
                Constant.isGameRunning = false;
                if (!Constant.appRP.isLive_mode()) {
                    Constant.r = () -> {
                        if (activity != null) {
                            function.showToast(getResources().getString(R.string.congrats) + Constant.appRP.getOnline_game_points());
                            Log.d("MyOnlineGames", "Badge at index");
                            function.secureAllWorld(function.userId(), title + " Game", String.valueOf(Constant.appRP.getOnline_game_points()));
                            Constant.isGameRunning = true;
                        }
                    };
                }
                Constant.handler.postDelayed(Constant.r, (long) Constant.appRP.getOnline_game_timer() * 60 * 1000);
            } else {
                function.alertBox(getResources().getString(R.string.wrong));
            }
        };
        function = new Function(activity, onClickGames);

        onClickSubs = (position, title, type, status_type, id, tag) -> {
            if (activity != null) {
                Intent i = new Intent(activity, GoogleSignInActivity.class);
                i.putExtra("id", tag);
                i.putExtra("coins", type);
                i.putExtra("channel_name", title);
                i.putExtra("channel_username", id);
                i.putExtra("ytLogo", status_type);
                activity.startActivity(i);
            }
        };
        function = new Function(activity, onClickSubs);

        onClickProducts = (position, title, type, status_type, id, tag) -> {
            if (activity != null) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                customTabsIntent.launchUrl(requireActivity(), Uri.parse(id));
            }
        };
        function = new Function(activity, onClickProducts);

        onClickWebs = (position, title, type, status_type, id, tag) -> {
            if (activity != null) {
                VieWebFragment vieWebFragment = new VieWebFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("type", type);
                vieWebFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, vieWebFragment, title).addToBackStack(title).commitAllowingStateLoss();
            }
        };
        function = new Function(activity, onClickWebs);

        onClickVideos = (position, title, type, status_type, id, tag) -> {
            if (activity != null) {
                startActivity(new Intent(requireActivity(), ViewYouTubeApp.class)
                        .putExtra("id", id)
                        .putExtra("url", title)
                        .putExtra("time", status_type)
                        .putExtra("point", type)
                );
            }
        };
        onClickRating = (position, title, type, status_type, id, tag) -> {
            if (activity != null) {
                if (!Constant.appRP.isLive_mode()) {
                    function.alertBox(getString(R.string.wrong));
                } else {
                    AppRatingFragment appRatingFragment = new AppRatingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("type", "install");
                    bundle.putString("mode", "home_app_rating");
                    appRatingFragment.setArguments(bundle);
                    requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, appRatingFragment, title).addToBackStack(title).commitAllowingStateLoss();
                }
            }
        };
        function = new Function(activity, onClickVideos);

        int columnWidth = function.getScreenWidth();
        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        conMain = view.findViewById(R.id.con_main_home);
        conNoData = view.findViewById(R.id.con_noDataFound);
        viewPager = view.findViewById(R.id.slider_home);
        conSlider = view.findViewById(R.id.con_slider_home);
        rvLatest = view.findViewById(R.id.rvLatestApps);
        rvMost = view.findViewById(R.id.rvMostInstalled);
        rvProducts = view.findViewById(R.id.rvProducts);
        rvGames = view.findViewById(R.id.rvGames);
        rvSubs = view.findViewById(R.id.rvSubs);
        rvWeb = view.findViewById(R.id.rvWebsite);
        rvVideo = view.findViewById(R.id.rvVideo);
        rvRating = view.findViewById(R.id.rvAppsRating);
        imageView = view.findViewById(R.id.imageView_pro);
        userPic = view.findViewById(R.id.userPic);
        tvName = view.findViewById(R.id.textView_name_pro);
        tvLatest = view.findViewById(R.id.viewLatest);
        viewProducts = view.findViewById(R.id.viewProducts);
        tvGames = view.findViewById(R.id.viewGames);
        viewTopUser = view.findViewById(R.id.viewTopUser);
        viewWebs = view.findViewById(R.id.viewWebsite);
        viewSubs = view.findViewById(R.id.viewSubs);
        viewVideo = view.findViewById(R.id.viewVideo);
        tvInstalled = view.findViewById(R.id.viewInstalled);
        tvCoins = view.findViewById(R.id.tvCoin);
        cardProfile = view.findViewById(R.id.cardProfile);
        cardRefer = view.findViewById(R.id.cardReferral);
        cardReward = view.findViewById(R.id.cardReward);
        if (!Constant.appRP.isLive_mode()) {
            cardReward.setVisibility(View.INVISIBLE);
        }

        imgPost1 = view.findViewById(R.id.imgPost1);
        imgPost2 = view.findViewById(R.id.imgPost2);
        imgPost3 = view.findViewById(R.id.imgPost3);
        tvPost1 = view.findViewById(R.id.tvPost1);
        tvPost2 = view.findViewById(R.id.tvPost2);
        tvPost3 = view.findViewById(R.id.tvPost3);
        tvScore1 = view.findViewById(R.id.tvScore1);
        tvScore2 = view.findViewById(R.id.tvScore2);
        tvScore3 = view.findViewById(R.id.tvScore3);
        progressPointsC = view.findViewById(R.id.progressPointsC);
        progressPointsB = view.findViewById(R.id.progressPointsB);
        progressPointsA = view.findViewById(R.id.progressPointsA);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
        rvLatest.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rvLatest.setLayoutManager(layoutManager);

        rvMost.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(getContext(), 3);
        rvMost.setLayoutManager(layoutManager2);

        rvProducts.setHasFixedSize(true);
        GridLayoutManager layoutManager8 = new GridLayoutManager(getContext(), 2);
        rvProducts.setLayoutManager(layoutManager8);

        GridLayoutManager layoutManager3 = new GridLayoutManager(getContext(), 3);
        rvGames.setLayoutManager(layoutManager3);
        rvGames.setHasFixedSize(true);

        GridLayoutManager layoutManager6 = new GridLayoutManager(getContext(), 3);
        rvSubs.setLayoutManager(layoutManager6);
        rvSubs.setHasFixedSize(true);

        GridLayoutManager layoutManager4 = new GridLayoutManager(getContext(), 3);
        rvWeb.setLayoutManager(layoutManager4);
        rvWeb.setHasFixedSize(true);

        GridLayoutManager layoutManager5 = new GridLayoutManager(getContext(), 2);
        rvVideo.setLayoutManager(layoutManager5);
        rvVideo.setHasFixedSize(true);

        GridLayoutManager layoutManager7 = new GridLayoutManager(getContext(), 3);
        rvRating.setLayoutManager(layoutManager7);
        rvRating.setHasFixedSize(true);

        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 3 + 100));
        viewPager.setPageMargin(dpToPx(5));
        viewPager.setClipToPadding(false);
        viewPager.setPadding(70, 0, 70, 0);
        callData();

        cardRefer.setOnClickListener(view1 -> requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new ReferenceCodeFragment(), getResources().getString(R.string.reference_code)).addToBackStack(getResources().getString(R.string.reference_code)).commitAllowingStateLoss());
        cardReward.setOnClickListener(view1 -> {
            SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "type");
            subscriptionFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, subscriptionFragment, getResources().getString(R.string.adClick)).addToBackStack(getResources().getString(R.string.adClick)).commitAllowingStateLoss();
        });
        cardProfile.setOnClickListener(view1 -> {
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle_profile = new Bundle();
            bundle_profile.putString("type", "user");
            bundle_profile.putString("id", function.userId());
            profileFragment.setArguments(bundle_profile);
            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, profileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commitAllowingStateLoss();
        });

        tvLatest.setOnClickListener(v -> {
            if (getActivity() != null) {
                MoreAppsFragment moreAppsFragment = new MoreAppsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "home_latest");
                moreAppsFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, moreAppsFragment, getResources().getString(R.string.home)).addToBackStack(getResources().getString(R.string.latestApps)).commitAllowingStateLoss();
            }
        });
        viewProducts.setOnClickListener(v -> {
            if (getActivity() != null) {
                ProductsFragment productsFragment = new ProductsFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, productsFragment, getResources().getString(R.string.home)).addToBackStack(getResources().getString(R.string.affiliate_product)).commitAllowingStateLoss();
            }
        });
        tvGames.setOnClickListener(v -> {
            if (getActivity() != null) {
                GameListFragment gameListFragment = new GameListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "home_latest");
                gameListFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, gameListFragment, getResources().getString(R.string.home)).addToBackStack(getResources().getString(R.string.onlineGames)).commitAllowingStateLoss();
            }
        });

        tvInstalled.setOnClickListener(v -> {
            if (getActivity() != null) {
                MoreAppsFragment moreAppsFragment = new MoreAppsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "home_most");
                moreAppsFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, moreAppsFragment, getResources().getString(R.string.home)).addToBackStack(getResources().getString(R.string.latestApps)).commitAllowingStateLoss();
            }
        });

        viewTopUser.setOnClickListener(v -> {
            if (getActivity() != null) {
                TopUsersFragment topUsersFragment = new TopUsersFragment();
                Bundle bundleProfile = new Bundle();
                topUsersFragment.setArguments(bundleProfile);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, topUsersFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commitAllowingStateLoss();
            }
        });
        viewWebs.setOnClickListener(v -> {
            if (getActivity() != null) {
                WebsitesFragment websitesFragment = new WebsitesFragment();
                Bundle bundleProfile = new Bundle();
                bundleProfile.putString("type", "home_website");
                websitesFragment.setArguments(bundleProfile);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, websitesFragment, getResources().getString(R.string.allWebsites)).addToBackStack(getResources().getString(R.string.allWebsites)).commitAllowingStateLoss();
            }
        });
        viewSubs.setOnClickListener(v -> {
            if (getActivity() != null) {
                SubscriberFragment subscriberFragment = new SubscriberFragment();
                Bundle bundleProfile = new Bundle();
                bundleProfile.putString("type", "home_subs");
                subscriberFragment.setArguments(bundleProfile);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, subscriberFragment, getResources().getString(R.string.subscribers)).addToBackStack(getResources().getString(R.string.subscribers)).commitAllowingStateLoss();
            }
        });
        viewVideo.setOnClickListener(v -> {
            if (getActivity() != null) {
                VideosFragment videosFragment = new VideosFragment();
                Bundle bundleProfile = new Bundle();
                bundleProfile.putString("type", "home_video");
                videosFragment.setArguments(bundleProfile);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, videosFragment, getResources().getString(R.string.allWebsites)).addToBackStack(getResources().getString(R.string.youtube_watch)).commitAllowingStateLoss();
            }
        });
        return view;
    }

    private void callData() {
        function.showProgressDialog(requireActivity());
        if (function.isNetworkAvailable() && getActivity() != null) {
            if (function.isLogin()) {
                profile(function.userId());
            }
            home();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void home() {
        if (getActivity() != null) {
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "home");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HomeRP> call = apiService.getHome(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<HomeRP>() {
                @Override
                public void onResponse(@NotNull Call<HomeRP> call, @NotNull Response<HomeRP> response) {
                    if (getActivity() != null) {
                        try {
                            HomeRP homeRP = response.body();
                            if (Objects.requireNonNull(homeRP).getStatus().equals("1")) {
                                if (homeRP.getSliderLists().size() != 0) {
                                    sliderAdapter = new SliderAdapter(getActivity(), "slider", homeRP.getSliderLists(), onClick);
                                    viewPager.setAdapter(sliderAdapter);
                                    viewPager.setOffscreenPageLimit(homeRP.getSliderLists().size() - 1);
                                    Update = () -> {
                                        if (viewPager.getCurrentItem() == (sliderAdapter.getCount() - 1)) {
                                            viewPager.setCurrentItem(0, true);
                                        } else {
                                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                        }
                                    };
                                    if (sliderAdapter.getCount() > 1) {
                                        timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                handler.post(Update);
                                            }
                                        }, DELAY_MS, PERIOD_MS);
                                    }

                                } else {
                                    if (sliderAdapter == null) {
                                        conSlider.setVisibility(View.GONE);
                                    }
                                }

                                conMain.setVisibility(View.VISIBLE);

                            } else if (homeRP.getStatus().equals("2")) {
                                function.suspend(homeRP.getMessage());
                            } else {
                                function.alertBox(homeRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                    onlineGames();
                }

                @Override
                public void onFailure(@NotNull Call<HomeRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = requireActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getHomeApps() {
        if (getActivity() != null) {
            if (appsAdapter == null) {
                appsLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "latest_app_list");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<AppsRP> call = apiService.getApps(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<AppsRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<AppsRP> call, @NotNull Response<AppsRP> response) {
                    if (getActivity() != null) {
                        try {
                            AppsRP categoryRP = response.body();
                            if (Objects.requireNonNull(categoryRP).getStatus().equals("1")) {
                                if (categoryRP.getAppsLists().size() != 0) {
                                    appsLists.addAll(categoryRP.getAppsLists());
                                }

                                if (appsAdapter == null) {
                                    if (appsLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        appsAdapter = new AppsAdapter(getActivity(), appsLists, "home", onClicks);
                                        rvLatest.setAdapter(appsAdapter);
                                        rvLatest.setLayoutAnimation(animation);
                                    }
                                } else {
                                    appsAdapter.notifyDataSetChanged();
                                }
                            } else {
                                function.alertBox(categoryRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again) + " : " + e);
                        }

                    }
                    getHomeMostApps();
                }

                @Override
                public void onFailure(@NotNull Call<AppsRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void getHomeMostApps() {
        if (getActivity() != null) {
            if (mostAppsAdapter == null) {
                mostAppsLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "most_app_list");
            jsObj.addProperty("value", "single");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MostAppsRP> call = apiService.getMostApps(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<MostAppsRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<MostAppsRP> call, @NotNull Response<MostAppsRP> response) {
                    if (getActivity() != null) {
                        try {
                            MostAppsRP categoryRP = response.body();
                            if (Objects.requireNonNull(categoryRP).getStatus().equals("1")) {
                                if (categoryRP.getMostAppsLists().size() != 0) {
                                    mostAppsLists.addAll(categoryRP.getMostAppsLists());
                                }
                                if (mostAppsAdapter == null) {
                                    if (mostAppsLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        mostAppsAdapter = new MostAppsAdapter(getActivity(), mostAppsLists, "install", onClicks);
                                        rvMost.setAdapter(mostAppsAdapter);
                                        rvMost.setLayoutAnimation(animation);
                                    }
                                } else {
                                    mostAppsAdapter.notifyDataSetChanged();
                                }
                            } else {
                                function.alertBox(categoryRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again) + " : " + e);
                        }
                    }
                    getHomeWebs();
                }

                @Override
                public void onFailure(@NotNull Call<MostAppsRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void getHomeWebs() {
        if (getActivity() != null) {
            if (websiteAdapter == null) {
                websiteLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "featured_websites");
            jsObj.addProperty("value", "few");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<WebsiteRP> call = apiService.getFeaturedWeb(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<WebsiteRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<WebsiteRP> call, @NotNull Response<WebsiteRP> response) {
                    if (getActivity() != null) {
                        try {
                            WebsiteRP categoryRP = response.body();
                            if (Objects.requireNonNull(categoryRP).getStatus().equals("1")) {
                                if (categoryRP.getWebsiteLists().size() != 0) {
                                    websiteLists.addAll(categoryRP.getWebsiteLists());
                                }
                                if (websiteAdapter == null) {
                                    if (websiteLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        websiteAdapter = new WebsiteAdapter(getActivity(), websiteLists, "home", onClickWebs);
                                        rvWeb.setAdapter(websiteAdapter);
                                        rvWeb.setLayoutAnimation(animation);
                                    }
                                } else {
                                    websiteAdapter.notifyDataSetChanged();
                                }
                            } else {
                                function.alertBox(categoryRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again) + " : " + e);
                        }
                    }
                    getLatestVideos();
                }

                @Override
                public void onFailure(@NotNull Call<WebsiteRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void getLatestVideos() {
        if (getActivity() != null) {
            if (videoAdapter == null) {
                videoLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "latest_video");
            jsObj.addProperty("type", "home");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<VideoRP> call = apiService.getVideoList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<VideoRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<VideoRP> call, @NotNull Response<VideoRP> response) {
                    if (getActivity() != null) {
                        try {
                            VideoRP categoryRP = response.body();
                            if (Objects.requireNonNull(categoryRP).getStatus().equals("1")) {
                                if (categoryRP.getVideoLists().size() != 0) {
                                    videoLists.addAll(categoryRP.getVideoLists());
                                }
                                if (videoAdapter == null) {
                                    if (videoLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        videoAdapter = new VideoAdapter(getActivity(), videoLists, "home", onClickVideos);
                                        rvVideo.setAdapter(videoAdapter);
                                        rvVideo.setLayoutAnimation(animation);
                                    }
                                } else {
                                    videoAdapter.notifyDataSetChanged();
                                }
                            } else {
                                function.alertBox(categoryRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again) + " : " + e);
                        }
                    }
                    getSubscriber();
                }

                @Override
                public void onFailure(@NotNull Call<VideoRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void profile(String userId) {
        if (getActivity() != null) {
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("AUM", "user_profile");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProfileRP> call = apiService.getProfile(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProfileRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<ProfileRP> call, @NotNull Response<ProfileRP> response) {
                    if (getActivity() != null) {
                        try {
                            ProfileRP profileRP = response.body();
                            if (Objects.requireNonNull(profileRP).getStatus().equals("1")) {
                                if (profileRP.getSuccess().equals("1")) {
                                    if (!profileRP.getUser_image().equals("") && (profileRP.getUser_image().contains(".jpg") || profileRP.getUser_image().contains(".png"))) {
                                        Glide.with(getActivity().getApplicationContext()).load(profileRP.getUser_image())
                                                .placeholder(R.drawable.profile).into(imageView);
                                        Glide.with(getActivity().getApplicationContext()).load(profileRP.getUser_image())
                                                .placeholder(R.drawable.profile).into(userProfile);
                                        Glide.with(getActivity().getApplicationContext()).load(profileRP.getUser_image())
                                                .placeholder(R.drawable.profile).into(userPic);
                                    }
                                    tvName.setText(getString(R.string.hello) + getString(R.string.space) + profileRP.getName() + "!");
                                    tvCoins.setText(getString(R.string.you_have) + getString(R.string.space) + profileRP.getTotal_point() + getString(R.string.space) + getString(R.string.point));
                                    conMain.setVisibility(View.VISIBLE);
                                } else {
                                    conNoData.setVisibility(View.VISIBLE);
                                    function.alertBox(profileRP.getMsg());
                                }
                            } else if (profileRP.getStatus().equals("2")) {
                                function.suspend(profileRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(profileRP.getMessage());
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                    topUserHome();
                }

                @Override
                public void onFailure(@NotNull Call<ProfileRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void topUserHome() {
        if (getActivity() != null) {
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "get_top_three");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HomeTopUserRP> call = apiService.getTopThree(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<HomeTopUserRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<HomeTopUserRP> call, @NotNull Response<HomeTopUserRP> response) {
                    if (getActivity() != null) {
                        try {
                            HomeTopUserRP topUserRP = response.body();
                            if (Objects.requireNonNull(topUserRP).getStatus().equals("1")) {
                                tvPost1.setText(topUserRP.getName_1());
                                tvPost2.setText(topUserRP.getName_2());
                                tvPost3.setText(topUserRP.getName_3());
                                tvScore1.setText(topUserRP.getPoint_1());
                                tvScore2.setText(topUserRP.getPoint_2());
                                tvScore3.setText(topUserRP.getPoint_3());

                                progressPointsA.setProgress(Integer.parseInt(topUserRP.getPoint_1()));
                                progressPointsB.setProgress(Integer.parseInt(topUserRP.getPoint_2()));
                                progressPointsC.setProgress(Integer.parseInt(topUserRP.getPoint_3()));

                                if (topUserRP.getImage_1().contains(".jpg") || topUserRP.getImage_1().contains(".png") || topUserRP.getImage_1().contains(".jpeg")) {
                                    Glide.with(activity).load(topUserRP.getImage_1())
                                            .placeholder(R.drawable.number_one).into(imgPost1);
                                }
                                if (topUserRP.getImage_2().contains(".jpg") || topUserRP.getImage_2().contains(".png") || topUserRP.getImage_2().contains(".jpeg")) {
                                    Glide.with(activity).load(topUserRP.getImage_2())
                                            .placeholder(R.drawable.number_two).into(imgPost2);
                                }
                                if (topUserRP.getImage_3().contains(".jpg") || topUserRP.getImage_3().contains(".png") || topUserRP.getImage_3().contains(".jpeg")) {
                                    Glide.with(activity).load(topUserRP.getImage_3())
                                            .placeholder(R.drawable.number_three).into(imgPost3);
                                }
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(getString(R.string.no_data_found));
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<HomeTopUserRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void onlineGames() {
        if (getActivity() != null) {
            if (gameAdapter == null) {
                gameLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "getGameList");
            jsObj.addProperty("value", "games");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<GameRP> call = apiService.getGameList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<GameRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<GameRP> call, @NotNull Response<GameRP> response) {
                    if (getActivity() != null) {
                        try {
                            GameRP gameRP = response.body();
                            if (Objects.requireNonNull(gameRP).getStatus().equals("1")) {
                                if (gameRP.getGameLists().size() != 0) {
                                    gameLists.addAll(gameRP.getGameLists());
                                }
                                if (gameAdapter == null) {
                                    if (gameLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        gameAdapter = new GameAdapter(getActivity(), gameLists, "games", onClickGames);
                                        rvGames.setAdapter(gameAdapter);
                                        rvGames.setLayoutAnimation(animation);
                                    }
                                } else {
                                    gameAdapter.notifyDataSetChanged();
                                }

                            } else if (gameRP.getStatus().equals("2")) {
                                function.suspend(gameRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(gameRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }
                    getHomeApps();
                }

                @Override
                public void onFailure(@NotNull Call<GameRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void getSubscriber() {
        if (getActivity() != null) {
            if (subscribeAdapter == null) {
                subscribeLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "getSubscriber");
            jsObj.addProperty("value", "few");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SubscribeRP> call = apiService.getSubscriber(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<SubscribeRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<SubscribeRP> call, @NotNull Response<SubscribeRP> response) {
                    if (getActivity() != null) {
                        try {
                            SubscribeRP gameRP = response.body();
                            if (Objects.requireNonNull(gameRP).getStatus().equals("1")) {
                                if (gameRP.getSubscribeLists().size() != 0) {
                                    subscribeLists.addAll(gameRP.getSubscribeLists());
                                }
                                if (subscribeAdapter == null) {
                                    if (gameLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        subscribeAdapter = new SubscribeAdapter(getActivity(), subscribeLists, "subscribe", onClickSubs);
                                        rvSubs.setAdapter(subscribeAdapter);
                                        rvSubs.setLayoutAnimation(animation);
                                    }
                                } else {
                                    subscribeAdapter.notifyDataSetChanged();
                                }
                            } else if (gameRP.getStatus().equals("2")) {
                                function.suspend(gameRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(gameRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }
                    getHomeRatingApps();
                }

                @Override
                public void onFailure(@NotNull Call<SubscribeRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void getHomeRatingApps() {
        if (getActivity() != null) {
            if (ratingAdapter == null) {
                appsRatingLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "rating_app_list");
            jsObj.addProperty("value", "featured");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<AppsRatingRP> call = apiService.getRatingApps(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<AppsRatingRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<AppsRatingRP> call, @NotNull Response<AppsRatingRP> response) {
                    if (getActivity() != null) {
                        try {
                            AppsRatingRP appsRatingRP = response.body();
                            if (Objects.requireNonNull(appsRatingRP).getStatus().equals("1")) {
                                if (appsRatingRP.getAppsLists().size() != 0) {
                                    appsRatingLists.addAll(appsRatingRP.getAppsLists());
                                }
                                if (ratingAdapter == null) {
                                    if (appsRatingLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        ratingAdapter = new AppsRatingAdapter(getActivity(), appsRatingLists, "rating", onClickRating);
                                        rvRating.setAdapter(ratingAdapter);
                                        rvRating.setLayoutAnimation(animation);
                                    }
                                } else {
                                    ratingAdapter.notifyDataSetChanged();
                                }
                            } else {
                                function.alertBox(appsRatingRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again) + " : " + e);
                        }
                    }
                    getAffiliateProduct();
                }

                @Override
                public void onFailure(@NotNull Call<AppsRatingRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void getAffiliateProduct() {
        if (activity != null) {
            if (productsAdapter == null) {
                productLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "affiliate_product_list");
            jsObj.addProperty("value", "single");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProductsRP> call = apiService.getProducts(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProductsRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<ProductsRP> call, @NotNull Response<ProductsRP> response) {
                    if (activity != null) {
                        try {
                            ProductsRP productsRP = response.body();
                            if (Objects.requireNonNull(productsRP).getStatus().equals("1")) {
                                if (productsRP.getProductLists().size() != 0) {
                                    productLists.addAll(productsRP.getProductLists());
                                }
                                if (productsAdapter == null) {
                                    if (productLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        productsAdapter = new ProductsAdapter(getActivity(), productLists, "products", onClickProducts);
                                        rvProducts.setAdapter(productsAdapter);
                                        rvProducts.setLayoutAnimation(animation);
                                    }
                                } else {
                                    productsAdapter.notifyDataSetChanged();
                                }
                            } else {
                                function.alertBox(productsRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again) + " : " + e);
                        }
                    }
                    function.hideProgressDialog(activity);
                }

                @Override
                public void onFailure(@NotNull Call<ProductsRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

}
