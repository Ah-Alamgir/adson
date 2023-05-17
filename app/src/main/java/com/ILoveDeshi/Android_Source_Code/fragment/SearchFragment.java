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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.response.SearchRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Function function;
    private ConstraintLayout conNoData;
    private Spinner spinner;
    private TextInputEditText etMessage;
    private MaterialCardView showStatistics;
    private CircleImageView imageView;
    private MaterialTextView tvAppName, tvAppDesc;
    private final String[] language = {"Application", "Games", "Website"};

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sub_cat_fragment, container, false);
        function = new Function(requireActivity());
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getString(R.string.searc));
        }

        conNoData = view.findViewById(R.id.con_noDataFound);
        spinner = view.findViewById(R.id.spinnerSearch);
        etMessage = view.findViewById(R.id.etMessage);
        ImageView sendMessage = view.findViewById(R.id.sendMessage);
        showStatistics = view.findViewById(R.id.showStatistics);
        imageView = view.findViewById(R.id.imageView);
        tvAppDesc = view.findViewById(R.id.tvAppDesc);
        tvAppName = view.findViewById(R.id.tvAppName);
        showStatistics.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, language);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        sendMessage.setOnClickListener(v -> {
            showStatistics.setVisibility(View.GONE);
            if (!Objects.requireNonNull(etMessage.getText()).toString().equals("")) {
                getSearch(etMessage.getText().toString());
            } else {
                function.alertBox(getString(R.string.please_enter_message));
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getSearch(String likes) {
        function.showProgressDialog(requireActivity());
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(requireActivity()));
        jsObj.addProperty("AUM", "get_search");
        jsObj.addProperty("like", likes);
        jsObj.addProperty("type", spinner.getSelectedItem().toString());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SearchRP> call = apiService.getSearch(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SearchRP>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SearchRP> call, @NotNull Response<SearchRP> response) {
                try {
                    SearchRP spinnerRP = response.body();
                    if (Objects.requireNonNull(spinnerRP).getStatus().equals("1")) {
                        tvAppDesc.setText(spinnerRP.getDesc());
                        tvAppName.setText(spinnerRP.getName());
                        if (spinnerRP.getImage().endsWith(".jpg") || spinnerRP.getImage().endsWith(".png")) {
                            Glide.with(requireActivity()).load(spinnerRP.getImage())
                                    .placeholder(R.drawable.placeholder_portable).into(imageView);
                        }
                        conNoData.setVisibility(View.GONE);
                        showStatistics.setVisibility(View.VISIBLE);
                        showStatistics.setOnClickListener(v -> {
                            if (spinner.getSelectedItem().toString().equals("Application")) {
                                ViewAppFragment viewAppFragment = new ViewAppFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", spinnerRP.getId());
                                bundle.putString("type", "install");
                                bundle.putString("mode", "search");
                                viewAppFragment.setArguments(bundle);
                                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, viewAppFragment, getString(R.string.searc)).addToBackStack(getString(R.string.searc)).commitAllowingStateLoss();
                            } else if (spinner.getSelectedItem().toString().equals("Games")) {
                                if (getActivity() != null) {
                                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                                    customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    customTabsIntent.launchUrl(requireActivity(), Uri.parse(spinnerRP.getUrl()));
                                    Constant.isGameRunning = false;
                                    Constant.r = () -> {
                                        if (getActivity() != null) {
                                            function.showToast(getResources().getString(R.string.congrats) + Constant.appRP.getOnline_game_points());
                                            Log.d("MyOnlineGames", "Badge at index");
                                            function.secureAllWorld(function.userId(), spinnerRP.getName(), String.valueOf(Constant.appRP.getOnline_game_points()));
                                            Constant.isGameRunning = true;
                                        }
                                    };
                                    Constant.handler.postDelayed(Constant.r, (long) Constant.appRP.getOnline_game_timer() * 60 * 1000);
                                } else {
                                    function.alertBox(getResources().getString(R.string.wrong));
                                }
                            } else if (spinner.getSelectedItem().toString().equals("Website")) {
                                VieWebFragment vieWebFragment = new VieWebFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", spinnerRP.getId());
                                bundle.putString("type", "all");
                                bundle.putString("time", spinnerRP.getWebsite_timer());
                                vieWebFragment.setArguments(bundle);
                                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, vieWebFragment, getString(R.string.webOpen)).addToBackStack(getString(R.string.webOpen)).commitAllowingStateLoss();
                            }
                        });
                    } else if (spinnerRP.getStatus().equals("2")) {
                        function.suspend(spinnerRP.getMessage());
                    } else {
                        function.alertBox(spinnerRP.getMessage());
                        conNoData.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(requireActivity());
            }

            @Override
            public void onFailure(@NotNull Call<SearchRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(requireActivity());
                conNoData.setVisibility(View.VISIBLE);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        showStatistics.setVisibility(View.GONE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.searc));
        }
    }
}
