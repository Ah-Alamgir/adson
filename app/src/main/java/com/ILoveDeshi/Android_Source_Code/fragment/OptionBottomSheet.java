package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.CLIPBOARD_SERVICE;

public class OptionBottomSheet extends BottomSheetDialogFragment {

    private Function function;
    private Dialog dialog;
    private String reportType, message;
    private TextInputEditText editText;
    private RadioGroup radioGroup;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;

    public OptionBottomSheet() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_option, container, false);

        function = new Function(getActivity());
        if (function.isRtl()) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        Bundle bundle = getArguments();
        final String id = bundle.getString("id");
        final String url = bundle.getString("url");
        final String status_type = bundle.getString("status_type");

        progressDialog = new ProgressDialog(getActivity());

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        ConstraintLayout conShare = view.findViewById(R.id.con_share_bottomSheet);
        ConstraintLayout conReport = view.findViewById(R.id.con_report_bottomSheet);
        ConstraintLayout conCopy = view.findViewById(R.id.con_copy_bottomSheet);

        if (!status_type.equals("quote")) {
            conCopy.setVisibility(View.GONE);
        } else {
            conCopy.setVisibility(View.VISIBLE);
        }

        conShare.setOnClickListener(v -> {
            if (status_type.equals("quote")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(intent);
            } else {
                if (function.isNetworkAvailable()) {
                    new ShareVideo().execute(url, id, status_type);
                } else {
                    function.alertBox(getResources().getString(R.string.internet_connection));
                }
            }
        });

        conCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", url);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), getResources().getString(R.string.copy_quote), Toast.LENGTH_SHORT).show();
        });

        conReport.setOnClickListener(v -> {
            if (function.isLogin()) {

                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_sheet_report);
                if (function.isRtl()) {
                    dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }
                dialog.getWindow().setLayout(ViewPager.LayoutParams.FILL_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

                radioGroup = dialog.findViewById(R.id.radioGroup_report_bottomSheet);
                editText = dialog.findViewById(R.id.editText_report_bottomSheet);
                MaterialButton button = dialog.findViewById(R.id.button_send_report_bottomSheet);

                radioGroup.clearCheck();

                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    MaterialRadioButton rb = group.findViewById(checkedId);
                    if (null != rb && checkedId > -1) {
                        reportType = rb.getText().toString();
                    }
                });

                button.setOnClickListener(vButton -> report(id, status_type));

                dialog.show();
            } else {
                function.alertBox(getResources().getString(R.string.you_have_not_login));
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class ShareVideo extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String iconsStoragePath;
        private File sdIconStorageDir;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel_dialog), (dialog, which) -> {
                if (sdIconStorageDir != null) {
                    sdIconStorageDir.delete();
                }
                dialog.dismiss();
                cancel(true);
            });
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            int count;
            try {
                URL url = new URL(params[0]);
                String id = params[1];
                String status_type = params[2];
                iconsStoragePath = getActivity().getExternalCacheDir().getAbsolutePath();
                String filePath;
                if (status_type.equals("image")) {
                    filePath = "file" + id + ".jpg";
                } else if (status_type.equals("gif")) {
                    filePath = "file" + id + ".gif";
                } else {
                    filePath = "file" + id + ".mp4";
                }

                sdIconStorageDir = new File(iconsStoragePath, filePath);

                //create storage directories, if they don't exist
                if (sdIconStorageDir.exists()) {
                    Log.d("File_name", "File_name");
                } else {
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty("Accept-Encoding", "identity");
                    connection.connect();
                    int lenghtOfFile = connection.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    OutputStream output = new FileOutputStream(sdIconStorageDir);
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        progressDialog.setProgress((int) (total * 100 / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("*/*");
            File media = new File(sdIconStorageDir.toString());
            Uri uri = Uri.fromFile(media);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, getResources().getString(R.string.share_to)));

        }

    }
    private void report(String postId, String statusType) {

        editText.setError(null);

        message = editText.getText().toString();
        editText.clearFocus();
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        if (message == null || message.equals("") || message.isEmpty()) {
            editText.requestFocus();
            editText.setError(getResources().getString(R.string.please_enter_message));
        } else if (reportType == null || reportType.equals("") || reportType.isEmpty()) {
            function.alertBox(getResources().getString(R.string.please_select_option));
        } else {
            String id = function.userId();
            submit(id, postId, reportType, statusType, message);
        }

    }

    private void submit(String userId, String postId, String reportType, String statusType, String reportMessage) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("post_id", postId);
            jsObj.addProperty("type", statusType);
            jsObj.addProperty("report_type", reportType);
            jsObj.addProperty("report_text", reportMessage);
            jsObj.addProperty("AUM", "status_report");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.submitReview(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                    if (getActivity() != null) {

                        try {
                            DataRP dataRP = response.body();
                            if (Objects.requireNonNull(dataRP).getStatus().equals("1")) {
                                if (dataRP.getSuccess().equals("1")) {
                                    editText.setText("");
                                    radioGroup.clearCheck();
                                    dialog.dismiss();
                                    dismiss();
                                    Toast.makeText(getActivity(), dataRP.getMsg(), Toast.LENGTH_SHORT).show();
                                } else {
                                    function.alertBox(dataRP.getMsg());
                                }

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
                    Log.e("fail", t.toString());
                    progressDialog.dismiss();
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    //---------------report-------------//

}
