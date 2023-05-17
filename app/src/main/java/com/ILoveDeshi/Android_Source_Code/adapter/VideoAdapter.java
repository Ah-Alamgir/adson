package com.ILoveDeshi.Android_Source_Code.adapter;

import static com.ILoveDeshi.Android_Source_Code.util.Constant.yt;
import static com.ILoveDeshi.Android_Source_Code.util.Constant.ytImg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.VideoList;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final Activity activity;
    private String type;
    private final List<VideoList> appsLists;
    private Function function;

    public VideoAdapter(Activity activity, List<VideoList> homeWallpaperLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.appsLists = homeWallpaperLists;
        function = new Function(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String thumb = function.okGoogle(appsLists.get(position).getVideo_url());
        String vpn = appsLists.get(position).getVpn();
        holder.rootCard.setOnClickListener(v -> {
            if (vpn.equals("1")) {
                if (function.isVPN(activity)) {
                    function.onClickData(position, appsLists.get(position).getVideo_url(), appsLists.get(position).getVideo_point(), appsLists.get(position).getVideo_timer(), appsLists.get(position).getId(), "");
                } else {
                    function.showToast(activity.getString(R.string.vpn_on));
                }
            } else {
                if (!function.isVPN(activity)) {
                    function.onClickData(position, appsLists.get(position).getVideo_url(), appsLists.get(position).getVideo_point(), appsLists.get(position).getVideo_timer(), appsLists.get(position).getId(), "");
                } else {
                    function.showToast(activity.getString(R.string.vpn_off));
                }
            }
        });

        holder.points.setText(" " + appsLists.get(position).getVideo_point());
        holder.tvTitle.setText(appsLists.get(position).getVideo_title());
        holder.points.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
        holder.appTimer.setText(" " + appsLists.get(position).getVideo_timer() + " m");
        holder.appTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stopwatch, 0, 0, 0);
        if (!Constant.appRP.isLive_mode()) {
            holder.points.setVisibility(View.GONE);
            holder.appTimer.setVisibility(View.GONE);
        }
        Glide.with(activity).load(yt + thumb + ytImg)
                .placeholder(R.drawable.placeholder_landscape).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return appsLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final MaterialCardView rootCard;
        private final MaterialTextView points, appTimer, tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            rootCard = itemView.findViewById(R.id.cardOne);
            imageView = itemView.findViewById(R.id.imageView_slider_adapter);
            points = itemView.findViewById(R.id.appCoins);
            appTimer = itemView.findViewById(R.id.appTimer);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
