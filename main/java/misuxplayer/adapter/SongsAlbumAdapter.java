package com.example.francesco.masterplayer.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.R;
import com.example.francesco.masterplayer.utility.Utility;

import java.util.ArrayList;

public class SongsAlbumAdapter extends RecyclerView.Adapter<SongsAlbumAdapter.SongViewHolder> {

    public Context context;
    public ArrayList<Song> songList;
    private RecyclerItemClickListener listener;
    private int selectedPosition;
    public MainActivity parent;

    public SongsAlbumAdapter(Context context, ArrayList <Song> songList, RecyclerItemClickListener listener){
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_album_row, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {

        final Song song = songList.get(position);
        if(song != null){

            int track = Integer.parseInt(song.getTrack());
            if(track <= 0 || track > 100) holder.numTrack.setText("-");
            else holder.numTrack.setText(song.getTrack());

            holder.tv_title.setText(song.getTitle());
            holder.tv_artist.setText(song.getArtist());
            String duration = Utility.convertDuration(song.getDuration());
            holder.tv_duration.setText(duration);

            //richiamo da Song Adapter le opzioni
            SongAdapter.popUp(context, holder.card_textOption_album_song, song);

            holder.bind(song, listener);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_title, tv_artist, tv_duration, numTrack;
        private CardView card_textOption_album_song;

        public SongViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            numTrack = itemView.findViewById(R.id.number_track);
            card_textOption_album_song = itemView.findViewById(R.id.card_textOption_album_song);

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
