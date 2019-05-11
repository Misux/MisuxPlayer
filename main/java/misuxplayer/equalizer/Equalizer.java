package com.example.francesco.masterplayer.equalizer;

import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.R;

import java.util.ArrayList;

public class Equalizer extends AppCompatActivity {

    private static final float VISUALIZER_HEIGHT_DIP = 50f;

    private Visualizer mVisualizer;
    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;
    Switch aSwitch;
    Spinner equalizerPresetSpinner;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equalizer);

        //switch equalizer
        aSwitch = (Switch) findViewById(R.id.idSwitch);

        //set up visualizer and equalizer bars
        setupVisualizerFxAndUI();
        setupEqualizerFxAndUI();

        //SET SWITCH MAIN-EQUALIZER
        if (MainActivity.isEqualizer) {
            aSwitch.setChecked(true);
            mVisualizer.setEnabled(true);

        } else {
            aSwitch.setChecked(false);
            mVisualizer.setEnabled(false);
            for (short equalizerBandIndex = 0; equalizerBandIndex < 5; equalizerBandIndex++) {
                seekBar = (SeekBar) findViewById(equalizerBandIndex);
                seekBar.setEnabled(false);
            }
        }

        //reverb
        reverbSound();

    }

    // shows spinner with list of equalizer presets to choose from - updates the seekBar progress and gain levels according to those of the selected preset
    private void equalizeSound() {

        //SET SPINNER
        ArrayList<String> equalizerPresetNames = new ArrayList<String>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, equalizerPresetNames);
        equalizerPresetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equalizerPresetSpinner = (Spinner) findViewById(R.id.spinner);


        //get list of the device's equalizer presets
        for (short i = 0; i < MainActivity.mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(MainActivity.mEqualizer.getPresetName(i));
        }
        equalizerPresetSpinner.setAdapter(equalizerPresetSpinnerAdapter);
        equalizerPresetSpinner.setSelection(MainActivity.positionEqualizerSpinner);

        //handle the spinner item selections
        equalizerPresetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //first list item selected by default and sets the preset accordingly

                MainActivity.positionEqualizerSpinner = position;

                MainActivity.mEqualizer.usePreset((short) position);
                // get the number of frequency bands for this equalizer engine
                short numberFrequencyBands = MainActivity.mEqualizer.getNumberOfBands();
                //get the lower gain setting for this equalizer band
                final short lowerEqualizerBandLevel = MainActivity.mEqualizer.getBandLevelRange()[0];

                //set seekBar indicators according to selected preset
                for (short equalizerBandIndex = 0; equalizerBandIndex < numberFrequencyBands; equalizerBandIndex++) {

                    SeekBar seekBar = (SeekBar) findViewById(equalizerBandIndex);
                    // get current gain setting for this equalizer band
                    // set the progress indicator of this seekBar to indicate the current gain value
                    seekBar.setProgress(MainActivity.mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);


                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // not used
            }
        });
    }

    // displays the SeekBar sliders for the supported equalizer frequency bands user can move sliders to change the frequency of the bands
    private void setupEqualizerFxAndUI() {

        // get reference to linear layout for the seekBars
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutEqual);

        //get number frequency bands supported by the equalizer engine
        short numberFrequencyBands = MainActivity.mEqualizer.getNumberOfBands();

        //get the level ranges to be used in setting the band level in milliBels
        final short lowerEqualizerBandLevel = MainActivity.mEqualizer.getBandLevelRange()[0];
        final short upperEqualizerBandLevel = MainActivity.mEqualizer.getBandLevelRange()[1];

        final String hz = " Hz";
        final String db = " dB";
        final String plus = "+";

        //loop through all the equalizer bands to display the band headings, lower/upper levels and the seek bars
        for (short i = 0; i < numberFrequencyBands; i++) {
            final short equalizerBandIndex = i;

            //FREQ HEADER SEEKBAR
            TextView frequencyHeaderTextview = new TextView(this);
            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);

            String band = Integer.toString(MainActivity.mEqualizer.getCenterFreq(equalizerBandIndex) / 1000);
            frequencyHeaderTextview.setText(band.concat(hz));
            mLinearLayout.addView(frequencyHeaderTextview);


            //LEVEL DB
            TextView lowerqualizerBandLevelDB = (TextView) findViewById(R.id.lowerqualizerBandLevelDB);
            TextView upperqualizerBandLevelDB = (TextView) findViewById(R.id.upperqualizerBandLevelDB);

            String lower = Integer.toString(lowerEqualizerBandLevel / 100);
            lowerqualizerBandLevelDB.setText(lower.concat(db));

            String upper = plus.concat(Integer.toString((upperEqualizerBandLevel / 100)));
            upperqualizerBandLevelDB.setText(upper.concat(db));


            //set up linear layout to contain each seekBar
            LinearLayout seekBarRowLayout = new LinearLayout(this);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);


            //  **********  SEEKBAR **************

            //set the layout parameters for the seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;

            //create a new seekBar
            seekBar = new SeekBar(this);
            //give the seekBar an ID
            seekBar.setId(i);
            //seekBar.setEnabled(false);

            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
            //set the progress for this seekBar
            seekBar.setProgress(MainActivity.mEqualizer.getBandLevel(equalizerBandIndex));

            //change progress as its changed by moving the sliders
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    MainActivity.mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progress + lowerEqualizerBandLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    //not used
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    //not used
                }
            });

            //add the lower and upper band level textviews and the seekBar to the row layout
            seekBarRowLayout.addView(seekBar);

            mLinearLayout.addView(seekBarRowLayout);

            //show the spinner
            equalizeSound();
        }
    }

    // DISPLAY AUDIO WAVEFORM
    private void setupVisualizerFxAndUI() {

        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutVisual);
        // Create a VisualizerView to display the audio waveform for the current settings
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(MainActivity.mediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }


    // SWITCH SPINNER
    public void onSwitchClick (View v) {
        if(aSwitch.isChecked()){
            Toast.makeText(this,"Equalizzatore ON", Toast.LENGTH_SHORT).show();
            aSwitch.setChecked(true);
            mVisualizer.setEnabled(true);
            MainActivity.mEqualizer.setEnabled(true);
            MainActivity.pReverb.setEnabled(true);
            MainActivity.isEqualizer=true;

            for (short equalizerBandIndex = 0; equalizerBandIndex < 5; equalizerBandIndex++) {
                seekBar = (SeekBar) findViewById(equalizerBandIndex);
                seekBar.setEnabled(true);
            }


        } else {
            Toast.makeText(this,"Equalizzatore OFF", Toast.LENGTH_SHORT).show();
            aSwitch.setChecked(false);
            mVisualizer.setEnabled(false);
            MainActivity.mEqualizer.setEnabled(false);
            MainActivity.pReverb.setEnabled(true);
            MainActivity.pReverb.setEnabled(false);
            MainActivity.isEqualizer=false;

            for (short equalizerBandIndex = 0; equalizerBandIndex < 5; equalizerBandIndex++) {
                seekBar = (SeekBar) findViewById(equalizerBandIndex);
                seekBar.setEnabled(false);
            }
        }
    }

    //RIVERBERO
    private void reverbSound() {

        // set up the spinner
        ArrayList<String> reverbPresetNames = new ArrayList<String>();
        ArrayAdapter<String> reverbPresetSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reverbPresetNames);
        reverbPresetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner reverbPresetSpinner = (Spinner) findViewById(R.id.spinnerReverb);

        reverbPresetNames.add("Nessuno");
        reverbPresetNames.add("Stanza Piccola");
        reverbPresetNames.add("Stanza Media");
        reverbPresetNames.add("Stanza Grande");
        reverbPresetNames.add("Piatto");
        reverbPresetNames.add("Sala Media");
        reverbPresetNames.add("Sala Grande");


        reverbPresetSpinner.setAdapter(reverbPresetSpinnerAdapter);
        reverbPresetSpinner.setSelection(MainActivity.positionReverbSpinner);

        //creazione
        MainActivity.mediaPlayer.attachAuxEffect(MainActivity.pReverb.getId());

        //handle the spinner item selections
        reverbPresetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                MainActivity.positionReverbSpinner = position;

                switch (position) {
                    case 1:
                        MainActivity.pReverb.setPreset(PresetReverb.PRESET_NONE);
                        break;
                    case 2:
                        MainActivity.pReverb.setPreset(PresetReverb.PRESET_SMALLROOM);
                        break;
                    case 3:
                        MainActivity.pReverb.setPreset(PresetReverb.PRESET_MEDIUMROOM);
                        break;
                    case 4:
                        MainActivity.pReverb.setPreset(PresetReverb.PRESET_LARGEROOM);
                        break;
                    case 5:
                        MainActivity.pReverb.setPreset(PresetReverb.PRESET_PLATE);
                        break;
                    case 6:
                        MainActivity.pReverb.setPreset(PresetReverb.PRESET_MEDIUMHALL);
                        break;
                    case 7:
                        MainActivity.pReverb.setPreset(PresetReverb.PRESET_LARGEHALL);
                        break;
                }
                MainActivity.mediaPlayer.setAuxEffectSendLevel(1.0f);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // not used
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing() && MainActivity.mediaPlayer != null) {
            mVisualizer.release();
        }
    }
}
