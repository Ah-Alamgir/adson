package com.ILoveDeshi.Android_Source_Code.adapter;

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

public class HistoryPointAdapter extends RecyclerView.Adapter<HistoryPointAdapter.ViewHolder> {

    private final Activity activity;
    private final List<RewardPointList> rewardPointLists;

    public HistoryPointAdapter(Activity activity, List<RewardPointList> rewardPointLists) {
        this.activity = activity;
        this.rewardPointLists = rewardPointLists;
    }

    @NonNull
    @Override
    public HistoryPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.history_point_adapter, parent, false);

        return new HistoryPointAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryPointAdapter.ViewHolder holder, final int position) {

        holder.textViewType.setText(rewardPointLists.get(position).getActivity_type());
        holder.textViewDate.setText(rewardPointLists.get(position).getDate());
        holder.textViewPoint.setText(rewardPointLists.get(position).getPoints());

    }

    @Override
    public int getItemCount() {
        return rewardPointLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textViewType, textViewDate, textViewPoint;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewType = itemView.findViewById(R.id.textView_type_history_point_adapter);
            textViewDate = itemView.findViewById(R.id.textView_date_history_point_adapter);
            textViewPoint = itemView.findViewById(R.id.textView_point_history_point_adapter);

        }
    }
}
