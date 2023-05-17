package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.activity.ViewYouTubeApp;
import com.ILoveDeshi.Android_Source_Code.adapter.VideoAdapter;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.VideoList;
import com.ILoveDeshi.Android_Source_Code.response.VideoRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideosFragment extends Fragment {

    private Function function;
    private OnClick onClick;
    private ConstraintLayout conNoData;
    private RecyclerView rvLatest;
    private List<VideoList> appsLists;
    private VideoAdapter appsAdapter;
    private LayoutAnimationController animation;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.more_apps_fragment, container, false);
        appsLists = new ArrayList<>();

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.youtube_watch));
        }

        String type1 = requireArguments().getString("type");
        onClick = (position, title, type, status_type, id, tag) -> {
            if (getActivity() != null) {
                startActivity(new Intent(requireActivity(), ViewYouTubeApp.class)
                        .putExtra("id", id)
                        .putExtra("url", title)
                        .putExtra("time", status_type)
                        .putExtra("point", type)
                );
            }
        };
        function = new Function(requireActivity(), onClick);

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        conNoData = view.findViewById(R.id.con_noDataFound);
        rvLatest = view.findViewById(R.id.recyclerView_category);
        conNoData.setVisibility(View.GONE);
        rvLatest.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvLatest.setLayoutManager(layoutManager);
        callData();

        setHasOptionsMenu(true);
        return view;
    }

    private void callData() {
        if (function.isNetworkAvailable()) {
            getVideos();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.searchThings) {
            SearchFragment searchFragment = new SearchFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, searchFragment, getString(R.string.searc)).commitAllowingStateLoss();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getVideos() {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            if (appsAdapter == null) {
                appsLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "latest_video");
            jsObj.addProperty("type", "all");
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
                                    appsLists.addAll(categoryRP.getVideoLists());
                                }
                                if (appsAdapter == null) {
                                    if (appsLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        appsAdapter = new VideoAdapter(getActivity(), appsLists, "home", onClick);
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
                    function.hideProgressDialog(requireActivity());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }
    }
}
