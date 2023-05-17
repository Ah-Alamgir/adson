package com.ILoveDeshi.Android_Source_Code.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.fragment.SubscriptionFragment;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.item.PackageList;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class PackageAdapter extends PagerAdapter {

    private final Function function;
    private final Activity activity;
    private final String type;
    private final List<PackageList> packageLists;

    public PackageAdapter(Activity activity, String type, List<PackageList> subCategoryLists, OnClick onClick) {
        this.activity = activity;
        this.packageLists = subCategoryLists;
        this.type = type;
        function = new Function(activity, onClick);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View view;
        view = activity.getLayoutInflater().inflate(R.layout.package_list, container, false);
        ImageView topBack = view.findViewById(R.id.topBack);
        MaterialTextView packName = view.findViewById(R.id.packName);
        MaterialTextView packDesc = view.findViewById(R.id.packDesc);
        MaterialTextView tvAd = view.findViewById(R.id.tvAd);
        MaterialTextView tvPrice = view.findViewById(R.id.tvPrice);
        MaterialCardView buyCard = view.findViewById(R.id.buyCard);

        Glide.with(activity).load(packageLists.get(position).getImage())
                .placeholder(R.drawable.placeholder_landscape).into(topBack);

        packName.setText(packageLists.get(position).getName());
        packDesc.setText(packageLists.get(position).getDescription());
        tvAd.setText(activity.getString(R.string.task_available) + " " + packageLists.get(position).getTask());
        String pay = packageLists.get(position).getPrice();
        if (!pay.equals("0")) {
            tvPrice.setText(activity.getString(R.string.buy_at) + " " + pay + " " + packageLists.get(position).getCurcy());
        } else {
            tvPrice.setText(activity.getString(R.string.free));
        }
        String userId = packageLists.get(position).getUid();
        String packId = packageLists.get(position).getPid();
        if (userId.equals(function.userId()) && packId.equals(packageLists.get(position).getId())) {
            tvPrice.setText(activity.getString(R.string.subscribed));
        }
        buyCard.setOnClickListener(v -> {
            if (userId.equals(function.userId()) && packId.equals(packageLists.get(position).getId())) {
//                activity.startActivity(new Intent(activity, PlayAd.class)
//                        .putExtra("type", packageLists.get(position).getName() + " " + activity.getString(R.string.ads_watched))
//                        .putExtra("pid", packageLists.get(position).getId()));
                function.onClickData(position, packageLists.get(position).getId(), "", "", "", "");
            } else {
                if (!tvPrice.getText().toString().equals(activity.getString(R.string.free))) {
                    SubscriptionFragment.sendReviewInfo(packageLists.get(position).getImage(), packageLists.get(position).getName(), packageLists.get(position).getId(), function.userId());
                } else {
//                    activity.startActivity(new Intent(activity, PlayAd.class)
//                            .putExtra("type", packageLists.get(position).getName() + " " + activity.getString(R.string.ads_watched))
//                            .putExtra("pid", packageLists.get(position).getId()));
                    function.onClickData(position, packageLists.get(position).getId(), "", "", "", "");
                }
            }
        });
        container.addView(view, 0);
        return view;
    }

    @Override
    public int getCount() {
        return packageLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}

