package com.ILoveDeshi.Android_Source_Code.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.item.RewardPointList;
import com.ILoveDeshi.Android_Source_Code.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RewardPointAdapter extends RecyclerView.Adapter<RewardPointAdapter.ViewHolder> {

    private final Activity activity;
    private final List<RewardPointList> rewardPointLists;

    public RewardPointAdapter(Activity activity, List<RewardPointList> rewardPointLists) {
        this.activity = activity;
        this.rewardPointLists = rewardPointLists;
    }

    @NonNull
    @Override
    public RewardPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.reward_point_adapter, parent, false);

        return new RewardPointAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RewardPointAdapter.ViewHolder holder, final int position) {

        holder.textViewTitle.setText(rewardPointLists.get(position).getActivity_type());
        holder.textViewType.setText(rewardPointLists.get(position).getActivity_type());
        holder.textViewDate.setText(rewardPointLists.get(position).getDate() + "  " + rewardPointLists.get(position).getTime());
        holder.textViewPoint.setText(rewardPointLists.get(position).getPoints());

    }

    @Override
    public int getItemCount() {
        return rewardPointLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private MaterialTextView textViewTitle, textViewDate, textViewType, textViewPoint;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_rewardPoint_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_rewardPoint_adapter);
            textViewDate = itemView.findViewById(R.id.textView_dateTime_rewardPoint_adapter);
            textViewPoint = itemView.findViewById(R.id.textView_point_rewardPoint_adapter);
            textViewType = itemView.findViewById(R.id.textView_type_rewardPoint_adapter);

        }
    }
}
