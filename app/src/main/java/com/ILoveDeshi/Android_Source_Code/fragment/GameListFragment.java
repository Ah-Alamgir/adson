package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.adapter.GameAdapter;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.GameList;
import com.ILoveDeshi.Android_Source_Code.response.GameRP;
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


public class GameListFragment extends Fragment {

    private Function function;
    private OnClick onClick;
    private String search;
    private ConstraintLayout conNoData;
    private RecyclerView recyclerView;
    private GameAdapter gameAdapter;
    private ArrayList<GameList> gameLists;
    private LayoutAnimationController animation;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.games_fragment, container, false);


        gameLists = new ArrayList<>();
        Constant.handler = new Handler();
        onClick = (position, title, type, status_type, id, tag) -> {
            if (getActivity() != null) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                customTabsIntent.launchUrl(requireActivity(), Uri.parse(id));
                Constant.isGameRunning = false;
                Constant.r = () -> {
                    if (getActivity() != null) {
                        function.showToast(getResources().getString(R.string.congrats) + Constant.appRP.getOnline_game_points());
                        Log.d("MyOnlineGames", "Badge at index");
                        function.secureAllWorld(function.userId(), title, String.valueOf(Constant.appRP.getOnline_game_points()));
                        Constant.isGameRunning = true;
                    }
                };
                Constant.handler.postDelayed(Constant.r, (long) Constant.appRP.getOnline_game_timer() * 60 * 1000);
            }
        };

        function = new Function(requireActivity(), onClick);
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.onlineGames));
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
                topUserList();
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    private void topUserList() {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            if (gameAdapter == null) {
                gameLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "getGameList");
            jsObj.addProperty("value", "all");
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
                                        gameAdapter = new GameAdapter(getActivity(), gameLists, "AllUserList", onClick);
                                        recyclerView.setAdapter(gameAdapter);
                                        recyclerView.setLayoutAnimation(animation);
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
                    function.hideProgressDialog(requireActivity());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }
    }
}
