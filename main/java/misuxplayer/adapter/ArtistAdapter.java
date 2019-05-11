package com.example.francesco.masterplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.francesco.masterplayer.model.Artist;
import com.example.francesco.masterplayer.R;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context context;
    private ArrayList<Artist> artistList;
    private RecyclerItemClickListener listener;
    private int selectedPosition;

    public ArtistAdapter(Context context, ArrayList<Artist> artistList, RecyclerItemClickListener listener){

        this.context = context;
        this.artistList = artistList;
        this.listener = listener;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_row, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {

        Artist artist = artistList.get(position);

            holder.nomeArtist.setText(artist.getNameArtist());
            holder.numAlbum.setText(String.valueOf(artist.getNumAlbum()) + " Album ");
        if(artist.getNumBrani()==1) {
            holder.numBrani.setText("- " + String.valueOf(artist.getNumBrani()) + " Brano");
        } else {
            holder.numBrani.setText("- " + String.valueOf(artist.getNumBrani()) + " Brani");
        }

        holder.bind(artist, listener); //necessario mette in ascolto la recycler

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder{

        private TextView nomeArtist, numAlbum, numBrani;
        private ImageView artist_cover;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            artist_cover = itemView.findViewById(R.id.artistacover);
            nomeArtist = itemView.findViewById(R.id.name_artist);
            numAlbum = itemView.findViewById(R.id.num_Album);
            numBrani = itemView.findViewById(R.id.num_Brani);

        }

        public void bind(final Artist artist, final RecyclerItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickListener(artist, getLayoutPosition());
                }
            });
        }

    }

    public interface RecyclerItemClickListener{
        void onClickListener(Artist artist, int position);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
