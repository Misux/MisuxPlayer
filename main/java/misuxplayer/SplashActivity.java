package com.example.francesco.masterplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    TextView tv;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv = (TextView) findViewById(R.id.spash_name);
        iv = (ImageView) findViewById(R.id.spash_app);

        //animation
        Animation animation = AnimationUtils.loadAnimation
                (getBaseContext(), R.anim.splash);
        tv.startAnimation(animation);
        iv.startAnimation(animation);

        //rotate animation
        Animation rotation = AnimationUtils.loadAnimation
                (getBaseContext(), R.anim.rotate);
        iv.startAnimation(rotation);

        final Intent i = new Intent(this, MainActivity.class);
        Thread timer = new Thread(){
            public void run() {
                try {
                    sleep(800);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}




