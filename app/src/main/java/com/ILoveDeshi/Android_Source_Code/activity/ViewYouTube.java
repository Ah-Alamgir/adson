package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class ViewYouTube extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.OnFullscreenListener {

    private Function function;
    private YouTubePlayerView ytPlayer;
    private YouTubePlayer player;
    private Long appTime;
    private CountDownTimer countDownTimer;
    String access = Constant.appRP.getYoutube_api_key();
    private String search, id, time, point;
    private String img = "https://img.youtube.com/vi/ptG3luCO98k/mqdefault.jpg";
    private int orientation = 0;
    private Display display;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_youtube);
        display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        orientation = display.getOrientation();
        search = getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");
        time = getIntent().getStringExtra("time");
        point = getIntent().getStringExtra("point");
        function = new Function(ViewYouTube.this);
        function.forceRTLIfSupported();
        appTime = Long.valueOf(time);
        ytPlayer = (YouTubePlayerView) findViewById(R.id.ytPlayer);
        if (function.isNetworkAvailable()) {
            ytPlayer.initialize(
                    access,
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(
                                YouTubePlayer.Provider provider,
                                YouTubePlayer youTubePlayer, boolean b) {
                            player = youTubePlayer;
                            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                            youTubePlayer.cueVideo(function.okGoogle(search));
                            youTubePlayer.setPlayerStateChangeListener(ViewYouTube.this);
                            //youTubePlayer.play();
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult
                                                                    youTubeInitializationResult) {
                            Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onVideoStarted() {
        player.setOnFullscreenListener(ViewYouTube.this);
        player.setFullscreen(true);
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
        player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

        if (!Constant.appRP.isLive_mode()) {
            countDownTimer = new CountDownTimer(appTime * 60 * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    Log.e("seconds remaining: ", String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    if (!ViewYouTube.this.isFinishing()) {
                        function.secureTheVdo(function.userId(), getString(R.string.youtube_watch), point, id);
                    }
                }
            }.start();
        }
    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    @Override
    public void onFullscreen(boolean b) {
        player.setFullscreen(b);
        player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
    }
}
