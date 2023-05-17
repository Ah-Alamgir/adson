package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.ILoveDeshi.Android_Source_Code.activity.Login;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.activity.RewardPointClaim;
import com.ILoveDeshi.Android_Source_Code.adapter.ViewpagerRewardAdapter;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.RewardPointRP;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardPointFragment extends Fragment {

    private Function function;
    private Menu menu;
    private String[] tabName;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private MaterialButton button;
    private ConstraintLayout conNoData;
    private ImageView imageViewData;
    private MaterialButton buttonLogin;
    private FragmentManager childFragManger;
    private String type = "", totalPoint = null;
    private CoordinatorLayout coordinatorLayout;
    private MaterialTextView textViewData, textViewMenuPointCount, textViewPointMenu, textViewPoint, textViewMoney;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.reward_point_fragment, container, false);


        tabName = new String[]{getResources().getString(R.string.current_point),
                getResources().getString(R.string.withdrawal_history)};

        childFragManger = getChildFragmentManager();
        type = requireArguments().getString("type");
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.reward_point));
        }

        function = new Function(requireActivity());

        conNoData = view.findViewById(R.id.con_not_login);
        coordinatorLayout = view.findViewById(R.id.con_myUpload_fragment);
        imageViewData = view.findViewById(R.id.imageView_not_login);
        buttonLogin = view.findViewById(R.id.button_not_login);
        textViewData = view.findViewById(R.id.textView_not_login);
        textViewPoint = view.findViewById(R.id.textView_total_reward_point_fragment);
        textViewMoney = view.findViewById(R.id.textView_money_reward_point_fragment);
        tabLayout = view.findViewById(R.id.tabLayout_reward_point_fragment);
        viewPager2 = view.findViewById(R.id.viewPager_reward_point_fragment);
        button = view.findViewById(R.id.button_reward_point_fragment);
        AppBarLayout appbar = view.findViewById(R.id.appbar_reward_point_fragment);

        coordinatorLayout.setVisibility(View.GONE);
        data(false, false);

        buttonLogin.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Login.class));
            requireActivity().finishAffinity();
        });

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    if (totalPoint != null) {
                        textViewMenuPointCount.setVisibility(View.VISIBLE);
                        textViewPointMenu.setVisibility(View.VISIBLE);
                    }
                } else if (isShow) {
                    isShow = false;
                    if (totalPoint != null) {
                        textViewMenuPointCount.setVisibility(View.GONE);
                        textViewPointMenu.setVisibility(View.GONE);
                    }
                }
            }
        });

        callData();

        setHasOptionsMenu(true);
        return view;
    }

    public void callData() {

        if (function.isNetworkAvailable()) {
            if (function.isLogin()) {
                userData(function.userId());
            } else {
                data(true, true);
            }
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.point_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void userData(String id) {

        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", id);
            jsObj.addProperty("AUM", "reward_points");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<RewardPointRP> call = apiService.getUserRewardPoint(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<RewardPointRP>() {
                @Override
                public void onResponse(@NotNull Call<RewardPointRP> call, @NotNull Response<RewardPointRP> response) {

                    if (getActivity() != null) {

                        try {
                            RewardPointRP rewardPointRP = response.body();
                            if (Objects.requireNonNull(rewardPointRP).getStatus().equals("1")) {
                                if (rewardPointRP.getSuccess().equals("1")) {
                                    String money = rewardPointRP.getRedeem_points()
                                            + " " + getResources().getString(R.string.point)
                                            + " " + getResources().getString(R.string.equal)
                                            + " " + rewardPointRP.getRedeem_money();
                                    textViewMoney.setText(money);

                                    totalPoint = rewardPointRP.getTotal_point();
                                    textViewPoint.setText(totalPoint);

                                    if (totalPoint.equals("")) {
                                        button.setVisibility(View.GONE);
                                    } else {
                                        button.setVisibility(View.VISIBLE);
                                    }

                                    //attach tab layout with ViewPager
                                    //set gravity for tab bar
                                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                                    tabLayout.setTabMode(TabLayout.MODE_FIXED);

                                    //create and set ViewPager adapter
                                    ViewpagerRewardAdapter viewpagerRewardAdapter = new ViewpagerRewardAdapter(childFragManger, tabName.length, getLifecycle());
                                    viewPager2.setAdapter(viewpagerRewardAdapter);

                                    TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
                                        tab.setText(tabName[position]);
                                        viewPager2.setCurrentItem(tab.getPosition(), true);
                                    });
                                    tabLayoutMediator.attach();

                                    if (type.equals("payment_withdraw")) {
                                        viewPager2.setCurrentItem(1, false);
                                    }

                                    //change viewpager page when tab selected
                                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                        @Override
                                        public void onTabSelected(TabLayout.Tab tab) {
                                            viewPager2.setCurrentItem(tab.getPosition());
                                        }

                                        @Override
                                        public void onTabUnselected(TabLayout.Tab tab) {

                                        }

                                        @Override
                                        public void onTabReselected(TabLayout.Tab tab) {

                                        }
                                    });

                                    coordinatorLayout.setVisibility(View.VISIBLE);

                                    button.setOnClickListener(v -> {
                                        int point = Integer.parseInt(rewardPointRP.getMinimum_redeem_points());
                                        int compair = Integer.parseInt(totalPoint);
                                        String minimum_point = getResources().getString(R.string.minimum)
                                                + " " + rewardPointRP.getMinimum_redeem_points()
                                                + " " + getResources().getString(R.string.point_require);

                                        if (compair >= point) {
                                            startActivity(new Intent(getActivity(), RewardPointClaim.class)
                                                    .putExtra("user_id", rewardPointRP.getUser_id())
                                                    .putExtra("user_points", totalPoint));
                                        } else {
                                            function.alertBox(minimum_point);
                                        }
                                    });

                                    if (menu != null) {
                                        changeCart(menu);
                                    }

                                } else {
                                    data(true, false);
                                    function.alertBox(rewardPointRP.getMsg());
                                }

                            } else if (rewardPointRP.getStatus().equals("2")) {
                                function.suspend(rewardPointRP.getMessage());
                            } else {
                                data(true, false);
                                function.alertBox(rewardPointRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<RewardPointRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    data(true, false);
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    private void changeCart(Menu menu) {
        View cart = menu.findItem(R.id.action_point).getActionView();
        textViewMenuPointCount = cart.findViewById(R.id.textView_menu_point_count_layout);
        textViewPointMenu = cart.findViewById(R.id.textView_menu_point_layout);
        textViewMenuPointCount.setVisibility(View.GONE);
        textViewPointMenu.setVisibility(View.GONE);
        if (totalPoint != null) {
            if (textViewMenuPointCount != null) {
                textViewMenuPointCount.setText(totalPoint);
            }
        }
        Objects.requireNonNull(textViewMenuPointCount).setTypeface(textViewMenuPointCount.getTypeface(), Typeface.BOLD);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }
    }
}
