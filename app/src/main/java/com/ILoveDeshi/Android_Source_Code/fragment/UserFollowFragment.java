package com.ILoveDeshi.Android_Source_Code.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.adapter.UserFollowAdapter;
import com.ILoveDeshi.Android_Source_Code.item.UserFollowList;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.EndlessRecyclerViewScrollListener;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.response.UserFollowRP;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFollowFragment extends Fragment {

    private OnClick onClick;
    private Function function;
    private String type, userId, search;
    private List<UserFollowList> userFollowLists;
    private ConstraintLayout conNoData;
    private RecyclerView recyclerView;
    private UserFollowAdapter userFollowAdapter;
    private LayoutAnimationController animation;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.games_fragment, container, false);
        type = requireArguments().getString("type");
        userId = requireArguments().getString("user_id");
        search = requireArguments().getString("search");

        if (MainActivity.toolbar != null) {
            if (type.equals("follower")) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.followers));
            } else if (type.equals("following")) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.following));
            } else {
                MainActivity.toolbar.setTitle(search);
            }
        }

        userFollowLists = new ArrayList<>();

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

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_sub_category);

        conNoData.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    userFollowAdapter.hideHeader();
                }
            }
        });

        callData();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.ic_searchView);
        searchItem.setVisible(function.isLogin());
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener((new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (function.isNetworkAvailable()) {
                    if (function.isLogin()) {
                        UserFollowFragment userFollowFragment = new UserFollowFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "search_user");
                        bundle.putString("user_id", function.userId());
                        bundle.putString("search", query);
                        userFollowFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, userFollowFragment, query).addToBackStack(query).commitAllowingStateLoss();
                    } else {
                        function.alertBox(getResources().getString(R.string.you_have_not_login));
                    }
                    return false;
                } else {
                    function.alertBox(getResources().getString(R.string.internet_connection));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        }));

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void callData() {
        if (function.isNetworkAvailable()) {
            userFollow(userId);
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void userFollow(String userId) {

        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            if (userFollowAdapter == null) {
                userFollowLists.clear();
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            if (type.equals("follower")) {
                jsObj.addProperty("AUM", "user_followers");
            } else if (type.equals("following")) {
                jsObj.addProperty("AUM", "user_following");
            } else {
                jsObj.addProperty("search_keyword", search);
                jsObj.addProperty("AUM", "user_search");
            }
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("page", paginationIndex);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserFollowRP> call = apiService.getUserFollow(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<UserFollowRP>() {
                @Override
                public void onResponse(@NotNull Call<UserFollowRP> call, @NotNull Response<UserFollowRP> response) {

                    if (getActivity() != null) {

                        try {
                            UserFollowRP userFollowRP = response.body();
                            if (Objects.requireNonNull(userFollowRP).getStatus().equals("1")) {
                                if (userFollowRP.getUserFollowLists().size() == 0) {
                                    if (userFollowAdapter != null) {
                                        userFollowAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    userFollowLists.addAll(userFollowRP.getUserFollowLists());
                                }

                                if (userFollowAdapter == null) {
                                    if (userFollowLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        userFollowAdapter = new UserFollowAdapter(getActivity(), userFollowLists, "follow_following", onClick);
                                        recyclerView.setAdapter(userFollowAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    userFollowAdapter.notifyDataSetChanged();
                                }

                            } else if (userFollowRP.getStatus().equals("2")) {
                                function.suspend(userFollowRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(userFollowRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<UserFollowRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
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
