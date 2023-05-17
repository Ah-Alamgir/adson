package com.ILoveDeshi.Android_Source_Code.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.util.TouchImageView;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Function function = new Function(ViewImage.this);
        function.forceRTLIfSupported();

        String string = getIntent().getStringExtra("path");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_view_image);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        function.changeStatusBarColor();

        TouchImageView imageView = findViewById(R.id.imageView_view_image);

        Glide.with(ViewImage.this).load(string)
                .placeholder(R.drawable.placeholder_landscape)
                .into(imageView);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
