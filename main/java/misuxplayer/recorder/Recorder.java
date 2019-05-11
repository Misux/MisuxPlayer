package com.example.francesco.masterplayer.recorder;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.media.MediaRecorder;

import android.os.Environment;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.R;


public class Recorder extends AppCompatActivity {

    public static final int RequestPermissionCode = 1;
    private File mOutputFile; //save file
    ImageView buttonStop, buttonSave;
    MediaRecorder mediaRecorder;
    CardView card_rec, buttonStart;
    CheckBox checkBoxMusic;
    Chronometer chronometer;
    Animation anim;
    Boolean running = false;
    private long timeWhenStopped = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder);

        checkBoxMusic = (CheckBox) findViewById(R.id.checkBoxMusic);
        card_rec = (CardView) findViewById(R.id.card_rec);
        buttonStart = (CardView) findViewById(R.id.radius_rec);
        buttonStop = (ImageView) findViewById(R.id.button2);
        buttonSave = (ImageView) findViewById(R.id.save_rec);
        chronometer = (Chronometer)findViewById(R.id.chronometer1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //animazione text allo stop
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);

        buttonStop.setEnabled(false);
        buttonSave.setEnabled(false);

        if(!checkPermission()) {
            requestPermission();
        }

            if (MainActivity.mediaPlayer.isPlaying()) checkBoxMusic.setChecked(true);

            buttonStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MediaRecorderReady();
                    running = true;
                    Log.d("Voice Recorder", "output: " + getOutputFile());

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Log.d("Voice Recorder", "started recording to " + mOutputFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Voice Recorder", "prepare() failed " + e.getMessage());
                    }

                    buttonStart.setEnabled(false);
                    card_rec.setCardBackgroundColor(getResources().getColor(R.color.greyLight));
                    buttonStop.setEnabled(true);
                    buttonStop.setImageDrawable(ContextCompat.getDrawable(Recorder.this, R.drawable.ic_pause_circle_outline_black));
                    buttonSave.setEnabled(true);

                    chronometer.clearAnimation();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    timeWhenStopped = 0;
                    chronometer.start();
                    //Toast.makeText(Recorder.this, "Registrazione ON", Toast.LENGTH_LONG).show();

                }
            });

            buttonStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (running) {
                        //pause rec
                        running = false;
                        mediaRecorder.pause();
                        Log.v("PauseDOP", "thread");

                        buttonStop.setImageDrawable(ContextCompat.getDrawable(Recorder.this, R.drawable.ic_play_circle_outline_black));
                        buttonStart.setEnabled(true);
                        card_rec.setCardBackgroundColor(getResources().getColor(R.color.greyDark));

                        chronometer.stop();
                        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime(); //conta il tempo di stop del cronomotro

                        //text lampeggiante
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        chronometer.startAnimation(anim);

                    } else {
                        //resume rec
                        mediaRecorder.resume();
                        running = true;
                        chronometer.setBase(SystemClock.elapsedRealtime()+timeWhenStopped); //sottrae al cronometro il tempo di stop
                        chronometer.start();
                        chronometer.clearAnimation();

                        buttonStart.setEnabled(false);
                        card_rec.setCardBackgroundColor(getResources().getColor(R.color.greyLight));
                        buttonStop.setImageDrawable(ContextCompat.getDrawable(Recorder.this, R.drawable.ic_pause_circle_outline_black));
                    }
                }
            });

            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    chronometer.stop();

                    buttonStart.setEnabled(true);
                    buttonStop.setEnabled(false);
                    card_rec.setCardBackgroundColor(getResources().getColor(R.color.greyDark));
                    running = false;

                    Uri uri = Uri.parse("file://" + mOutputFile.getAbsolutePath());
                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanIntent.setData(uri);
                    sendBroadcast(scanIntent);
                    setResult(Activity.RESULT_OK, new Intent().setData(uri));

                    //refresh 
                    MainActivity.refresh = true;

                    Toast.makeText(Recorder.this, "Registrazione salvata nella libreria!", Toast.LENGTH_LONG).show();
                }
            });

    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mOutputFile = getOutputFile();
        mediaRecorder.setOutputFile(mOutputFile.getAbsolutePath());
    }

    private File getOutputFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss_ddMMyyyy", Locale.ITALIAN);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/REC_"
                + dateFormat.format(new Date())
                + ".m4a");
    }

    //controllo music
    public void ClickMe(View v){
        if(!checkBoxMusic.isChecked()){
            MainActivity.mediaPlayer.pause();
        } else {
            MainActivity.mediaPlayer.start();
        }
    }

    @Override
    public void onBackPressed() {
        try{
            super.onBackPressed();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    //PERMESSI
    private void requestPermission() {
        ActivityCompat.requestPermissions(Recorder.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(Recorder.this, "Permessi Concessi", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Recorder.this, "Permessi Negati", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

}


