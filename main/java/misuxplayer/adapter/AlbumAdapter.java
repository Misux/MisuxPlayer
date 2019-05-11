package com.example.francesco.masterplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;

import com.example.francesco.masterplayer.model.Album;
import com.example.francesco.masterplayer.R;

public class AlbumAdapter extends  RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context context;
    private ArrayList <Album> albumList;
    private AlbumAdapter.RecyclerItemClickListener listener;
    private int selectedPosition;

    public AlbumAdapter(Context context, ArrayList<Album> arrayList, AlbumAdapter.RecyclerItemClickListener listener){
        this.context = context;
        this.albumList = arrayList;
        this.listener = listener;
    }

    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.example.francesco.masterplayer.R.layout.album_row, parent, false);
        return new AlbumAdapter.AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {

        final Album album = albumList.get(position);
        Bitmap art; //image cover album
        String path = album.getPath();
        art = SongAdapter.decodeSampledBitmapFromResource(context.getResources(),R.drawable.phonograph,173,173);

        try { //Prova se l'immagine di copertina è presente la inserisce
            File imgFile = new File(path);
            if(imgFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = 2;
                options.inJustDecodeBounds = false;
                options.inTempStorage = new byte[64 * 1024];
                art = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        holder.albumImg.setImageBitmap(art);
        holder.nameAlbum.setText(album.getAlbumName());
        holder.nameArtist.setText(album.getArtistName());
        holder.bind(album, listener);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumImg;
        private TextView nameAlbum, nameArtist;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            albumImg = itemView.findViewById(com.example.francesco.masterplayer.R.id.albumImg);
            nameAlbum = itemView.findViewById(com.example.francesco.masterplayer.R.id.nameAlbum);
            nameArtist = itemView.findViewById(com.example.francesco.masterplayer.R.id.nameArtist);
        }

        public void bind(final Album album, final AlbumAdapter.RecyclerItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    listener.onClickListener(album, getLayoutPosition());
                }
            });
        }

    }

    public interface RecyclerItemClickListener{
        void onClickListener(Album album, int position);

    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

}



