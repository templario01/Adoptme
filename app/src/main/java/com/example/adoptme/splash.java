package com.example.adoptme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class splash extends AppCompatActivity {
    private final int DURACION_SPLASH = 2000;
    //agregamos sonido
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mp = MediaPlayer.create(this,R.raw.ladridos);
        new Handler().postDelayed(new Runnable(){
            public void run(){
                //antes de cmbiar de antivity sonara 2 ladridos
                mp.start();
                Intent intent = new Intent(splash.this, login.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);
    }
}
