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
import com.ILoveDeshi.Android_Source_Code.item.ProductList;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private final Activity activity;
    private final List<ProductList> productLists;
    private final Function function;

    public ProductsAdapter(Activity activity, List<ProductList> homeWallpaperLists, String type, OnClick onClick) {
        this.activity = activity;
        this.productLists = homeWallpaperLists;
        function = new Function(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.product_item_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Glide.with(activity).load(productLists.get(position).getImage())
                .placeholder(R.drawable.placeholder_portable).into(holder.imageView);
        holder.rootCard.setOnClickListener(v -> function.onClickData(position, productLists.get(position).getTitle(), "", "", productLists.get(position).getVisit_utl(), ""));
        holder.textView.setText(productLists.get(position).getTitle());
        holder.price.setText(productLists.get(position).getPrice());
        holder.Dprice.setText(productLists.get(position).getDiscount_price());
    }

    @Override
    public int getItemCount() {
        return productLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final MaterialCardView rootCard;
        private final MaterialTextView textView, price, Dprice;

        public ViewHolder(View itemView) {
            super(itemView);
            rootCard = itemView.findViewById(R.id.cardview);
            imageView = itemView.findViewById(R.id.itemImage);
            textView = itemView.findViewById(R.id.appName);
            price = itemView.findViewById(R.id.price);
            Dprice = itemView.findViewById(R.id.Dprice);
        }
    }
}
