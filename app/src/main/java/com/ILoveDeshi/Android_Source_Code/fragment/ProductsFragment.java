package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.adapter.ProductsAdapter;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.ProductList;
import com.ILoveDeshi.Android_Source_Code.response.ProductsRP;
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

public class ProductsFragment extends Fragment {

    private Function function;
    private OnClick onClick;
    private ConstraintLayout conNoData;
    private RecyclerView rvLatest;
    private List<ProductList> productLists;
    private ProductsAdapter productsAdapter;
    private LayoutAnimationController animation;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.more_apps_fragment, container, false);
        productLists = new ArrayList<>();
        MainActivity.toolbar.setTitle(getResources().getString(R.string.affiliate_product));

        onClick = (position, title, type, status_type, id, tag) -> {
            if (getActivity() != null) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                customTabsIntent.launchUrl(requireActivity(), Uri.parse(id));
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
            getHomeApps();
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

    private void getHomeApps() {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            if (productsAdapter == null) {
                productLists.clear();
            }
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "affiliate_product_list");
            jsObj.addProperty("value", "all");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProductsRP> call = apiService.getProducts(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProductsRP>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NotNull Call<ProductsRP> call, @NotNull Response<ProductsRP> response) {
                    if (getActivity() != null) {
                        try {
                            ProductsRP productsRP = response.body();
                            if (Objects.requireNonNull(productsRP).getStatus().equals("1")) {
                                if (productsRP.getProductLists().size() != 0) {
                                    productLists.addAll(productsRP.getProductLists());
                                }

                                if (productsAdapter == null) {
                                    if (productLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        productsAdapter = new ProductsAdapter(getActivity(), productLists, "products", onClick);
                                        rvLatest.setAdapter(productsAdapter);
                                        rvLatest.setLayoutAnimation(animation);
                                    }
                                } else {
                                    productsAdapter.notifyDataSetChanged();
                                }
                            } else {
                                function.alertBox(productsRP.getMessage());
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
                public void onFailure(@NotNull Call<ProductsRP> call, @NotNull Throwable t) {
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
