package com.ILoveDeshi.Android_Source_Code.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.item.UserRMList;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class UserRMAdapter extends RecyclerView.Adapter<UserRMAdapter.ViewHolder> {

    private final Function function;
    private final Activity activity;
    private final String type;
    private final Animation myAnim;
    private final List<UserRMList> userRMLists;

    public UserRMAdapter(Activity activity, List<UserRMList> rewardPointLists, OnClick onClick, String type) {
        this.activity = activity;
        this.userRMLists = rewardPointLists;
        this.type = type;
        function = new Function(activity, onClick);
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
    }

    @NonNull
    @Override
    public UserRMAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.user_rm_adapter, parent, false);

        return new UserRMAdapter.ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull UserRMAdapter.ViewHolder holder, final int position) {

        holder.textViewPoint.setText(activity.getResources().getString(R.string.user_point) + " " + userRMLists.get(position).getUser_points());
        holder.textViewDate.setText(userRMLists.get(position).getRequest_date());
        holder.textViewPrice.setText(userRMLists.get(position).getRedeem_price());

        switch (userRMLists.get(position).getStatus()) {
            case "0":
                holder.textViewStatus.setText(activity.getResources().getString(R.string.pending));
                holder.textViewStatus.setBackground(activity.getResources().getDrawable(R.drawable.button_background));
                break;
            case "1":
                holder.textViewStatus.setText(activity.getResources().getString(R.string.approve));
                holder.textViewStatus.setBackground(activity.getResources().getDrawable(R.drawable.approve_bg));
                break;
            default:
                holder.textViewStatus.setText(activity.getResources().getString(R.string.reject));
                holder.textViewStatus.setBackground(activity.getResources().getDrawable(R.drawable.reject_bg));
                break;
        }

        holder.textViewStatus.setOnClickListener(view -> {
            holder.textViewStatus.startAnimation(myAnim);
            if (!userRMLists.get(position).getStatus().equals("0")) {
                function.onClickData(position, activity.getResources().getString(R.string.point_status), type, "", userRMLists.get(position).getRedeem_id(), "td");
            } else {
                function.alertBox(activity.getResources().getString(R.string.payment_pending));
            }
        });

        holder.conDetail.setOnClickListener(v -> {
            holder.conDetail.startAnimation(myAnim);
            function.onClickData(position, activity.getResources().getString(R.string.reward_point), type, "", userRMLists.get(position).getRedeem_id(), "uh");
        });

        holder.textViewDetail.setOnClickListener(view -> {
            holder.textViewDetail.startAnimation(myAnim);
            function.onClickData(position, activity.getResources().getString(R.string.reward_point), type, "", userRMLists.get(position).getRedeem_id(), "uh");
        });

    }

    @Override
    public int getItemCount() {
        return userRMLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout conDetail;
        private MaterialTextView textViewPoint, textViewDate, textViewPrice, textViewStatus, textViewDetail;

        public ViewHolder(View itemView) {
            super(itemView);

            conDetail = itemView.findViewById(R.id.con_detail_rm_adapter);
            textViewPoint = itemView.findViewById(R.id.textView_point_rm_adapter);
            textViewDate = itemView.findViewById(R.id.textView_date_rm_adapter);
            textViewPrice = itemView.findViewById(R.id.textView_price_rm_adapter);
            textViewStatus = itemView.findViewById(R.id.textView_status_rm_adapter);
            textViewDetail = itemView.findViewById(R.id.textView_detail_rm_adapter);

        }
    }
}
