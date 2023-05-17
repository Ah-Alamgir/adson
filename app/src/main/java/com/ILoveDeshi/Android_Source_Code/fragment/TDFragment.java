package com.ILoveDeshi.Android_Source_Code.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.activity.ViewImage;
import com.ILoveDeshi.Android_Source_Code.response.TransactionDetailRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TDFragment extends Fragment {

    private Function function;
    private ImageView imageView;
    private ConstraintLayout conNoData;
    private MaterialCardView cardView;
    private MaterialTextView textViewMsg, textViewPayment, textViewDate, textViewRequestDate,
            textViewResponseDate, textViewPoint, textViewPaymentMode, textViewBankDetail;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.td_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.point_status));
        }

        function = new Function(requireActivity());
        String redeemId = requireArguments().getString("redeem_id");

        cardView = view.findViewById(R.id.cardView_td);
        conNoData = view.findViewById(R.id.con_noDataFound);
        imageView = view.findViewById(R.id.imageView_td);
        textViewMsg = view.findViewById(R.id.textView_msg_td);
        textViewPayment = view.findViewById(R.id.textView_payment_td);
        textViewDate = view.findViewById(R.id.textView_date_td);
        textViewRequestDate = view.findViewById(R.id.textView_requestDate_td);
        textViewResponseDate = view.findViewById(R.id.textView_responseDate_td);
        textViewPoint = view.findViewById(R.id.textView_point_td);
        textViewPaymentMode = view.findViewById(R.id.textView_payment_mode_td);
        textViewBankDetail = view.findViewById(R.id.textView_bankDetail_td);

        cardView.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        if (function.isNetworkAvailable()) {
            detail(redeemId);
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

        setHasOptionsMenu(false);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void detail(String redeemId) {

        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("redeem_id", redeemId);
            jsObj.addProperty("AUM", "get_transaction");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<TransactionDetailRP> call = apiService.getTransactionDetail(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<TransactionDetailRP>() {
                @Override
                public void onResponse(@NotNull Call<TransactionDetailRP> call, @NotNull Response<TransactionDetailRP> response) {

                    if (getActivity() != null) {

                        try {
                            TransactionDetailRP transactionDetailRP = response.body();
                            if (Objects.requireNonNull(transactionDetailRP).getStatus().equals("1")) {
                                if (transactionDetailRP.getSuccess().equals("1")) {
                                    if (!transactionDetailRP.getReceipt_img().equals("")) {
                                        Glide.with(getActivity().getApplicationContext()).load(transactionDetailRP.getReceipt_img())
                                                .placeholder(R.drawable.placeholder_portable)
                                                .into(imageView);

                                        imageView.setOnClickListener(view -> startActivity(new Intent(getActivity(), ViewImage.class)
                                                .putExtra("path", transactionDetailRP.getReceipt_img())));

                                    }

                                    if (transactionDetailRP.getTd_status().equals("1")) {
                                        textViewDate.setTextColor(getResources().getColor(R.color.green));
                                        textViewDate.setText(getResources().getString(R.string.approve_date));
                                    } else {
                                        textViewDate.setTextColor(getResources().getColor(R.color.red));
                                        textViewDate.setText(getResources().getString(R.string.reject_date));
                                    }

                                    textViewMsg.setText(transactionDetailRP.getCust_message());
                                    textViewPayment.setText(transactionDetailRP.getRedeem_price());
                                    textViewRequestDate.setText(transactionDetailRP.getRequest_date());
                                    textViewResponseDate.setText(transactionDetailRP.getResponce_date());
                                    textViewPoint.setText(transactionDetailRP.getUser_points());
                                    textViewPaymentMode.setText(transactionDetailRP.getPayment_mode());
                                    textViewBankDetail.setText(Html.fromHtml(transactionDetailRP.getBank_details()));

                                    cardView.setVisibility(View.VISIBLE);

                                } else {
                                    conNoData.setVisibility(View.VISIBLE);
                                    function.alertBox(transactionDetailRP.getMsg());
                                }

                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(transactionDetailRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<TransactionDetailRP> call, @NotNull Throwable t) {
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
