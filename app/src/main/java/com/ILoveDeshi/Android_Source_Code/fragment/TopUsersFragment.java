package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.adapter.AllTopUserAdapter;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.HomeTopUserList;
import com.ILoveDeshi.Android_Source_Code.response.TopUserRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUsersFragment extends Fragment {

    private Function function;
    private OnClick onClick;
    private Activity activity;
    private ConstraintLayout conNoData;
    private RecyclerView recyclerView;
    private AllTopUserAdapter homeTopUserAdapter;
    private List<HomeTopUserList> homeTopUserListList;
    private LayoutAnimationController animation;
    private CircleImageView imgPost1, imgPost2, imgPost3;
    private TextView tvPost1, tvPost2, tvPost3, tvScore1, tvScore2, tvScore3;
    private NumberProgressBar progressPointsC, progressPointsB, progressPointsA;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.top_user_fragment, container, false);
        homeTopUserListList = new ArrayList<>();
        activity = requireActivity();
        onClick = (position, title, type, status_type, id, tag) -> {
            if (getActivity() != null) {
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

        function = new Function(requireActivity(), onClick);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.top_user_List));
        }

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_category);

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

        conNoData.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        callData();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ic_searchView) {
            SearchFragment searchFragment = new SearchFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, searchFragment, getString(R.string.searc)).commitAllowingStateLoss();
        }
        return super.onOptionsItemSelected(item);
    }

    private void callData() {
        if (getActivity() != null) {
            if (function.isNetworkAvailable()) {
                topUserList();
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    private void topUserList() {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            if (homeTopUserAdapter == null) {
                homeTopUserListList.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "getTopUserList");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<TopUserRP> call = apiService.getTopUser(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<TopUserRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<TopUserRP> call, @NotNull Response<TopUserRP> response) {

                    if (getActivity() != null) {
                        try {
                            TopUserRP categoryRP = response.body();
                            if (Objects.requireNonNull(categoryRP).getStatus().equals("1")) {
                                if (categoryRP.getHomeTopUserLists().size() != 0) {
                                    homeTopUserListList.addAll(categoryRP.getHomeTopUserLists());
                                    tvPost1.setText(homeTopUserListList.get(0).getName());
                                    tvPost2.setText(homeTopUserListList.get(1).getName());
                                    tvPost3.setText(homeTopUserListList.get(2).getName());
                                    tvScore1.setText(homeTopUserListList.get(0).getTotal_point());
                                    tvScore2.setText(homeTopUserListList.get(1).getTotal_point());
                                    tvScore3.setText(homeTopUserListList.get(2).getTotal_point());
                                    progressPointsA.setProgress(Integer.parseInt(homeTopUserListList.get(0).getTotal_point()));
                                    progressPointsB.setProgress(Integer.parseInt(homeTopUserListList.get(1).getTotal_point()));
                                    progressPointsC.setProgress(Integer.parseInt(homeTopUserListList.get(2).getTotal_point()));

                                    if (homeTopUserListList.get(0).getUser_image().equals("") || homeTopUserListList.get(0).getUser_image() == null) {
                                        Glide.with(activity).load(homeTopUserListList.get(0).getUser_image())
                                                .placeholder(R.drawable.number_one).into(imgPost1);
                                    }
                                    if (homeTopUserListList.get(1).getUser_image().equals("") || homeTopUserListList.get(1).getUser_image() == null) {
                                        Glide.with(activity).load(homeTopUserListList.get(1).getUser_image())
                                                .placeholder(R.drawable.number_two).into(imgPost2);
                                    }
                                    if (homeTopUserListList.get(2).getUser_image().equals("") || homeTopUserListList.get(2).getUser_image() == null) {
                                        Glide.with(activity).load(homeTopUserListList.get(2).getUser_image())
                                                .placeholder(R.drawable.number_three).into(imgPost3);
                                    }
                                    homeTopUserListList.remove(0);
                                    homeTopUserListList.remove(1);
                                    homeTopUserListList.remove(2);
                                }

                                if (homeTopUserAdapter == null) {
                                    if (homeTopUserListList.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        homeTopUserAdapter = new AllTopUserAdapter(getActivity(), homeTopUserListList, "install", onClick);
                                        recyclerView.setAdapter(homeTopUserAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    homeTopUserAdapter.notifyDataSetChanged();
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
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<TopUserRP> call, @NotNull Throwable t) {
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
