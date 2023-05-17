package com.ILoveDeshi.Android_Source_Code.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.MostAppsList;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class MostAppsAdapter extends RecyclerView.Adapter<MostAppsAdapter.ViewHolder> {

    private final Activity activity;
    private final List<MostAppsList> appsLists;
    private final Function function;

    public MostAppsAdapter(Activity activity, List<MostAppsList> homeWallpaperLists, String type, OnClick onClick) {
        this.activity = activity;
        this.appsLists = homeWallpaperLists;
        function = new Function(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.app_item_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Glide.with(activity).load(appsLists.get(position).getApp_image())
                .placeholder(R.drawable.app_icon).into(holder.imageView);
        holder.rootCard.setOnClickListener(v -> function.onClickData(position, appsLists.get(position).getApp_name(), "", "", appsLists.get(position).getId(), ""));
        holder.textView.setText(appsLists.get(position).getApp_name());
        holder.points.setText(" "+ appsLists.get(position).getApp_coins());
        holder.points.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
        holder.appTimer.setText(" "+ appsLists.get(position).getApp_run_time()+" m");
        holder.appTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stopwatch, 0, 0, 0);
        if (!Constant.appRP.isLive_mode()) {
            holder.points.setVisibility(View.GONE);
            holder.appTimer.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() { return appsLists.size(); }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final MaterialCardView rootCard;
        private final MaterialTextView textView, points, appTimer;
        public ViewHolder(View itemView) {
            super(itemView);
            rootCard = itemView.findViewById(R.id.cardview);
            imageView = itemView.findViewById(R.id.userProfile);
            textView = itemView.findViewById(R.id.appName);
            points = itemView.findViewById(R.id.appCoins);
            appTimer = itemView.findViewById(R.id.appTimer);
        }
    }
}
