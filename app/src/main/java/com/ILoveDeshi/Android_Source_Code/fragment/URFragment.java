package com.ILoveDeshi.Android_Source_Code.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.adapter.UserRMAdapter;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.response.UserRedeemRP;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class URFragment extends Fragment {

    private Function function;
    private OnClick onClick;
    private ConstraintLayout conNoData;
    private RecyclerView recyclerView;
    private UserRMAdapter userRMAdapter;
    private LayoutAnimationController animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_rm_fragment, container, false);

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        onClick = (position, title, type, status_type, id, tag) -> {
            if (tag.equals("td")) {
                TDFragment tdFragment = new TDFragment();
                Bundle bundle = new Bundle();
                bundle.putString("redeem_id", id);
                tdFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, tdFragment, type).addToBackStack(type).commitAllowingStateLoss();
            } else {
                URHistoryFragment urHistoryFragment = new URHistoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("redeem_id", id);
                urHistoryFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, urHistoryFragment, type).addToBackStack(type).commitAllowingStateLoss();
            }
        };
        function = new Function(requireActivity(), onClick);

        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_user_rm_fragment);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        conNoData.setVisibility(View.GONE);

        if (function.isNetworkAvailable()) {
            history(function.userId());
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void history(final String id) {

        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", id);
            jsObj.addProperty("AUM", "user_redeem_history");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserRedeemRP> call = apiService.getUserRedeemHistory(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<UserRedeemRP>() {
                @Override
                public void onResponse(@NotNull Call<UserRedeemRP> call, @NotNull Response<UserRedeemRP> response) {

                    if (getActivity() != null) {

                        try {
                            UserRedeemRP userRedeemRP = response.body();
                            if (Objects.requireNonNull(userRedeemRP).getStatus().equals("1")) {
                                if (userRedeemRP.getUserRMLists().size() == 0) {
                                    conNoData.setVisibility(View.VISIBLE);
                                } else {
                                    userRMAdapter = new UserRMAdapter(getActivity(), userRedeemRP.getUserRMLists(), onClick, "ur");
                                    recyclerView.setAdapter(userRMAdapter);
                                    recyclerView.setLayoutAnimation(animation);
                                }

                            } else if (userRedeemRP.getStatus().equals("2")) {
                                function.suspend(userRedeemRP.getMessage());
                            } else {
                                function.alertBox(userRedeemRP.getMessage());
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
                public void onFailure(@NotNull Call<UserRedeemRP> call, @NotNull Throwable t) {
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
