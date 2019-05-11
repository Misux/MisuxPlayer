package com.example.francesco.masterplayer.alteration;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.R;

public class Alteration extends AppCompatActivity {

    AudioManager audioManager = null;
    SeekBar volumeSeekbar = null, pitch = null, speed = null;
    TextView value_volume, value_pitch, value_speed;
    MediaPlayer mp;
    PlaybackParams params = new PlaybackParams();

    CardView btnA, btnB;
    ImageView clear_loop, clear_pitch, clear_speed, clear_volume;
    public static Boolean isLoop = false;
    public static int currentPositionA=-1, currentPositionB=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alteration_sound);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        mp = MainActivity.mediaPlayer;
        volumeSeekbar = (SeekBar) findViewById(R.id.seekBar_volume);
        value_volume = (TextView) findViewById(R.id.perc_volume);
        clear_volume = (ImageView) findViewById(R.id.clear_volume);

        pitch = (SeekBar) findViewById(R.id.seekBar_pitch);
        value_pitch = (TextView) findViewById(R.id.val_pitch);
        clear_pitch = (ImageView) findViewById(R.id.clear_pitch);


        speed = (SeekBar) findViewById(R.id.seekBar_speed);
        value_speed = (TextView) findViewById(R.id.perc_speed);
        clear_speed = (ImageView) findViewById(R.id.clear_speed);

        btnA = (CardView) findViewById(R.id.btnA);
        btnB = (CardView) findViewById(R.id.btnB);
        clear_loop = (ImageView) findViewById(R.id.clear_loop);

        if(mp.isPlaying()) {

            volumeSeekbar.setEnabled(true);
            pitch.setEnabled(true);
            speed.setEnabled(true);

            //VOLUME
            try {

                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                volumeSeekbar.setMax(maxVolume);
                volumeSeekbar.setProgress(currentVolume);

                //silent mode volume
                if(currentVolume==0) clear_volume.setImageDrawable(ContextCompat.getDrawable(Alteration.this, R.drawable.ic_volume_off_black));
                else clear_volume.setImageDrawable(ContextCompat.getDrawable(Alteration.this, R.drawable.ic_volume_up_black));

                //first lunch progress volume %
                int perc = (100*currentVolume)/15;
                String x = " " + Integer.toString(perc) + "%";
                value_volume.setText(x);


                volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                        //progress volume %
                        int perc = (100*progress)/15;
                        String x = " " + Integer.toString(perc) + "%";
                        value_volume.setText(x);

                        //silent mode volume
                        if(progress==0) clear_volume.setImageDrawable(ContextCompat.getDrawable(Alteration.this, R.drawable.ic_volume_off_black));
                        else clear_volume.setImageDrawable(ContextCompat.getDrawable(Alteration.this, R.drawable.ic_volume_up_black));

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            //silent mode volume
            clear_volume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if(currentVolume==0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7, 0);
                        volumeSeekbar.setProgress(7);
                        clear_volume.setImageDrawable(ContextCompat.getDrawable(Alteration.this, R.drawable.ic_volume_up_black));
                    } else {
                        audioManager.setStreamVolume(AudioManager.RINGER_MODE_SILENT, 0, 0);
                        volumeSeekbar.setProgress(0);
                        clear_volume.setImageDrawable(ContextCompat.getDrawable(Alteration.this, R.drawable.ic_volume_off_black));
                    }

                }
            });


            //PITCH
            pitch.setMax(24);

            //valore progressive bar dinamico in base al valore del pitch del brano
            float i = mp.getPlaybackParams().getPitch();
            final double a = Math.pow(2,1f/12);
            float j = (float) (Math.log(i)/ Math.log(a));
            int g = (int) j;
            pitch.setProgress(g+12);

            final String semitoni =" semitoni";
            final String s = Integer.toString(g).concat(semitoni);
            final String plus = " +";
            final String space = " ";
            if (g > 0) value_pitch.setText(plus.concat(s));
            else value_pitch.setText(space.concat(s));

            //alterazione pitch
            pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //imposta pitch
                    float myfloat = (float) progress; // cast in float

                    params.setPitch((float) Math.pow(a, myfloat-12));  //-12 a 12
                    mp.setPlaybackParams(params);

                    String x = Float.toString(myfloat-12).concat(semitoni);
                    if(myfloat-12>0) value_pitch.setText(plus.concat(x));
                    else value_pitch.setText(space.concat(x));

                    if(myfloat-12==0) value_pitch.setText(space.concat(s));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            //clear pitch
            clear_pitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pitch.setProgress(12);
                    String s2 = " 0";
                    value_pitch.setText(s2.concat(semitoni));
                    params.setPitch(1);  //0 a 2
                    mp.setPlaybackParams(params);
                }
            });


            //SPEED

            //first lunch progress volume
            speed.setMax(10);

            //valore progressive bar dinamico in base al valore del tempo del brano
            float s2 = mp.getPlaybackParams().getSpeed();
            double s1 = (double) s2;
            double t = Math.round((s1 - 0.5)*10);
            int t2 = (int) t;
            speed.setProgress(t2);
            Log.v("val: ", Double.toString(t));

            final String perc = "%";
            final String l = Integer.toString((t2*100/10) + 50).concat(perc);
            value_speed.setText(space.concat(l));

            speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    double y = (double) progress/10;
                    double s = y + 0.5;
                    float s2 = (float) s;
                    mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(s2)); //da 0.5 a 1.5

                    String k = Integer.toString((progress*100/10) + 50).concat(perc);
                    value_speed.setText(space.concat(k));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            //clear speed
            clear_speed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    speed.setProgress(5);
                    String m = " 100";
                    value_speed.setText(m.concat(perc));
                    mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(1)); //da 0.5 a 1.5
                }
            });


            //LOOP

            if(isLoop){
                btnA.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                btnB.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            } else {
                btnA.setCardBackgroundColor(getResources().getColor(R.color.greyLight));
                btnB.setCardBackgroundColor(getResources().getColor(R.color.greyLight));
            }

            btnA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isLoop) {
                        btnA.setEnabled(false);
                    } else {
                        btnA.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                        btnA.setEnabled(false);
                        mp.setLooping(true);

                        currentPositionA = mp.getCurrentPosition();
                        Log.v("posizione", Integer.toString(currentPositionA));
                    }
                }
            });


            btnB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isLoop) {
                        btnB.setEnabled(false);
                    } else {
                        btnB.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                        btnB.setEnabled(false);

                        //first click
                        currentPositionB = mp.getCurrentPosition();
                        mp.seekTo(currentPositionA); //ricomincia da A
                        isLoop = true;
                    }

                }
            });

            clear_loop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnA.setCardBackgroundColor(getResources().getColor(R.color.greyLight));
                    btnB.setCardBackgroundColor(getResources().getColor(R.color.greyLight));
                    mp.setLooping(false);
                    btnA.setEnabled(true);
                    btnB.setEnabled(true);
                    currentPositionA=-1;
                    currentPositionB=-1;
                    isLoop=false;
                }
            });

        } else {
            volumeSeekbar.setEnabled(false);
            pitch.setEnabled(false);
            speed.setEnabled(false);
        }
    }
}
