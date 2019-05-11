package com.example.francesco.masterplayer.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.francesco.masterplayer.adapter.SongsArtistAdapter;
import com.example.francesco.masterplayer.decoration.SimpleDividerItemDecoration;
import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.R;
import com.example.francesco.masterplayer.utility.Utility;

import java.io.File;
import java.util.ArrayList;

public class ArtistSongFragment extends Fragment {

    private View artistSongView;
    private RecyclerView recycler;
    private ArrayList<Song> songsArtist = new ArrayList<Song>();
    public MainActivity parent;
    SongsArtistAdapter mAdapter;
    TextView name_artist, totDuration;
    ImageView arrow, ic_time;
    Bundle extras_songs;
    File imgFile;
    Bitmap art;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        artistSongView = inflater.inflate(R.layout.fragment_artist_song, container, false);

        extras_songs = getArguments();
        songsArtist = extras_songs.getParcelableArrayList("arraySongArtist");

        initializedViewArtistSong();

        mAdapter = new SongsArtistAdapter(this.getActivity().getApplicationContext(), songsArtist, new SongsArtistAdapter.RecyclerItemClickListener() {
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
        return artistSongView;
    }

    private void initializedViewArtistSong(){

        name_artist = artistSongView.findViewById(R.id.name_artist);
        name_artist.setText(ArtistFragment.title_artist);

        //durata totale album
        long j = 0;
        for(int i = 0; i < songsArtist.size(); i++) {
            j = j + songsArtist.get(i).getDuration();
        }
        String duration = Utility.convertDuration(j);
        totDuration = artistSongView.findViewById(R.id.totDuration);
        totDuration.setText(duration);

        //timer
        ic_time = artistSongView.findViewById(R.id.ic_time);


        //Recycler
        recycler = artistSongView.findViewById(R.id.recyclerSongsArtist);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext()));
        recycler.setNestedScrollingEnabled(false);
        recycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity())); //divider

        arrow = artistSongView.findViewById(R.id.ic_arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(parent.arsf);
                ft.commit();

            }
        });

    }
}
