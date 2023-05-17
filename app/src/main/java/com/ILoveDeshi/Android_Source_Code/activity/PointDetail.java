package com.ILoveDeshi.Android_Source_Code.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.adapter.EarnPointAdapter;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.PointDetailRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointDetail extends AppCompatActivity {

    private Function function;
    private RecyclerView recyclerView;
    private EarnPointAdapter earnPointAdapter;
    private ConstraintLayout conMain, conNoData;
    private LayoutAnimationController animation;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_detail);

        function = new Function(PointDetail.this);
        function.forceRTLIfSupported();
        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(PointDetail.this, resId);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.earn_point));
        function.setStatusBarGradiant(PointDetail.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        conMain = findViewById(R.id.con_main_pd);
        conNoData = findViewById(R.id.con_noDataFound);
        recyclerView = findViewById(R.id.recyclerView_pd);
        conNoData.setVisibility(View.GONE);
        conMain.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PointDetail.this);
        recyclerView.setLayoutManager(layoutManager);

        if (function.isNetworkAvailable()) {
            Point();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void Point() {
        function.showProgressDialog(PointDetail.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PointDetail.this));
        jsObj.addProperty("AUM", "points_details");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PointDetailRP> call = apiService.getPointDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PointDetailRP>() {
            @Override
            public void onResponse(@NotNull Call<PointDetailRP> call, @NotNull Response<PointDetailRP> response) {

                try {
                    PointDetailRP pointDetailRP = response.body();
                    if (Objects.requireNonNull(pointDetailRP).getStatus().equals("1")) {
                        if (pointDetailRP.getPointDetailLists().size() == 0) {
                            conNoData.setVisibility(View.VISIBLE);
                        } else {
                            earnPointAdapter = new EarnPointAdapter(PointDetail.this, pointDetailRP.getPointDetailLists());
                            recyclerView.setAdapter(earnPointAdapter);
                            recyclerView.setLayoutAnimation(animation);
                        }

                        conMain.setVisibility(View.VISIBLE);

                    } else {
                        function.alertBox(pointDetailRP.getMessage());
                        conNoData.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(PointDetail.this);
            }

            @Override
            public void onFailure(@NotNull Call<PointDetailRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(PointDetail.this);
                conNoData.setVisibility(View.VISIBLE);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
