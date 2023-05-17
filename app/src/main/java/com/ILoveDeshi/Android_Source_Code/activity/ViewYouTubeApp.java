package com.ILoveDeshi.Android_Source_Code.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.util.NiceCountDownTimer;
import com.google.android.material.card.MaterialCardView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ViewYouTubeApp extends AppCompatActivity implements NiceCountDownTimer.OnCountDownListener {

    private Function function;
    private String search, id, time, point;
    Boolean isVideoView = false;
    public static boolean pointGive;
    NiceCountDownTimer niceCountDownTimer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_yt_app);
        search = getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");
        time = getIntent().getStringExtra("time");
        point = getIntent().getStringExtra("point");
        function = new Function(ViewYouTubeApp.this);
        MaterialCardView btnViewVideo = findViewById(R.id.btnViewVideo);

        if (function.isNetworkAvailable()) {
            btnViewVideo.setOnClickListener(view -> {
                Intent ytIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(search));
                startActivity(ytIntent);
                startTimer(Integer.parseInt(time) * 60);
            });
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void startTimer(int time) {
        niceCountDownTimer = new NiceCountDownTimer(0, time, 0, this);
        niceCountDownTimer.setTimerPattern("s");
        niceCountDownTimer.start(true);
    }

    @Override
    public void onDestroy() {
        //niceCountDownTimer.pause();
        niceCountDownTimer = null;
        super.onDestroy();
    }

    @Override
    public void onCountDownActive(String time) {
        function.showToast(time + " Seconds Remaining");
    }

    @Override
    public void onCountDownFinished() {
        isVideoView = true;
        function.showToast(getString(R.string.task_completed));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVideoView) {
            isVideoView = false;
            function.showToast(getString(R.string.task_completed));
            function.secureTheVdo(function.userId(), getString(R.string.youtube_watch), point, id);
        } else {
            if (!pointGive) {
                if (niceCountDownTimer != null) {
                    niceCountDownTimer.pause();
                    niceCountDownTimer = null;
                    function.showToast(getString(R.string.task_completed_not));
                }
            }
        }

        if (pointGive) {
            pointGive = false;
            function.showToast(getString(R.string.task_completed) + " " + getString(R.string.you_have) + point + getString(R.string.point));
        }
    }
}
