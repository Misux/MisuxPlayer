package com.example.francesco.masterplayer.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.francesco.masterplayer.adapter.SongsAlbumAdapter;
import com.example.francesco.masterplayer.decoration.SimpleDividerItemDecoration;
import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.R;
import com.example.francesco.masterplayer.utility.Utility;

import java.io.File;
import java.util.ArrayList;

public class AlbumSongFragment extends Fragment {

    private View albumSongView;
    private RecyclerView recycler;
    private ArrayList<Song> songsAlbum = new ArrayList<Song>();
    public MainActivity parent;
    TextView name_album, totDuration, year_album;
    ImageView arrow, ic_time, album_cover;
    Bundle extras_songs;
    SongsAlbumAdapter mAdapter;
    File imgFile;
    Bitmap art;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        albumSongView = inflater.inflate(R.layout.fragment_album_song, container, false);

        extras_songs = getArguments();
        songsAlbum = extras_songs.getParcelableArrayList("arraySongAlbum");


        initializedViewAlbumSong();

        mAdapter = new SongsAlbumAdapter(this.getActivity().getApplicationContext(), songsAlbum, new SongsAlbumAdapter.RecyclerItemClickListener() {
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
        return albumSongView;
    }

    private void initializedViewAlbumSong(){

        //Image album
        album_cover = albumSongView.findViewById(R.id.album_cover_song);
        try { //Prova se l'immagine di copertina è presente la inserisce
            imgFile = new File(AlbumFragment.cover_art);
            if(imgFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = 2;
                options.inJustDecodeBounds = false;
                options.inTempStorage = new byte[64 * 1024];
                art = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

                album_cover.setImageBitmap(art);
            }
        } catch (Exception e) { //Se non è presente gestisce l'eccezione e inserisce l'image di default
            e.printStackTrace();
        }

        //Title album
        name_album = albumSongView.findViewById(R.id.name_album);
        name_album.setText(AlbumFragment.title_album);

        //Anno album
        year_album = albumSongView.findViewById(R.id.year_album);

        String yearS = null;
        for(int i = 0; i < songsAlbum.size(); i++) {
            yearS = songsAlbum.get(i).getYear();
        }
        year_album.setText(yearS);


        //durata totale album
        long j = 0;
        for(int i = 0; i < songsAlbum.size(); i++) {
            j = j + songsAlbum.get(i).getDuration();
        }
        String duration = Utility.convertDuration(j);
        totDuration = albumSongView.findViewById(R.id.totDuration);
        totDuration.setText(duration);

        //timer
        ic_time = albumSongView.findViewById(R.id.ic_time);

        //Recycler
        recycler = albumSongView.findViewById(R.id.recyclerSongsAlbum);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext()));
        recycler.setNestedScrollingEnabled(false);
        recycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity())); //divider

        //back Album Fragment
        arrow = albumSongView.findViewById(R.id.ic_arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(parent.alsf);
                ft.commit();

            }
        });

    }
}
