package com.example.francesco.masterplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.MusicLibrary;
import com.example.francesco.masterplayer.R;

import java.io.File;
import java.util.ArrayList;

public class SongsPlaylistAdapter extends RecyclerView.Adapter<SongsPlaylistAdapter.SongViewHolder> {

    public Context context;
    public ArrayList<Song> songList;
    private RecyclerItemClickListener listener;
    private int selectedPosition;

    public SongsPlaylistAdapter(Context context, ArrayList <Song> songList, RecyclerItemClickListener listener){
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_song_row, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {

        final Song song = songList.get(position);
        Bitmap art=null;

        if(song != null){
            //image album song
            for(int i = 0; i < MusicLibrary.albumList.size(); i++) {
                if (song.getIdAlbum() == MusicLibrary.albumList.get(i).getID()){
                    String path = MusicLibrary.albumList.get(i).getPath(); //path album
                    art = SongAdapter.decodeSampledBitmapFromResource(context.getResources(),R.drawable.phonograph,41,41);

                    try { //Prova se l'immagine di copertina è presente la inserisce
                        File imgFile = new File(path);
                        if(imgFile.exists()) {
                            art = BitmapFactory.decodeFile(imgFile.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            holder.cover_album_playlist.setImageBitmap(art);
            holder.tv_title.setText(song.getTitle());
            holder.artist.setText(song.getArtist());

            //richiamo da Song Adapter le opzioni
            SongAdapter.popUpPL(context, holder.card_textOption_song_pl, song);

            holder.bind(song, listener);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_title, artist;
        private ImageView cover_album_playlist, iv_play_active, textOptionPlaylistSong;
        private CardView card_textOption_song_pl;

        public SongViewHolder(View itemView) {
            super(itemView);
            cover_album_playlist = itemView.findViewById(R.id.number_track);
            tv_title = itemView.findViewById(R.id.tv_title);
            artist = itemView.findViewById(R.id.tv_artist);
            textOptionPlaylistSong = itemView.findViewById(R.id.textOption_song_pl);
            card_textOption_song_pl = itemView.findViewById(R.id.card_textOption_song_pl);
            iv_play_active = itemView.findViewById(R.id.iv_play_active);

        }

        public void bind(final Song song, final RecyclerItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickListener(song, getLayoutPosition());
                }
            });
        }

    }

    public interface RecyclerItemClickListener{
        void onClickListener(Song song, int position);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
