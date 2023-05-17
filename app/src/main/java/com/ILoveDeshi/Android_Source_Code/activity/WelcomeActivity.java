package com.ILoveDeshi.Android_Source_Code.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.google.android.material.textview.MaterialTextView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class WelcomeActivity extends AppCompatActivity {

    private Function function;
    private int[] layouts;
    private ViewPager viewPager;
    private ImageView imageViewNext;
    private MaterialTextView textViewSkip, textViewNext;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        function = new Function(WelcomeActivity.this);
        function.forceRTLIfSupported();

        if (!function.isWelcome()) {
            if (!function.isLanguage()) {
                startActivity(new Intent(WelcomeActivity.this, SplashScreen.class));
                finish();
            } else {
                launchHomeScreen();
            }
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        function.changeStatusBarColor();
        setContentView(R.layout.activity_welcome);
        viewPager = findViewById(R.id.view_pager);
        textViewSkip = findViewById(R.id.textView_skip);
        textViewNext = findViewById(R.id.textView_next);
        imageViewNext = findViewById(R.id.imageView_next);

        textViewNext.setVisibility(View.GONE);
        layouts = new int[]{R.layout.welcome_slide_one, R.layout.welcome_slide_two, R.layout.welcome_slide_three, R.layout.welcome_slide_four};

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        textViewSkip.setOnClickListener(v -> launchHomeScreen());

        textViewNext.setOnClickListener(v -> {
            int current = getItem();
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });

        imageViewNext.setOnClickListener(v -> {
            int current = getItem();
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });

    }

    private int getItem() {
        return viewPager.getCurrentItem() + 1;
    }

    private void launchHomeScreen() {
        function.setFirstWelcome(false);
        startActivity(new Intent(WelcomeActivity.this, SplashScreen.class)
                .putExtra("type", "welcome"));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            if (position == layouts.length - 1) {
                textViewSkip.setVisibility(View.GONE);
                imageViewNext.setVisibility(View.GONE);
                textViewNext.setVisibility(View.VISIBLE);
            } else {
                textViewSkip.setVisibility(View.GONE);
                imageViewNext.setVisibility(View.VISIBLE);
                textViewNext.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}