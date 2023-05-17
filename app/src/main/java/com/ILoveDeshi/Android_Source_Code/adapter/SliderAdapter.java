package com.ILoveDeshi.Android_Source_Code.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ILoveDeshi.Android_Source_Code.item.SubCategoryList;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private final Function function;
    private final Activity activity;
    private final String type;
    private final List<SubCategoryList> subCategoryLists;

    public SliderAdapter(Activity activity, String type, List<SubCategoryList> subCategoryLists, OnClick onClick) {
        this.activity = activity;
        this.subCategoryLists = subCategoryLists;
        this.type = type;
        function = new Function(activity, onClick);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View view;
        view = activity.getLayoutInflater().inflate(R.layout.slider_adapter, container, false);
        ImageView imageView = view.findViewById(R.id.imageView_slider_adapter);
        MaterialCardView cardView = view.findViewById(R.id.cardView_slider_adapter);

        Glide.with(activity).load(subCategoryLists.get(position).getExternal_image())
                .placeholder(R.drawable.placeholder_landscape).into(imageView);

        cardView.setOnClickListener(v -> {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(subCategoryLists.get(position).getExternal_url())));
        });
        container.addView(view, 0);
        return view;

    }

    @Override
    public int getCount() {
        return subCategoryLists.size();
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

