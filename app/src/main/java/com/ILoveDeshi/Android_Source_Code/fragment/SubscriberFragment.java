package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.GoogleSignInActivity;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.adapter.SubscribeAdapter;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.SubscribeList;
import com.ILoveDeshi.Android_Source_Code.response.SubscribeRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubscriberFragment extends Fragment {

    private Function function;
    private OnClick onClick;
    private String search;
    private ConstraintLayout conNoData;
    private RecyclerView recyclerView;
    private SubscribeAdapter subscribeAdapter;
    private ArrayList<SubscribeList> subscribeLists;
    private LayoutAnimationController animation;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.games_fragment, container, false);
        subscribeLists = new ArrayList<>();
        Constant.handler = new Handler();
        onClick = (position, title, type, status_type, id, tag) -> {
            if (getActivity() != null) {
                if (getActivity() != null) {
                    Intent i = new Intent(getActivity(), GoogleSignInActivity.class);
                    i.putExtra("id", tag);
                    i.putExtra("coins", type);
                    i.putExtra("channel_name", title);
                    i.putExtra("channel_username", id);
                    i.putExtra("ytLogo", status_type);
                    getActivity().startActivity(i);
                }
            }
        };

        function = new Function(requireActivity(), onClick);
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.subscribers));
        }

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_sub_category);

        conNoData.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager3 = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager3);
        callData();

        setHasOptionsMenu(true);
        return view;
    }

    private void callData() {
        if (getActivity() != null) {
            if (function.isNetworkAvailable()) {
                getSubscriber();
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    private void getSubscriber() {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            if (subscribeAdapter == null) {
                subscribeLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "getSubscriber");
            jsObj.addProperty("value", "all");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SubscribeRP> call = apiService.getSubscriber(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<SubscribeRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<SubscribeRP> call, @NotNull Response<SubscribeRP> response) {
                    if (getActivity() != null) {
                        try {
                            SubscribeRP subscribeRP = response.body();
                            if (Objects.requireNonNull(subscribeRP).getStatus().equals("1")) {
                                if (subscribeRP.getSubscribeLists().size() != 0) {
                                    subscribeLists.addAll(subscribeRP.getSubscribeLists());
                                }
                                if (subscribeAdapter == null) {
                                    if (subscribeLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        subscribeAdapter = new SubscribeAdapter(getActivity(), subscribeLists, "AllChannels", onClick);
                                        recyclerView.setAdapter(subscribeAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    subscribeAdapter.notifyDataSetChanged();
                                }

                            } else if (subscribeRP.getStatus().equals("2")) {
                                function.suspend(subscribeRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(subscribeRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                    function.hideProgressDialog(requireActivity());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }
    }
}
