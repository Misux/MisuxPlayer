package com.example.francesco.masterplayer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.francesco.masterplayer.activity.*;
import com.example.francesco.masterplayer.adapter.SectionsPagerAdapter;
import com.example.francesco.masterplayer.adapter.SongAdapter;
import com.example.francesco.masterplayer.alteration.Alteration;
import com.example.francesco.masterplayer.equalizer.Equalizer;
import com.example.francesco.masterplayer.fragment.AlbumFragment;
import com.example.francesco.masterplayer.fragment.AlbumSongFragment;
import com.example.francesco.masterplayer.fragment.ArtistFragment;
import com.example.francesco.masterplayer.fragment.ArtistSongFragment;
import com.example.francesco.masterplayer.fragment.PlaylistSongFragment;
import com.example.francesco.masterplayer.fragment.SongFragment;
import com.example.francesco.masterplayer.fragment.PlaylistFragment;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.recorder.Recorder;
import com.example.francesco.masterplayer.utility.Utility;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SlidingUpPanelLayout sliding_layout;
    public DrawerLayout mydrawer;
    public Toolbar toolbar;
    public ActionBarDrawerToggle toggle;
    public NavigationView navigationView;
    public SectionsPagerAdapter adapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private MusicLibrary musicLibrary;
    private RelativeLayout music_toolbar;
    private TextView tb_title, tb_title2, tb_artist, tb_artist2, tv_timeStart, tv_timeEnd;
    private ImageView cover_sliding, iv_play, iv_play2, iv_next, iv_previous, iv_random, iv_queue;
    private ImageView expand, optionSong, iv_pause_hide, addPL, iv_favorite;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    public static MediaPlayer mediaPlayer;
    public static android.media.audiofx.Equalizer mEqualizer;
    public static android.media.audiofx.PresetReverb pReverb;

    public static Boolean isEqualizer=false;
    public static int positionEqualizerSpinner=0;
    public static int positionReverbSpinner=0;


    public boolean firstLaunch = true, flag_queue = false, flag_star = false, folder = false;
    private int currentIndex;
    private long currentSongLength;
    private SongAdapter mAdapter;
    public Context context;
    public static int order = 6;
    public Bundle bundle = new Bundle();
    public static ArrayList<Song> starSong = new ArrayList<Song>();
    private static final String TAG = "MyActivity";
    ImageView iv_play_active, iv_equalizer, iv_according, iv_alteration;
    TextView tv_time;
    MenuItem item;
    NotificationCompat.Builder mBuilder;
    NotificationManager notificationManager;
    public static Boolean refresh = false;


    //fragment
    public static SongFragment bf;
    public static AlbumFragment alf;
    public static ArtistFragment arf;
    public static PlaylistFragment pf;
    public static AlbumSongFragment alsf;
    public static ArtistSongFragment arsf;
    public static PlaylistSongFragment psf;
    public static MainActivity instance;


    public static final int MULTIPLE_PERMISSIONS = 4;


    private int[] tabIcons = {
            R.drawable.ic_music_note,
            R.drawable.ic_album,
            R.drawable.ic_person,
            R.drawable.ic_playlist_play
    };

    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermissions()) {
            initializeViews();
            context = this.getApplicationContext();
            musicLibrary = new MusicLibrary(context, this);
            instance = this;
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initializeViews();
                    context = this.getApplicationContext();
                    musicLibrary = new MusicLibrary(context, this);
                    refreshFragment();
                    Toast.makeText(this, "Permessi Concessi", Toast.LENGTH_LONG).show();
                } else {
                    finish();
                    Toast.makeText(this, "Permessi Negati", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //INIT VAR
    private void initializeViews() {

        //Menu Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarMenu);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Drawer
        mydrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mydrawer, toolbar, R.string.open, R.string.close);
        mydrawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_library);

        //ViewPager
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        //Tabs
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setupTabIcons();

        //inizializzo mediaplayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mEqualizer = new android.media.audiofx.Equalizer(0, MainActivity.mediaPlayer.getAudioSessionId());
        pReverb = new PresetReverb(1, 0);

        //SlidingUpPannel
        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                //visibility if collapsed or dragging
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    iv_play.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    expand.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_expand));
                    music_toolbar.setBackgroundColor((Color.parseColor("#1248c6")));
                    tb_title.setTextColor(ContextCompat.getColor(context, R.color.white));
                    tb_artist.setTextColor(ContextCompat.getColor(context, R.color.white));
                    optionSong.setVisibility(View.INVISIBLE);
                    addPL.setVisibility(View.INVISIBLE);
                    iv_favorite.setVisibility(View.INVISIBLE);
                } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    iv_play.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    expand.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_expand_more));
                    music_toolbar.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                    tb_title.setTextColor(ContextCompat.getColor(context, R.color.transparent));
                    tb_artist.setTextColor(ContextCompat.getColor(context, R.color.transparent));
                    optionSong.setVisibility(View.VISIBLE);
                    addPL.setVisibility(View.VISIBLE);
                    iv_favorite.setVisibility(View.VISIBLE);
                }
            }
        });

        sliding_layout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        //init adapter
        mAdapter = new SongAdapter(this, null, null);

        //init info songs
        music_toolbar = (RelativeLayout) findViewById(R.id.music_toolbar);
        cover_sliding = (ImageView) findViewById(R.id.imgCover);
        tb_title = (TextView) findViewById(R.id.tb_title);
        tb_title2 = (TextView) findViewById(R.id.tb_title2);
        tb_artist = (TextView) findViewById(R.id.tb_artist);
        tb_artist2 = (TextView) findViewById(R.id.tb_artist2);
        tb_title.setSelected(true);
        tb_artist.setSelected(true);
        tv_timeStart = (TextView) findViewById(R.id.tv_timeStart);
        tv_timeEnd = (TextView) findViewById(R.id.tv_timeEnd);
        tv_time = (TextView) findViewById(R.id.tv_time);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_play2 = (ImageView) findViewById(R.id.iv_play2);
        iv_previous = (ImageView) findViewById(R.id.iv_previous);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        iv_random = (ImageView) findViewById(R.id.iv_random);
        iv_play_active = (ImageView) findViewById(R.id.iv_play_active);
        expand = (ImageView) findViewById(R.id.iv_expande);
        optionSong = (ImageView) findViewById(R.id.optionSong);
        addPL = (ImageView) findViewById(R.id.addPL);
        iv_pause_hide = (ImageView) findViewById(R.id.iv_pause_hide);
        iv_favorite = (ImageView) findViewById(R.id.iv_favorite);
        iv_queue = (ImageView) findViewById(R.id.iv_queue);

        //musician toolbar
        iv_equalizer = (ImageView) findViewById(R.id.iv_equalizer);
        iv_equalizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Equalizer.class));
            }
        });

        iv_according = (ImageView) findViewById(R.id.iv_according);
        iv_according.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Recorder.class));
            }
        });

        iv_alteration = (ImageView) findViewById(R.id.iv_alteration);
        iv_alteration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Alteration.class));
            }
        });

        //visibility toolbar start app
        if (firstLaunch) { //if true
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN); //view expand
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
            lp.bottomMargin = 0;
        }

    }

    //icone tab
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    //creazione fragment
    private void setupViewPager(ViewPager viewPager) {
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //fragment brani
        bf = new SongFragment();
        bf.parent = this;
        adapter.addFragment(bf, "Brani");

        //fragment album
        alf = new AlbumFragment();
        alf.parent = this;
        adapter.addFragment(alf, "Album");

        //fragment artist
        arf = new ArtistFragment();
        arf.parent = this;
        adapter.addFragment(arf, "Artisti");

        //fragment playlist
        pf = new PlaylistFragment();
        pf.parent = this;
        adapter.addFragment(pf, "Playlist");

        //fragment album song
        alsf = new AlbumSongFragment();
        alsf.parent = this;

        //fragment album artist
        arsf = new ArtistSongFragment();
        arsf.parent = this;

        //fragment playlist song
        psf = new PlaylistSongFragment();
        psf.parent = this;

        //VIEWPAGER
        mViewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }

    //ATTIVA IL MENU ACTIVITY
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_options, menu);

        //slidingUpPannel
        item = menu.findItem(R.id.action_toggle);
        if (sliding_layout != null) {
            if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                item.setTitle(R.string.action_hide_first);
            } else {
                item.setTitle(R.string.action_hide);
            }
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search: {
                Search();
                return true;
            }
            case R.id.ripRand: {
                //se ci sono brani abilita il random song
                if (!MusicLibrary.getSongList()) randomSong();
                return true;
            }
            case R.id.order_album: {
                order = 1;
                refreshFragment();
                item.setChecked(true);
                return true;
            }
            case R.id.order_title: {
                order = 2;
                refreshFragment();
                item.setChecked(true);
                return true;
            }
            case R.id.order_artist: {
                order = 3;
                refreshFragment();
                item.setChecked(true);
                return true;
            }
            case R.id.order_duration: {
                order = 4;
                refreshFragment();
                item.setChecked(true);
                return true;
            }
            case R.id.order_year: {
                order = 5;
                refreshFragment();
                item.setChecked(true);
                return true;
            }
            case R.id.order_track: {
                order = 6;
                refreshFragment();
                item.setChecked(true);
                return true;
            }
            case R.id.newPlaylist: {
                newPlaylist();
                return true;
            }
            case R.id.action_toggle: {
                if (!firstLaunch) {
                    if (sliding_layout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
                        lp.bottomMargin = 0;
                    } else {
                        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
                        lp.bottomMargin = 196;

                    }
                } else {
                    item.setTitle(R.string.action_hide_first);
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Search() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void refreshFragment() {
        //pulisco array
        MusicLibrary.songList.clear();
        MusicLibrary.albumList.clear();
        MusicLibrary.artistList.clear();
        MusicLibrary.playList.clear();
        //ottengo brani
        MusicLibrary.getSongList();
        MusicLibrary.getAlbumsLists();
        MusicLibrary.getArtistsLists();
        MusicLibrary.getPlaylists();
        //transaction
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.detach(bf);
        tr.remove(alsf);
        tr.detach(alf);
        tr.remove(arsf);
        tr.detach(arf);
        tr.remove(psf);
        tr.detach(pf);
        tr.attach(bf);
        tr.attach(alf);
        tr.attach(arf);
        tr.attach(pf);
        tr.commitAllowingStateLoss();
    }

    private void newPlaylist() {
        final View addView = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_new_playlist, null);
        final EditText name_nw_pl = addView.findViewById(R.id.name_new_playlist);

        AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
        a_builder.setView(addView)
                .setPositiveButton("Crea", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //creazione
                        MusicLibrary.addPlaylist(name_nw_pl.getText().toString());
                        MusicLibrary.playList.clear();
                        MusicLibrary.getPlaylists();
                        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                        tr.detach(pf);
                        tr.attach(pf);
                        tr.commit();
                        Toast.makeText(context, "Playlist creata!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false);

        AlertDialog alert = a_builder.create();
        alert.setTitle("Nuova Playlist");
        alert.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_library:
                item.setChecked(true);
                return true;
            case R.id.nav_equalizer:
                Equalizer();
                return true;
            case R.id.nav_recorder:
                Recorder();
                return true;
            case R.id.nav_alteration:
                Alteration();
                return true;
            case R.id.nav_info:
                Info();
                return true;
        }

        DrawerLayout drawer = (DrawerLayout)
                findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Equalizer() {
        Intent intent = new Intent(this, Equalizer.class);
        startActivity(intent);
    }

    private void Recorder() {
        Intent intent = new Intent(this, Recorder.class);
        startActivity(intent);
    }

    private void Alteration() {
        Intent intent = new Intent(this, Alteration.class);
        startActivity(intent);
    }

    private void Setting() {
        Intent intent = new Intent(this, SetActivity.class);
        startActivity(intent);
    }

    private void Info() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    //tasto Drawer
    @Override
    public void onBackPressed() {

        if (mydrawer.isDrawerOpen(GravityCompat.START)) {
            mydrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (sliding_layout != null &&
                (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }

        //pulisco array
        MusicLibrary.songList.clear();
        MusicLibrary.albumList.clear();
        MusicLibrary.artistList.clear();
        MusicLibrary.playList.clear();
    }

    /* ---------------------------------------------- MEDIAPLAYER ----------------------------------------------------------- */

    //MAIN SONG
    public void mainSong() {
        //Inizializzo media player
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //Lancia la canzone
                togglePlay(mp);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //condizione coda brani
                if (!flag_queue) {
                    //passa alla prossima
                    if (currentIndex + 1 < MusicLibrary.songList.size()) {
                        Song next = MusicLibrary.songList.get(currentIndex + 1);
                        changeSelectedSong(currentIndex + 1);
                        prepareSong(next);
                        setImageSliding(next);

                    } else {
                        Song next = MusicLibrary.songList.get(0);
                        changeSelectedSong(0);
                        prepareSong(next);
                        setImageSliding(next);
                    }
                } else {
                    //resta su quel brano
                    if (currentIndex < MusicLibrary.songList.size()) {
                        Song stop = MusicLibrary.songList.get(currentIndex);
                        changeSelectedSong(currentIndex);
                        prepareSong(stop);
                    } else {
                        Song stop = MusicLibrary.songList.get(0);
                        changeSelectedSong(0);
                        prepareSong(stop);
                    }
                }
            }
        });

        //Gestione Seekbar
        handleSeekbar();
        //Controllo del Brano
        pushPlay();
        pushPrevious();
        pushNext();
        pushRandom();
        pushQueue();
    }

    //POSIZIONE SONG
    public void changeSelectedSong(int index) {
        mAdapter.notifyItemChanged(mAdapter.getSelectedPosition());
        currentIndex = index;
        mAdapter.setSelectedPosition(currentIndex);
        mAdapter.notifyItemChanged(currentIndex);
    }

    //PREPARAZIONE SONG
    public void prepareSong(Song song) {
        //primo lancio di un brano
        if (firstLaunch) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            item.setTitle(R.string.action_hide);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
            lp.bottomMargin = 196;
        }
        firstLaunch = false;
        currentSongLength = song.getDuration();
        tb_title.setVisibility(View.VISIBLE);
        tb_artist.setVisibility(View.VISIBLE);
        tb_title2.setVisibility(View.VISIBLE);
        tb_artist2.setVisibility(View.VISIBLE);
        tb_title.setText(song.getTitle()); //ottieni titolo
        tb_artist.setText(song.getArtist()); //ottieni artista
        tb_title2.setText(song.getTitle()); //ottieni titolo
        tb_artist2.setText(song.getArtist()); //ottieni artista
        tv_timeEnd.setText(Utility.convertDuration(song.getDuration()));
        iv_play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_white));
        //play/stop gestito quando push next/previuos
        if (mediaPlayer.isPlaying())
            iv_play2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause));
        else
            iv_play2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_white));

        iv_pause_hide.setVisibility(View.INVISIBLE);

        String path = song.getPath(); //ottieni path

        tv_timeStart.setText(Utility.convertDuration(song.getDuration())); //ottieni durata
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(path));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //PLAY/STOP SONG
    public void togglePlay(MediaPlayer mp) {
        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();

        } else {
            tb_title.setVisibility(View.VISIBLE);
            tb_artist.setVisibility(View.VISIBLE);

            mp.start();

            iv_play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_circle_outline_white));
            iv_play2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause));

            //progressBar
            final Handler mHandler = new Handler();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setMax((int) currentSongLength / 1000);
                    try {
                        if(mediaPlayer.getCurrentPosition()>Alteration.currentPositionB && Alteration.currentPositionB!=-1) {
                            mediaPlayer.seekTo(Alteration.currentPositionA); //ricomincia da A
                            Alteration.isLoop=true;
                        }

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        progressBar.setProgress(mCurrentPosition);
                        tv_timeStart.setText(Utility.convertDuration((long) mediaPlayer.getCurrentPosition()));
                        mHandler.postDelayed(this, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //pulisco array
                        MusicLibrary.songList.clear();
                        MusicLibrary.albumList.clear();
                        MusicLibrary.artistList.clear();
                        MusicLibrary.playList.clear();
                    }
                }
            });

            //seekbar
            final Handler mHandler2 = new Handler();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seekBar.setMax((int) currentSongLength / 1000);
                    try {
                        if(mediaPlayer.getCurrentPosition()>Alteration.currentPositionB && Alteration.currentPositionB!=-1) {
                            mediaPlayer.seekTo(Alteration.currentPositionA); //ricomincia da A
                            Alteration.isLoop = true;
                        }

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        tv_timeStart.setText(Utility.convertDuration((long) mediaPlayer.getCurrentPosition()));
                        mHandler2.postDelayed(this, 1000);

                    } catch (Exception e) {
                        e.printStackTrace();
                        //pulisco array
                        MusicLibrary.songList.clear();
                        MusicLibrary.albumList.clear();
                        MusicLibrary.artistList.clear();
                        MusicLibrary.playList.clear();
                    }
                }
            });
        }
    }

    //RANDOM SONG
    public void randomSong() {
        Random r = new Random();
        int dim = r.nextInt(MusicLibrary.songList.size()); //posizione
        Song song = null;
        for (int i = 0; i < MusicLibrary.songList.size(); i++) {
            if (i == dim) {
                song = new Song(MusicLibrary.songList.get(i).getID(),
                        MusicLibrary.songList.get(i).getTitle(),
                        MusicLibrary.songList.get(i).getArtist(),
                        MusicLibrary.songList.get(i).getDuration(),
                        MusicLibrary.songList.get(i).getPath(),
                        MusicLibrary.songList.get(i).getIdAlbum(),
                        MusicLibrary.songList.get(i).getTrack(),
                        MusicLibrary.songList.get(i).getSize(),
                        MusicLibrary.songList.get(i).getYear());
            }
        }
        Log.v("size", Integer.toString(MusicLibrary.songList.size()));
        Log.v("position", Integer.toString(dim + 1));

        //get posizione
        changeSelectedSong(dim);
        //prepare song
        prepareSong(song);
        //go song
        mainSong();
        //detail song
        pushDetail(this, song);
        //star song
        pushStar(song);
        //image slide
        setImageSliding(song);
        //clean loop
        cleanLoop();
        //notification
        setNotification(song);
    }

    //SEEKBAR
    public void handleSeekbar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //PLAY/PAUSE SONG
    public void pushPlay() {
        //tasto toolbar
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying() && mediaPlayer != null) {
                    iv_play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.playd));
                    iv_play2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.playd));
                    mediaPlayer.pause();
                } else {
                    if (firstLaunch) {
                        Song song = MusicLibrary.songList.get(0);
                        changeSelectedSong(0);
                        prepareSong(song);
                    } else {
                        mediaPlayer.start();
                        firstLaunch = false;
                    }
                    iv_play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_circle_outline_white));
                    iv_play2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause));

                }
            }
        });

        //tasto sliding
        iv_play2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying() && mediaPlayer != null) {
                    iv_play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.playd));
                    iv_play2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.playd));
                    mediaPlayer.pause();
                } else {
                    if (firstLaunch) {
                        Song song = MusicLibrary.songList.get(0);
                        changeSelectedSong(0);
                        prepareSong(song);
                    } else {
                        mediaPlayer.start();
                        firstLaunch = false;
                    }
                    iv_play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_circle_outline_white));
                    iv_play2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause));
                }
            }
        });
    }

    //PREVIOUS SONG
    public void pushPrevious() {
        iv_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstLaunch = false;
                if (mediaPlayer != null) {
                    if (!flag_queue) { //if false
                        if (currentIndex - 1 >= 0) {
                            Song previous = MusicLibrary.songList.get(currentIndex - 1);
                            changeSelectedSong(currentIndex - 1);
                            prepareSong(previous);
                            setImageSliding(previous);
                            cleanLoop();
                        } else {
                            changeSelectedSong(MusicLibrary.songList.size() - 1);
                            prepareSong(MusicLibrary.songList.get(MusicLibrary.songList.size() - 1));
                            setImageSliding(MusicLibrary.songList.get(MusicLibrary.songList.size() - 1));
                            cleanLoop();
                        }
                    }
                }
            }
        });
    }

    //NEXT SONG
    public void pushNext() {
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstLaunch = false;
                if (mediaPlayer != null) {
                    if (!flag_queue) { //if false
                        if (currentIndex + 1 < MusicLibrary.songList.size()) {
                            Song next = MusicLibrary.songList.get(currentIndex + 1);
                            changeSelectedSong(currentIndex + 1);
                            prepareSong(next);
                            setImageSliding(next);
                            cleanLoop();
                        } else {
                            changeSelectedSong(0);
                            prepareSong(MusicLibrary.songList.get(0));
                            setImageSliding(MusicLibrary.songList.get(0));
                            cleanLoop();
                        }
                    }
                }
            }
        });
    }

    //REPEAT SONG
    public void pushQueue() {
        iv_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag_queue) { //if false
                    iv_queue.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_one));
                    flag_queue = true;
                } else {
                    iv_queue.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_white));
                    flag_queue = false;
                }
            }
        });
    }

    //RANDOM SONG
    public void pushRandom() {
        iv_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomSong();
            }
        });
    }

    //STAR SONG
    public void pushStar(final Song song) {
        iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag_star) { //if false
                    iv_favorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star));
                    flag_star = true;

                    //riempio il mio array della canzone
                    starSong.add(new Song(song.getID(), song.getTitle(), song.getArtist(), song.getDuration(),
                            song.getPath(), song.getIdAlbum(), song.getTrack(), song.getSize(), song.getYear()));


                    //creo cartella nuova playlist se non è presente
                    for (int i = 0; i < MusicLibrary.playList.size(); i++) {
                        if (MusicLibrary.playList.get(i).getNamePlaylist().equals("Preferiti")) {
                            folder = true; //cartella esiste
                        }
                    }
                    if (!folder) {
                        MusicLibrary.playList.clear();
                        MusicLibrary.addPlaylist("Preferiti");
                        MusicLibrary.getPlaylists();
                        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                        tr.detach(pf);
                        tr.attach(pf);
                        tr.commit();
                        Toast.makeText(context, "Cartella Preferiti creata!", Toast.LENGTH_LONG).show();
                        folder = true;
                    } else {
                        Toast.makeText(context, "ESISTE", Toast.LENGTH_LONG).show();
                        folder = false;
                    }

                    //ottengo id playlist preferiti
                    long id = 0;
                    for (int i = 0; i < MusicLibrary.playList.size(); i++) {
                        if (MusicLibrary.playList.get(i).getNamePlaylist().equals("Preferiti")) {
                            id = MusicLibrary.playList.get(i).getPlaylistID();
                            Log.v("idPl", Long.toString(id));
                            break;
                        }
                    }

                    //aggiungo brano alla playlist
                    //MusicLibrary.addTrackToPlaylist(song.getID(), id);

                } else {
                    iv_favorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_border_white));
                    flag_star = false;
                }
            }
        });
    }

    //DETAIL SONG
    public void pushDetail(final Context context, final Song song) {
        optionSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongAdapter.dialog_det(context, song);
            }
        });
    }

    //CONTROL LOOP
    public void cleanLoop(){
        mediaPlayer.setLooping(false);
        Alteration.currentPositionA=-1;
        Alteration.currentPositionB=-1;
        Alteration.isLoop=false;
    }

    //IMAGE SONG
    public void setImageSliding(Song song) {
        File imgFile;
        Bitmap art;
        //image album song
        cover_sliding.setImageResource(R.drawable.phonograph);
        for (int i = 0; i < MusicLibrary.albumList.size(); i++) {
            if (song.getIdAlbum() == MusicLibrary.albumList.get(i).getID()) {
                String path = MusicLibrary.albumList.get(i).getPath(); //path album
                try { //Prova se l'immagine di copertina è presente la inserisce
                    imgFile = new File(path);
                    if (imgFile.exists()) {
                        art = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        cover_sliding.setImageBitmap(art);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Audio notification system
    public void setNotification(Song song) {
        mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_music_note)
                .setAutoCancel(true)
                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,mBuilder.build());
    }

    public void cancelNotification(Context ctx) {
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setAutoCancel(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isFinishing() && mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(refresh) {
            refreshFragment();
            refresh=false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //DESTROY MEDIAPLAYER
    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
