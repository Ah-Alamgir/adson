package com.ILoveDeshi.Android_Source_Code.adapter;

import static com.ILoveDeshi.Android_Source_Code.util.Constant.isGameRunning;
import static com.ILoveDeshi.Android_Source_Code.util.Constant.r;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.GameList;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;


public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private final ArrayList<GameList> android;
    private final Function function;
    private final Activity activity;
    private final String type;

    public GameAdapter(Activity activity, ArrayList<GameList> android, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.android = android;
        function = new Function(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.game_item_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.gameName.setText(android.get(i).getGame_name());
        Glide.with(activity).load(android.get(i).getGame_image())
                .placeholder(R.drawable.app_icon)
                .into(viewHolder.gameImg);
        Constant.handler = new Handler();
        viewHolder.appCoins.setText(" " + Constant.appRP.getOnline_game_points());
        viewHolder.appCoins.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
        viewHolder.appTimer.setText(" " + Constant.appRP.getOnline_game_timer()+" m");
        viewHolder.appTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stopwatch, 0, 0, 0);

        viewHolder.cardView.setOnClickListener(view -> {
            function.onClickData(i, android.get(i).getGame_name(), type, android.get(i).getGame_image(), android.get(i).getGame_url(), "");
        });

        viewHolder.cardView.setOnTouchListener((@SuppressLint("ClickableViewAccessibility") View view, MotionEvent motionEvent) -> {
            if (!isGameRunning) {
                Constant.handler.removeCallbacks(r);
            } else {
                isGameRunning = true;
            }
            return false;
        });
        if (!Constant.appRP.isLive_mode()) {
            viewHolder.appCoins.setVisibility(View.GONE);
            viewHolder.appTimer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return android.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView gameName;
        private final MaterialTextView appCoins;
        private final MaterialTextView appTimer;
        private final ImageView gameImg;
        private final MaterialCardView cardView;

        public ViewHolder(View view) {
            super(view);
            gameName = view.findViewById(R.id.appName);
            appTimer = view.findViewById(R.id.appTimer);
            appCoins = view.findViewById(R.id.appCoins);
            gameImg = view.findViewById(R.id.userProfile);
            cardView = view.findViewById(R.id.cardview);
        }
    }
}