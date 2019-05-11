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

public class SongsArtistAdapter extends RecyclerView.Adapter<SongsArtistAdapter.SongViewHolder> {

    private Context context;
    private ArrayList<Song> songList;
    private RecyclerItemClickListener listener;
    private int selectedPosition;
    private String nome_album;

    public SongsArtistAdapter(Context context, ArrayList <Song> songList, RecyclerItemClickListener listener){
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_artist_row, parent, false);
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

            holder.cover_album_artist.setImageBitmap(art);
            holder.tv_title.setText(song.getTitle());

            for (int i = 0; i < MusicLibrary.albumList.size(); i++) {
                if (MusicLibrary.albumList.get(i).getID() == song.getIdAlbum())
                    nome_album = MusicLibrary.albumList.get(i).getAlbumName();
            }

            holder.name_album.setText(nome_album);

            //richiamo da Song Adapter le opzioni
            SongAdapter.popUp(context, holder.card_textOption_artist, song);

            holder.bind(song, listener);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_title, name_album;
        private ImageView cover_album_artist;
        CardView card_textOption_artist;

        public SongViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            name_album = itemView.findViewById(R.id.tv_album);
            cover_album_artist = itemView.findViewById(R.id.cover_album_artist);
            card_textOption_artist = itemView.findViewById(R.id.card_textOption_artist);

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
