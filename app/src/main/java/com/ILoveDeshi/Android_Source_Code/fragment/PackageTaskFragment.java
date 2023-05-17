package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.ILoveDeshi.Android_Source_Code.activity.PlayAd;
import com.ILoveDeshi.Android_Source_Code.adapter.PackageTaskAdapter;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.PackageTaskList;
import com.ILoveDeshi.Android_Source_Code.response.PackageTaskRP;
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

public class PackageTaskFragment extends Fragment {

    private Function function;
    private OnClick onClick;
    private String pacID;
    private Activity activity;
    private ConstraintLayout conNoData;
    private RecyclerView rvLatest;
    private List<PackageTaskList> packageTaskLists;
    private PackageTaskAdapter appsAdapter;
    private LayoutAnimationController animation;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.package_task_fragment, container, false);
        activity = requireActivity();
        packageTaskLists = new ArrayList<>();
        pacID = requireArguments().getString("packId");
        onClick = (position, title, type, status_type, id, tag) -> {
            activity.startActivity(new Intent(activity, PlayAd.class)
                    .putExtra("type", title + " " + type + " " + activity.getString(R.string.ads_watched))
                    .putExtra("pid", id));
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
        return view;
    }

    private void callData() {
        if (function.isNetworkAvailable()) {
            getPackTaskList(pacID);
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
        if (item.getItemId() == R.id.ic_searchView) {
            SearchFragment searchFragment = new SearchFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, searchFragment, getString(R.string.searc)).commitAllowingStateLoss();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPackTaskList(String packId) {
        if (activity != null) {
            function.showProgressDialog(requireActivity());
            if (appsAdapter == null) {
                packageTaskLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "package_task_list");
            jsObj.addProperty("pack_id", packId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<PackageTaskRP> call = apiService.getTaskList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<PackageTaskRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<PackageTaskRP> call, @NotNull Response<PackageTaskRP> response) {
                    if (activity != null) {
                        try {
                            PackageTaskRP packageTaskRP = response.body();
                            if (Objects.requireNonNull(packageTaskRP).getStatus().equals("1")) {
                                if (packageTaskRP.getTaskLists().size() != 0) {
                                    packageTaskLists.addAll(packageTaskRP.getTaskLists());
                                }

                                if (appsAdapter == null) {
                                    if (packageTaskLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        appsAdapter = new PackageTaskAdapter(getActivity(), packageTaskLists, "PackageTask", onClick);
                                        rvLatest.setAdapter(appsAdapter);
                                        rvLatest.setLayoutAnimation(animation);
                                    }
                                } else {
                                    appsAdapter.notifyDataSetChanged();
                                }
                            } else {
                                function.alertBox(packageTaskRP.getMessage());
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
                public void onFailure(@NotNull Call<PackageTaskRP> call, @NotNull Throwable t) {
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
            MainActivity.toolbar.setTitle(getResources().getString(R.string.adClick));
        }
    }
}
