package com.example.francesco.masterplayer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.MusicLibrary;
import com.example.francesco.masterplayer.R;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ListView lv;
    SearchView search;
    Toolbar toolbarSearch;
    TextView noSearch;
    ImageView backHome;
    ArrayAdapter adapterSearch;
    MainActivity instance = MainActivity.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Toolbar
        toolbarSearch = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbarSearch);
        toolbarSearch.setTitleTextColor(Color.parseColor("#fcfcfc"));

        //back home
        backHome = (ImageView) findViewById(R.id.ic_arrow);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Nessun Brano
        noSearch = (TextView) findViewById(R.id.noSerch);

        //inserisco dentro un array string i brani
        int size = MusicLibrary.songList.size();
        final String[] FirstSource = new String[size];
        for(int i=0; i<size; i++) {
            FirstSource[i] = MusicLibrary.songList.get(i).getTitle(); //nomi Songs
        }

        lv = (ListView) findViewById(R.id.idlistsong);
        adapterSearch = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, FirstSource);
        lv.setAdapter(adapterSearch);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String nameSong = FirstSource[position]; //nome song
                Song song = null;
                for (int i = 0; i < MusicLibrary.songList.size(); i++) {
                    if (MusicLibrary.songList.get(i).getTitle().equals(nameSong)) {
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
                //ottieni la posizione
                instance.changeSelectedSong(position);
                //prepara la song
                instance.prepareSong(song);
                //lancia la song
                instance.mainSong();
                //detail song
                instance.pushDetail(getApplicationContext(), song);
                //star song
                instance.pushStar(song);
                //image slide
                instance.setImageSliding(song);
            }
        });

        //SearchView
        search = (SearchView) findViewById(R.id.idsearch);
        search.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_hint) + "</font>"));
        search.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterSearch.getFilter().filter(newText);
        return false;
    }
}
