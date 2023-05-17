package com.ILoveDeshi.Android_Source_Code.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.LoginRP;
import com.ILoveDeshi.Android_Source_Code.response.RegisterRP;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import cn.refactor.library.SmoothCheckBox;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {

    private Function function;
    private SmoothCheckBox checkBox;
    private FloatingActionButton fabWhatsApp;
    private TextInputEditText editTextEmail, editTextPassword;

    public static final String mypreference = "statusLogin";
    public static final String pref_email = "pref_email";
    public static final String pref_password = "pref_password";
    public static final String pref_check = "pref_check";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    private InputMethodManager imm;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        function = new Function(Login.this);
        function.forceRTLIfSupported();

        pref = getSharedPreferences(mypreference, 0);
        editor = pref.edit();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();

        editTextEmail = findViewById(R.id.editText_email_login);
        editTextPassword = findViewById(R.id.editText_password_login);
        fabWhatsApp = findViewById(R.id.fabWhatsApp);
        MaterialCardView buttonLogin = findViewById(R.id.button_login);
        final ConstraintLayout llGoogleSign = findViewById(R.id.con_google_login);
        final FrameLayout frameLayoutFbSign = findViewById(R.id.frameLayout_login);
        MaterialButton buttonSkip = findViewById(R.id.button_skip_login);
        MaterialTextView textViewRegister = findViewById(R.id.textView_register_login);
        MaterialTextView textViewFP = findViewById(R.id.textView_fp_login);
        checkBox = findViewById(R.id.checkbox_login);
        checkBox.setChecked(false);

        if (pref.getBoolean(pref_check, false)) {
            editTextEmail.setText(pref.getString(pref_email, null));
            editTextPassword.setText(pref.getString(pref_password, null));
            checkBox.setChecked(true);
        } else {
            editTextEmail.setText("");
            editTextPassword.setText("");
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener((checkBox1, isChecked) -> {
            if (isChecked) {
                editor.putString(pref_email, Objects.requireNonNull(editTextEmail.getText()).toString());
                editor.putString(pref_password, Objects.requireNonNull(editTextPassword.getText()).toString());
                editor.putBoolean(pref_check, true);
            } else {
                editor.putBoolean(pref_check, false);
            }
            editor.commit();
        });
        PackageManager pm = getPackageManager();
        fabWhatsApp.setOnClickListener(v -> {
            if (function.isPackageInstalled("com.whatsapp", pm)) {
                sendMessageToOwner(getString(R.string.wp1) + " : " + function.pref.getString(function.userName, "") + getString(R.string.wp2) + " : " + getString(R.string.app_name), "com.whatsapp");
            } else {
                if (function.isPackageInstalled("com.whatsapp.w4b", pm)) {
                    sendMessageToOwner(getString(R.string.wp1) + " : " + function.pref.getString(function.userName, "") + getString(R.string.wp2) + " : " + getString(R.string.app_name), "com.whatsapp.w4b");
                } else {
                    function.alertBox(getString(R.string.please_install_whatsapp));
                }
            }
        });

        buttonLogin.setOnClickListener(v -> login());
        llGoogleSign.setOnClickListener(view -> signIn());

        frameLayoutFbSign.setOnClickListener(v -> {
            if (v == frameLayoutFbSign) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList(EMAIL));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fbUser(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        textViewRegister.setOnClickListener(v -> {
            Function.loginBack = false;
            startActivity(new Intent(Login.this, Register.class));
        });

        buttonSkip.setOnClickListener(v -> {
            if (Function.loginBack) {
                Function.loginBack = false;
                onBackPressed();
            } else {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        });

        textViewFP.setOnClickListener(v -> {
            Function.loginBack = false;
            startActivity(new Intent(Login.this, ForgetPassword.class));
        });

    }

    private void signIn() {
        if (function.isNetworkAvailable()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();

            registerSocialNetwork(id, name, email, "google");

        } catch (ApiException e) {

        }
    }

    private void fbUser(LoginResult loginResult) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    registerSocialNetwork(id, name, email, "facebook");
                } catch (JSONException e) {
                    try {
                        String id = object.getString("id");
                        String name = object.getString("name");
                        registerSocialNetwork(id, name, "", "facebook");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }


    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void login() {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString();
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.isEmpty()) {
            editTextPassword.requestFocus();
            editTextPassword.setError(getResources().getString(R.string.please_enter_password));
        } else {

            editTextEmail.clearFocus();
            editTextPassword.clearFocus();
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);

            if (function.isNetworkAvailable()) {
                login(email, password);
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    public void login(final String sendEmail, final String sendPassword) {
        function.showProgressDialog(Login.this);
        OSDeviceState device = OneSignal.getDeviceState();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("player_id", Objects.requireNonNull(device).getUserId());
        jsObj.addProperty("AUM", "user_login");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getLogin(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {

                try {
                    LoginRP loginRP = response.body();
                    if (Objects.requireNonNull(loginRP).getStatus().equals("1")) {
                        if (loginRP.getSuccess().equals("1")) {
                            OneSignal.sendTag("user_id", loginRP.getUser_id());
                            OneSignal.sendTag("player_id", device.getUserId());

                            function.editor.putBoolean(function.prefLogin, true);
                            function.editor.putString(function.profileId, loginRP.getUser_id());
                            function.editor.putString(function.userName, loginRP.getName());
                            function.editor.putString(function.loginType, "normal");
                            function.editor.commit();

                            if (checkBox.isChecked()) {
                                editor.putString(pref_email, Objects.requireNonNull(editTextEmail.getText()).toString());
                                editor.putString(pref_password, Objects.requireNonNull(editTextPassword.getText()).toString());
                                editor.putBoolean(pref_check, true);
                                editor.commit();
                            }

                            editTextEmail.setText("");
                            editTextPassword.setText("");

                            if (Function.loginBack) {
                                Function.loginBack = false;
                                onBackPressed();
                            } else {
                                startActivity(new Intent(Login.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();
                            }
                        } else {
                            function.alertBox(loginRP.getMsg());
                        }

                    } else {
                        function.alertBox(loginRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Login.this);
            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(Login.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }


    @SuppressLint("HardwareIds")
    public void registerSocialNetwork(String id, String sendName, String sendEmail, String type) {
        function.showProgressDialog(Login.this);
        OSDeviceState device = OneSignal.getDeviceState();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("type", type);
        jsObj.addProperty("auth_id", id);
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("device_id", function.getDeviceId());
        jsObj.addProperty("player_id", Objects.requireNonNull(device).getUserId());
        jsObj.addProperty("AUM", "user_register");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterRP> call = apiService.getRegisterDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RegisterRP>() {
            @Override
            public void onResponse(@NotNull Call<RegisterRP> call, @NotNull Response<RegisterRP> response) {

                try {
                    RegisterRP registerRP = response.body();
                    if (Objects.requireNonNull(registerRP).getStatus().equals("1")) {
                        if (registerRP.getSuccess().equals("1")) {

                            function.editor.putBoolean(function.isVerification, false);
                            function.editor.commit();

                            OneSignal.sendTag("user_id", registerRP.getUser_id());
                            OneSignal.sendTag("player_id", device.getUserId());

                            function.editor.putBoolean(function.prefLogin, true);
                            function.editor.putString(function.profileId, registerRP.getUser_id());
                            function.editor.putString(function.userName, registerRP.getName());
                            function.editor.putString(function.loginType, type);
                            function.editor.commit();

                            if (Function.loginBack) {
                                Function.loginBack = false;
                                onBackPressed();
                            } else {
                                if (registerRP.getReferral_code().equals("true")) {
                                    startActivity(new Intent(Login.this, EnterReferenceCode.class)
                                            .putExtra("user_id", registerRP.getUser_id())
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } else {
                                    startActivity(new Intent(Login.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                }
                                finishAffinity();
                            }

                        } else {
                            if (type.equals("google")) {
                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(Login.this, task -> {
                                            function.editor.putBoolean(function.prefLogin, false);
                                            function.editor.commit();
                                        });
                            } else {
                                LoginManager.getInstance().logOut();
                            }
                            function.alertBox(registerRP.getMsg());
                        }

                    } else {
                        function.alertBox(registerRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Login.this);
            }

            @Override
            public void onFailure(@NotNull Call<RegisterRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(Login.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void sendMessageToOwner(String message, String packageName) {
        PackageManager packageManager = Login.this.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + Constant.appRP.getApp_contact() + "&text=" + message;
            i.setPackage(packageName);
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Function.loginBack = false;
        super.onBackPressed();
    }
}
