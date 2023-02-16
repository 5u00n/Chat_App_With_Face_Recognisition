package com.group15.facechatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.group15.facechatapp.Login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView) findViewById(R.id.splash_video);
        videoView.setZOrderOnTop(true);

        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer m) {
                try {
                    if (m.isPlaying()) {
                        m.stop();
                        m.release();
                        m = new MediaPlayer();
                    }
                    m.setVolume(0f, 0f);
                    m.setLooping(false);
                    m.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                startNextActivity();
            }
        });
        videoView.start();


    }
    private void startNextActivity() {
        if (isFinishing())
            return;
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoView.start();

    }
}