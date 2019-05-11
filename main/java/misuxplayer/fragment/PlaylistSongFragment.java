package com.example.francesco.masterplayer.fragment;

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

import com.example.francesco.masterplayer.adapter.SongsPlaylistAdapter;
import com.example.francesco.masterplayer.decoration.SimpleDividerItemDecoration;
import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.R;
import com.example.francesco.masterplayer.utility.Utility;

import java.util.ArrayList;

public class PlaylistSongFragment extends Fragment {

    private View playlistSongView;
    private RecyclerView recycler;
    private ArrayList<Song> songsPlaylist = new ArrayList<Song>();
    public MainActivity parent;
    TextView playlist_name, totDuration, no_playlist;
    SongsPlaylistAdapter mAdapter;
    ImageView arrow, ic_time;
    Bundle extras_songs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        playlistSongView = inflater.inflate(R.layout.fragment_playlist_song, container, false);


        //passaggio songs da PlaylistFragment
        extras_songs = getArguments();
        songsPlaylist = extras_songs.getParcelableArrayList("arraySongPlaylist");

        initializedViewPlaylistSong();

        mAdapter = new SongsPlaylistAdapter(this.getActivity().getApplicationContext(), songsPlaylist, new SongsPlaylistAdapter.RecyclerItemClickListener() {
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
        return playlistSongView;

    }

    private void initializedViewPlaylistSong(){

        playlist_name = playlistSongView.findViewById(R.id.name_playlist);
        playlist_name.setText(PlaylistFragment.name_playlist);

        //durata totale album
        long j = 0;
        for(int i = 0; i < songsPlaylist.size(); i++) {
            j = j + songsPlaylist.get(i).getDuration();
        }
        String duration = Utility.convertDuration(j);
        totDuration = playlistSongView.findViewById(R.id.totDuration);
        totDuration.setText(duration);

        //timer
        ic_time = playlistSongView.findViewById(R.id.ic_time);
        no_playlist = playlistSongView.findViewById(R.id.no_playlist);
        if(j == 0) no_playlist.setVisibility(View.VISIBLE);

        //Recycler
        recycler = playlistSongView.findViewById(R.id.recyclerSongsPlaylist);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext()));
        recycler.setNestedScrollingEnabled(false);
        recycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity())); //divider

        arrow = playlistSongView.findViewById(R.id.ic_arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(parent.psf);
                ft.commit();

            }
        });

    }
}
