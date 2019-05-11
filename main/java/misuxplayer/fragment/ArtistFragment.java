package com.example.francesco.masterplayer.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.francesco.masterplayer.adapter.ArtistAdapter;
import com.example.francesco.masterplayer.decoration.SimpleDividerItemDecoration;
import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Artist;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.MusicLibrary;
import com.example.francesco.masterplayer.R;

import java.io.File;
import java.util.ArrayList;

public class ArtistFragment extends Fragment {

    private View artistView;
    private RecyclerView recyclerViewArtist;
    public static String title_artist;
    public static ArrayList<Song> songsArtist = new ArrayList<Song>();
    private Bundle args = new Bundle();
    public MainActivity parent;
    TextView no_artist;
    NestedScrollView scrollArtist;
    ArtistAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        artistView = inflater.inflate(R.layout.fragment_artist, container, false);

        enjoyArtist();

        return artistView;
    }

    private void enjoyArtist() {

        //Inizializzo
        initializedViewArtist();

        mAdapter = new ArtistAdapter(getActivity().getApplicationContext(), MusicLibrary.artistList, new ArtistAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Artist artist, int position) {
                songsArtist.clear();
                for (int i = 0; i < MusicLibrary.songList.size(); i++) {
                    if (MusicLibrary.songList.get(i).getArtist().equals(artist.getNameArtist())) {

                        //costruzione un array song riferito agli artisti
                        songsArtist.add(new Song(
                                MusicLibrary.songList.get(i).getID(),
                                MusicLibrary.songList.get(i).getTitle(),
                                MusicLibrary.songList.get(i).getArtist(),
                                MusicLibrary.songList.get(i).getDuration(),
                                MusicLibrary.songList.get(i).getPath(),
                                MusicLibrary.songList.get(i).getIdAlbum(),
                                MusicLibrary.songList.get(i).getTrack(),
                                MusicLibrary.songList.get(i).getSize(),
                                MusicLibrary.songList.get(i).getYear())
                        );
                    }
                }

                if (artistView.findViewById(R.id.fragment_container_artist) != null) {

                    //passaggio titolo
                    title_artist = artist.getNameArtist();

                    //passaggio dati a song_artist
                    args.putParcelableArrayList("arraySongArtist", songsArtist);
                    parent.arsf.setArguments(args);

                    //transazione al nuovo fragment
                    //parent.arsf.setExitTransition(new Slide(Gravity.LEFT));
                    if(parent.arsf.isAdded()){
                        Log.v("Fragment: ", "is already add!");
                    } else {
                        FragmentTransaction ft = getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .add(R.id.fragment_container_artist, parent.arsf)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null);
                        ft.commit();
                    }

                }

            }
        });
        recyclerViewArtist.setAdapter(mAdapter);

    }

    private void initializedViewArtist(){
        //NestedscrollView
        scrollArtist = artistView.findViewById(R.id.NestscrollView);

        //Recycler
        recyclerViewArtist = artistView.findViewById(R.id.recyclerArtist);
        recyclerViewArtist.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerViewArtist.setNestedScrollingEnabled(false);
        recyclerViewArtist.addItemDecoration(new SimpleDividerItemDecoration(getActivity())); //divider

        no_artist = artistView.findViewById(R.id.no_artist);
        if (MusicLibrary.artistList.size() == 0) no_artist.setVisibility(View.VISIBLE);

    }
}


