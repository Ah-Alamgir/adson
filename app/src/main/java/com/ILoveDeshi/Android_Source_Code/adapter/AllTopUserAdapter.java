package com.ILoveDeshi.Android_Source_Code.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.HomeTopUserList;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllTopUserAdapter extends RecyclerView.Adapter<AllTopUserAdapter.ViewHolder> {

    private final Activity activity;
    private String type;
    private final List<HomeTopUserList> appsLists;
    private Function function;

    public AllTopUserAdapter(Activity activity, List<HomeTopUserList> homeWallpaperLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.appsLists = homeWallpaperLists;
        function = new Function(activity, onClick);
    }

    @NonNull
    @Override
    public AllTopUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.all_top_user_adapter, parent, false);
        return new AllTopUserAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AllTopUserAdapter.ViewHolder holder, final int position) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Glide.with(activity).load(appsLists.get(position).getUser_image())
                .placeholder(R.drawable.placeholder_landscape).into(holder.imageView);
        holder.rootCard.setOnClickListener(v -> function.onClickData(position, appsLists.get(position).getName(), "", "", appsLists.get(position).getId(), ""));
        holder.title.setText(appsLists.get(position).getName());
        holder.points.setText(activity.getResources().getString(R.string.point)+" : "+appsLists.get(position).getTotal_point());
        holder.progressPoints.setProgress(Integer.parseInt(appsLists.get(position).getTotal_point()));
    }
    @Override
    public int getItemCount() { return appsLists.size(); }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView rootCard;
        private final CircleImageView imageView;
        private final TextView title, points;
        private final NumberProgressBar progressPoints;
        public ViewHolder(View itemView) {
            super(itemView);
            rootCard = itemView.findViewById(R.id.rootCard);
            imageView = itemView.findViewById(R.id.imageView_reward_point_adapter);
            title = itemView.findViewById(R.id.textView_title_reward_point_adapter);
            points = itemView.findViewById(R.id.textView_point_reward_point_adapter);
            progressPoints = itemView.findViewById(R.id.progressPoints);
        }
    }
}
