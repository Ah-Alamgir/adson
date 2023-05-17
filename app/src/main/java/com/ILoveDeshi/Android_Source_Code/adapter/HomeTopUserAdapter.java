package com.ILoveDeshi.Android_Source_Code.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.HomeTopUserList;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeTopUserAdapter extends RecyclerView.Adapter<HomeTopUserAdapter.ViewHolder> {

    private final Activity activity;
    private final String type;
    private final List<HomeTopUserList> rewardPointLists;
    private final Function function;

    public HomeTopUserAdapter(Activity activity, List<HomeTopUserList> rewardPointLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.rewardPointLists = rewardPointLists;
        function = new Function(activity, onClick);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_top_user_adapter, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        holder.rootCard.getLayoutParams().width = (int) (displayMetrics.widthPixels / 3.6);
        if(rewardPointLists.get(position).getUser_image() != null){
            if (rewardPointLists.get(position).getUser_image().contains(".jpg") || rewardPointLists.get(position).getUser_image().contains(".png")){
                Glide.with(activity).load(rewardPointLists.get(position).getUser_image())
                        .placeholder(R.drawable.placeholder_landscape).into(holder.imageView);
            }
        }
        holder.textView_title.setText(rewardPointLists.get(position).getName());
        holder.textView_point.setText(rewardPointLists.get(position).getTotal_point()+ " : Pts");
        holder.rootCard.setOnClickListener(v -> function.onClickData(position, rewardPointLists.get(position).getName(), type, rewardPointLists.get(position).getType(), rewardPointLists.get(position).getId(), ""));

    }
    @Override
    public int getItemCount() { return rewardPointLists.size(); }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView imageView;
        private final CardView rootCard;
        private final MaterialTextView textView_title;
        private final MaterialTextView textView_point;
        public ViewHolder(View itemView) {
            super(itemView);
            rootCard = itemView.findViewById(R.id.rootCard);
            imageView = itemView.findViewById(R.id.imageView_reward_point_adapter);
            textView_title = itemView.findViewById(R.id.textView_title_reward_point_adapter);
            textView_point = itemView.findViewById(R.id.textView_point_reward_point_adapter);
        }
    }
}
