package com.example.francesco.masterplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.MusicLibrary;
import com.example.francesco.masterplayer.R;
import com.example.francesco.masterplayer.adapter.SongAdapter;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.decoration.SimpleDividerItemDecoration;


import java.util.ArrayList;


public class SongFragment extends Fragment {

    private View songView;
    private RecyclerView recycler;
    public MainActivity parent;
    SongAdapter mAdapter;
    TextView no_songs, line;
    NestedScrollView scrollBrani;
    Button btnRand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        songView = inflater.inflate(R.layout.fragment_song, container, false);

        enjoyPlayer();

        return songView;
    }

    public void enjoyPlayer(){

        //Inizializzo layout
        initializedViewBrani();

        mAdapter = new SongAdapter(getActivity().getApplicationContext(), MusicLibrary.songList, new SongAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Song song, int position) {
                //ottieni la posizione
                parent.changeSelectedSong(position);
                //prepara la song
                parent.prepareSong(song);
                //lancia la song
                parent.mainSong();
                //detail song
                parent.pushDetail(getActivity(), song);
                //star song
                parent.pushStar(song);
                //image slide
                parent.setImageSliding(song);
                //notification
                parent.setNotification(song);
            }
        });
        recycler.setAdapter(mAdapter);
    }

    private void initializedViewBrani(){

        //NestedscrollView
        scrollBrani = songView.findViewById(R.id.NestscrollView);

        //Button Random
        btnRand = songView.findViewById(R.id.btnRand);
        btnRand.setVisibility(View.VISIBLE);
        btnRand.setSelected(true);
        btnRand.setFocusable(true);
        btnRand.setFocusableInTouchMode(true);
        btnRand.requestFocus();
        btnRand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.randomSong();
            }
        });

        line = songView.findViewById(R.id.line);
        line.setVisibility(View.VISIBLE);


        //Recycler
        recycler = songView.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recycler.setNestedScrollingEnabled(false);
        recycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity())); //divider

        no_songs = songView.findViewById(R.id.no_song);
        if (MusicLibrary.songList.size() == 0) {
            no_songs.setVisibility(View.VISIBLE);
            btnRand.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);

        }
    }

}
