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
import com.ILoveDeshi.Android_Source_Code.item.WebsiteList;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.ViewHolder> {

    private final Activity activity;
    private final List<WebsiteList> websiteLists;
    private final Function function;

    public WebsiteAdapter(Activity activity, List<WebsiteList> homeWallpaperLists, String type, OnClick onClick) {
        this.activity = activity;
        this.websiteLists = homeWallpaperLists;
        function = new Function(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.web_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Glide.with(activity).load(websiteLists.get(position).getWebsite_logo())
                .placeholder(R.drawable.app_icon).into(holder.imageView);
        String vpn = websiteLists.get(position).getVpn();
        holder.rootCard.setOnClickListener(v -> {
            if (vpn.equals("1")) {
                if (function.isVPN(activity)) {
                    function.onClickData(position, websiteLists.get(position).getWebsite_title(), "", websiteLists.get(position).getWebsite_timer(), websiteLists.get(position).getId(), websiteLists.get(position).getVpn());
                } else {
                    function.showToast(activity.getString(R.string.vpn_on));
                }
            } else {
                if (!function.isVPN(activity)) {
                    function.onClickData(position, websiteLists.get(position).getWebsite_title(), "", websiteLists.get(position).getWebsite_timer(), websiteLists.get(position).getId(), websiteLists.get(position).getVpn());
                } else {
                    function.showToast(activity.getString(R.string.vpn_off));
                }
            }
        });
        holder.textView.setText(websiteLists.get(position).getWebsite_title());
        holder.points.setText(" " + websiteLists.get(position).getWebsite_coins());
        holder.points.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
        holder.appTimer.setText(" " + websiteLists.get(position).getWebsite_timer() + " m");
        holder.appTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stopwatch, 0, 0, 0);
        if (!Constant.appRP.isLive_mode()) {
            holder.points.setVisibility(View.GONE);
            holder.appTimer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return websiteLists.size();
    }

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
