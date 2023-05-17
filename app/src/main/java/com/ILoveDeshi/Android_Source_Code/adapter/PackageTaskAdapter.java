package com.ILoveDeshi.Android_Source_Code.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.PackageTaskList;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class PackageTaskAdapter extends RecyclerView.Adapter<PackageTaskAdapter.ViewHolder> {

    private final Activity activity;
    private String type;
    private final List<PackageTaskList> packageTaskLists;
    private Function function;

    public PackageTaskAdapter(Activity activity, List<PackageTaskList> homeWallpaperLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.packageTaskLists = homeWallpaperLists;
        function = new Function(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.package_task_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        holder.taskName.setText(packageTaskLists.get(position).getName());
        holder.tvAds.setText(packageTaskLists.get(position).getAds() + "\n" + activity.getString(R.string.ads));
        String vpn = packageTaskLists.get(position).getVpn();
        holder.cardview.setOnClickListener(view -> {
            if (vpn.equals("1")) {
                if (function.isVPN(activity)) {
                    function.onClickData(position, packageTaskLists.get(position).getPack_name(), packageTaskLists.get(position).getName(), "", packageTaskLists.get(position).getId(), "");
                } else {
                    function.showToast(activity.getString(R.string.vpn_on));
                }
            } else {
                if (!function.isVPN(activity)) {
                    function.onClickData(position, packageTaskLists.get(position).getPack_name(), packageTaskLists.get(position).getName(), "", packageTaskLists.get(position).getId(), "");
                } else {
                    function.showToast(activity.getString(R.string.vpn_off));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageTaskLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardview;
        private final MaterialTextView taskName, tvAds, tvStart;

        public ViewHolder(View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardview);
            taskName = itemView.findViewById(R.id.taskName);
            tvAds = itemView.findViewById(R.id.tvAds);
            tvStart = itemView.findViewById(R.id.tvStart);
        }
    }
}
