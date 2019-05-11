package com.example.francesco.masterplayer.fragment;

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

import com.example.francesco.masterplayer.adapter.PlaylistAdapter;
import com.example.francesco.masterplayer.decoration.SimpleDividerItemDecoration;
import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Playlist;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.MusicLibrary;
import com.example.francesco.masterplayer.R;

import java.util.ArrayList;


public class PlaylistFragment extends Fragment {

    private View playlistView;
    private RecyclerView recyclerPlaylist;
    public static ArrayList<Song> songsPlaylist = new ArrayList<Song>();
    private Bundle args = new Bundle();
    public static String name_playlist;
    TextView no_playlist;
    NestedScrollView scrollPlaylist;
    PlaylistAdapter mAdapter;
    public MainActivity parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        playlistView = inflater.inflate(R.layout.fragment_playlist, container, false);

        enjoyPlaylist();

        return playlistView;

    }

    private void enjoyPlaylist(){

        //Inizializzo
        initializedPlaylist();

        mAdapter = new PlaylistAdapter(getActivity().getApplicationContext(), MusicLibrary.playList, new PlaylistAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Playlist playlist, int position) {

                songsPlaylist.clear();
                MusicLibrary.playListTrack.clear();

                //get playlist tracks
                MusicLibrary.getPlaylistTracks(getActivity().getApplicationContext(), playlist.getPlaylistID());

                for (int i = 0; i < MusicLibrary.playListTrack.size(); i++) {
                    for(int j = 0; j < MusicLibrary.songList.size(); j++) {
                        if (MusicLibrary.playListTrack.get(i).getTitlePL().equals(MusicLibrary.songList.get(j).getTitle())) {

                            //costruzione un array song riferito a playlist
                            songsPlaylist.add(new Song(
                                        MusicLibrary.songList.get(j).getID(),
                                        MusicLibrary.songList.get(j).getTitle(),
                                        MusicLibrary.songList.get(j).getArtist(),
                                        MusicLibrary.songList.get(j).getDuration(),
                                        MusicLibrary.songList.get(j).getPath(),
                                        MusicLibrary.songList.get(j).getIdAlbum(),
                                        MusicLibrary.songList.get(i).getTrack(),
                                        MusicLibrary.songList.get(i).getSize(),
                                        MusicLibrary.songList.get(i).getYear())
                                );
                        }
                    }
                }

                if (playlistView.findViewById(R.id.fragment_container_playlist) != null) {

                    //passaggio titolo
                    name_playlist = playlist.getNamePlaylist();

                    //passaggio dati
                    args.putParcelableArrayList("arraySongPlaylist", songsPlaylist);
                    parent.psf.setArguments(args);


                    //transazione al nuovo fragment
                    if(parent.psf.isAdded()){
                        Log.v("Fragment: ", "is already add!");
                    } else {
                        FragmentTransaction ft = getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .add(R.id.fragment_container_playlist, parent.psf)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null);
                        ft.commit();
                    }

                }
            }
        });
        recyclerPlaylist.setAdapter(mAdapter);
    }

    private void initializedPlaylist(){

        //NestedscrollView
        scrollPlaylist = playlistView.findViewById(R.id.NestscrollView);

        //Recycler
        recyclerPlaylist = playlistView.findViewById(R.id.recyclerPlayList);
        recyclerPlaylist.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerPlaylist.setNestedScrollingEnabled(false);
        recyclerPlaylist.addItemDecoration(new SimpleDividerItemDecoration(getActivity())); //divider

        no_playlist = playlistView.findViewById(R.id.no_playlist);
        if (MusicLibrary.playList.size() == 0) no_playlist.setVisibility(View.VISIBLE);
    }
}
