package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.ILoveDeshi.Android_Source_Code.response.ProfileRP;
import com.google.android.material.button.MaterialButton;
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

public class ChangePasswordFragment extends Fragment {

    private Function function;
    private ProgressBar progressBar;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private MaterialButton button;
    private CircleImageView imageView;
    private MaterialTextView textViewName;
    private ConstraintLayout conNoData, conMain;
    private TextInputEditText editTextOldPassword, editTextPassword, editTextConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_password_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.change_pass));
        }

        function = new Function(getActivity());

        progressDialog = new ProgressDialog(getActivity());

        imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        conNoData = view.findViewById(R.id.con_noDataFound);
        conMain = view.findViewById(R.id.con_main_cp_fragment);
        progressBar = view.findViewById(R.id.progressBar_cp_fragment);
        imageView = view.findViewById(R.id.imageView_cp_fragment);
        textViewName = view.findViewById(R.id.textView_name_cp_fragment);
        editTextOldPassword = view.findViewById(R.id.editText_old_password_cp_fragment);
        editTextPassword = view.findViewById(R.id.editText_password_cp_fragment);
        editTextConfirmPassword = view.findViewById(R.id.editText_confirm_password_cp_fragment);
        button = view.findViewById(R.id.button_edit_cp_fragment);

        progressBar.setVisibility(View.GONE);
        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        button.setOnClickListener(v -> save());

        if (function.isNetworkAvailable()) {
            profile(function.userId());
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void save() {

        String oldPassword = editTextOldPassword.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        editTextOldPassword.setError(null);
        editTextPassword.setError(null);
        editTextConfirmPassword.setError(null);

        if (oldPassword.equals("") || oldPassword.isEmpty()) {
            editTextOldPassword.requestFocus();
            editTextOldPassword.setError(getResources().getString(R.string.please_enter_old_password));
        } else if (password.equals("") || password.isEmpty()) {
            editTextPassword.requestFocus();
            editTextPassword.setError(getResources().getString(R.string.please_enter_new_password));
        } else if (confirmPassword.equals("") || confirmPassword.isEmpty()) {
            editTextConfirmPassword.requestFocus();
            editTextConfirmPassword.setError(getResources().getString(R.string.please_enter_new_confirm_password));
        } else if (!password.equals(confirmPassword)) {
            function.alertBox(getResources().getString(R.string.new_password_not_match));
        } else {
            if (function.isNetworkAvailable()) {

                editTextOldPassword.clearFocus();
                editTextPassword.clearFocus();
                editTextConfirmPassword.clearFocus();
                imm.hideSoftInputFromWindow(editTextOldPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextConfirmPassword.getWindowToken(), 0);

                passwordUpdate(function.userId(), oldPassword, password);

            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    public void profile(final String id) {
        if (getActivity() != null) {
            progressBar.setVisibility(View.VISIBLE);
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", id);
            jsObj.addProperty("AUM", "user_profile");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProfileRP> call = apiService.getUserReferenceCode(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProfileRP>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(@NotNull Call<ProfileRP> call, @NotNull Response<ProfileRP> response) {

                    if (getActivity() != null) {

                        try {
                            ProfileRP profileRP = response.body();
                            if (Objects.requireNonNull(profileRP).getStatus().equals("1")) {
                                if (profileRP.getSuccess().equals("1")) {
                                    textViewName.setText(profileRP.getName());

                                    Glide.with(getActivity().getApplicationContext()).load(profileRP.getUser_image())
                                            .placeholder(R.drawable.profile)
                                            .circleCrop()
                                            .into(imageView);

                                    conMain.setVisibility(View.VISIBLE);

                                } else {
                                    conNoData.setVisibility(View.VISIBLE);
                                    function.alertBox(profileRP.getMsg());
                                }

                            } else if (profileRP.getStatus().equals("2")) {
                                function.suspend(profileRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(profileRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<ProfileRP> call, @NotNull Throwable t) {

                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });


        }

    }

    private void passwordUpdate(String userId, String oldPassword, String password) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("old_password", oldPassword);
            jsObj.addProperty("new_password", password);
            jsObj.addProperty("AUM", "change_password");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.updatePassword(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                    if (getActivity() != null) {
                        try {
                            DataRP dataRP = response.body();
                            if (Objects.requireNonNull(dataRP).getStatus().equals("1")) {
                                if (dataRP.getSuccess().equals("1")) {
                                    editTextOldPassword.setText("");
                                    editTextPassword.setText("");
                                    editTextConfirmPassword.setText("");
                                }
                                function.alertBox(dataRP.getMsg());
                            } else if (dataRP.getStatus().equals("2")) {
                                function.suspend(dataRP.getMessage());
                            } else {
                                function.alertBox(dataRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {

                    Log.e("onFailure_data", t.toString());
                    progressDialog.dismiss();
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
