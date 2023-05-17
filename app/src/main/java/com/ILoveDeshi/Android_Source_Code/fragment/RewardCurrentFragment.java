package com.ILoveDeshi.Android_Source_Code.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.adapter.RewardPointAdapter;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.URPListRP;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardCurrentFragment extends Fragment {

    private Function function;
    private RecyclerView recyclerView;
    private ConstraintLayout conNoData;
    private RewardPointAdapter rewardPointAdapter;
    private LayoutAnimationController layoutAnimationController;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_rm_fragment, container, false);

        function = new Function(requireActivity());

        int resId = R.anim.layout_animation_fall_down;
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_user_rm_fragment);
        conNoData.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (function.isNetworkAvailable()) {
            rewardPoint(function.userId());
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;
    }

    private void rewardPoint(String id) {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", id);
            jsObj.addProperty("AUM", "user_rewads_point");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<URPListRP> call = apiService.getUserRewardPointList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<URPListRP>() {
                @Override
                public void onResponse(@NotNull Call<URPListRP> call, @NotNull Response<URPListRP> response) {

                    if (getActivity() != null) {
                        try {
                            URPListRP urpListRP = response.body();
                            if (Objects.requireNonNull(urpListRP).getStatus().equals("1")) {
                                if (urpListRP.getRewardPointLists().size() == 0) {
                                    conNoData.setVisibility(View.VISIBLE);
                                } else {
                                    rewardPointAdapter = new RewardPointAdapter(getActivity(), urpListRP.getRewardPointLists());
                                    recyclerView.setAdapter(rewardPointAdapter);
                                    recyclerView.setLayoutAnimation(layoutAnimationController);
                                }

                            } else if (urpListRP.getStatus().equals("2")) {
                                function.suspend(urpListRP.getMessage());
                            } else {
                                function.alertBox(urpListRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<URPListRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    conNoData.setVisibility(View.VISIBLE);
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
