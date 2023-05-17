package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.item.ContactList;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.ContactRP;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContactUs extends AppCompatActivity {

    private Function function;
    private Spinner spinner;
    private MaterialButton buttonSubmit;
    private List<ContactList> contactLists;
    private String contactType, contactId;
    private InputMethodManager imm;
    private ConstraintLayout con;
    private ConstraintLayout conNoData;
    private TextInputEditText editTextName, editTextEmail, editTextMessage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        function = new Function(ContactUs.this);
        function.forceRTLIfSupported();
        contactLists = new ArrayList<>();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.contact_us));
        function.setStatusBarGradiant(ContactUs.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        con = findViewById(R.id.con_contactUs);
        conNoData = findViewById(R.id.con_noDataFound);
        spinner = findViewById(R.id.spinner_contactUs);
        editTextName = findViewById(R.id.editText_name_contactUs);
        editTextEmail = findViewById(R.id.editText_email_contactUs);
        editTextMessage = findViewById(R.id.editText_message_contactUs);
        buttonSubmit = findViewById(R.id.button_contactUs);

        con.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        if (function.isNetworkAvailable()) {
            if (function.isLogin()) {
                getContact(function.userId());
            } else {
                getContact("0");
            }
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void getContact(String userId) {
        function.showProgressDialog(ContactUs.this);
        contactLists.clear();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ContactUs.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("AUM", "get_contact");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ContactRP> call = apiService.getContactSub(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<ContactRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ContactRP> call, @NotNull Response<ContactRP> response) {

                try {

                    ContactRP contactRP = response.body();
                    if (Objects.requireNonNull(contactRP).getStatus().equals("1")) {
                        editTextName.setText(contactRP.getName());
                        editTextEmail.setText(contactRP.getEmail());
                        contactLists.add(new ContactList("", getResources().getString(R.string.select_contact_type)));
                        contactLists.addAll(contactRP.getContactLists());

                        List<String> strings = new ArrayList<String>();
                        for (int i = 0; i < contactLists.size(); i++) {
                            strings.add(contactLists.get(i).getSubject());
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ContactUs.this, android.R.layout.simple_spinner_item, strings);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(dataAdapter);

                        con.setVisibility(View.VISIBLE);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_contactUs));
                                } else {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
                                }
                                contactType = contactLists.get(position).getSubject();
                                contactId = contactLists.get(position).getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        buttonSubmit.setOnClickListener(v -> submitForm());

                    } else if (contactRP.getStatus().equals("2")) {
                        function.suspend(contactRP.getMessage());
                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        function.alertBox(contactRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(ContactUs.this);
            }

            @Override
            public void onFailure(@NotNull Call<ContactRP> call, @NotNull Throwable t) {
                Log.e("onFailure_data", t.toString());
                function.hideProgressDialog(ContactUs.this);
                conNoData.setVisibility(View.VISIBLE);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void submitForm() {

        String name = Objects.requireNonNull(editTextName.getText()).toString();
        String email = Objects.requireNonNull(editTextEmail.getText()).toString();
        String message = Objects.requireNonNull(editTextMessage.getText()).toString();
        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextMessage.setError(null);

        if (contactType.equals(getResources().getString(R.string.select_contact_type)) || contactType.equals("") || contactType.isEmpty()) {
            function.alertBox(getResources().getString(R.string.please_select_contact));
        } else if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (message.equals("") || message.isEmpty()) {
            editTextMessage.requestFocus();
            editTextMessage.setError(getResources().getString(R.string.please_enter_message));
        } else {

            editTextName.clearFocus();
            editTextEmail.clearFocus();
            editTextMessage.clearFocus();
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);

            if (function.isNetworkAvailable()) {
                contactUs(email, name, message, contactId);
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    public void contactUs(String sendEmail, String sendName, String sendMessage, String contact_subject) {
        function.showProgressDialog(ContactUs.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ContactUs.this));
        jsObj.addProperty("contact_email", sendEmail);
        jsObj.addProperty("contact_name", sendName);
        jsObj.addProperty("contact_msg", sendMessage);
        jsObj.addProperty("contact_subject", contact_subject);
        jsObj.addProperty("AUM", "user_contact_us");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.submitContact(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {

                    DataRP dataRP = response.body();
                    if (Objects.requireNonNull(dataRP).getStatus().equals("1")) {
                        if (dataRP.getSuccess().equals("1")) {
                            editTextName.setText("");
                            editTextEmail.setText("");
                            editTextMessage.setText("");
                            spinner.setSelection(0);

                        }

                        function.alertBox(dataRP.getMsg());

                    } else {
                        function.alertBox(dataRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(ContactUs.this);
            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                Log.e("onFailure_data", t.toString());
                function.hideProgressDialog(ContactUs.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.onBackPressed();
    }

}
