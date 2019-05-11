package com.example.francesco.masterplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Album;
import com.example.francesco.masterplayer.adapter.AlbumAdapter;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.MusicLibrary;
import com.example.francesco.masterplayer.R;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    private View albumView;
    private RecyclerView recyclerViewAlbum;
    public static String title_album, cover_art;
    public static ArrayList<Song> songsAlbum = new ArrayList<Song>();
    private Bundle args = new Bundle();
    public MainActivity parent;
    TextView no_album;
    RecyclerView.LayoutManager layoutManager;
    AlbumAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        albumView = inflater.inflate(R.layout.fragment_album, container, false);

        enjoyAlbum();

        return albumView;
    }

    private void enjoyAlbum() {

        //Inizializzo
        initializedViewAlbum();

        mAdapter = new AlbumAdapter(getActivity().getApplicationContext(), MusicLibrary.albumList, new AlbumAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Album album, int position) {

                songsAlbum.clear();
                for (int i = 0; i < MusicLibrary.songList.size(); i++) {
                    if (MusicLibrary.songList.get(i).getIdAlbum() == album.getID()) {
                        songsAlbum.add(new Song(
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

                if (albumView.findViewById(R.id.fragment_container_album) != null) {

                    //passaggio titolo
                    title_album = album.getAlbumName();

                    //passaggio path cover
                    cover_art = album.getPath();

                    //passaggio dati
                    args.putParcelableArrayList("arraySongAlbum", songsAlbum);
                    parent.alsf.setArguments(args);

                    if(parent.alsf.isAdded()){
                        Log.v("Fragment: ", "is already add!");
                    } else {
                        //transazione al nuovo fragment
                        FragmentTransaction ft = getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .add(R.id.fragment_container_album, parent.alsf)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null);
                        ft.commit();
                    }

               }
            }
        });
        recyclerViewAlbum.setAdapter(mAdapter);
    }

    private void initializedViewAlbum() {

        //Recycler
        recyclerViewAlbum = albumView.findViewById(R.id.recyclerAlbum);
        layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerViewAlbum.setLayoutManager(layoutManager);
        recyclerViewAlbum.setNestedScrollingEnabled(false);

        //nessun album
        no_album = albumView.findViewById(R.id.no_album);
        if (MusicLibrary.albumList.size() == 0) no_album.setVisibility(View.VISIBLE);
    }
}

