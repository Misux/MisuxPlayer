package com.example.francesco.masterplayer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Playlist;
import com.example.francesco.masterplayer.MusicLibrary;
import com.example.francesco.masterplayer.R;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private Context context;
    private ArrayList <Playlist> playList;
    private RecyclerItemClickListener listener;
    private int selectedPosition;
    public static MainActivity parent;

    public PlaylistAdapter(Context context, ArrayList<Playlist> playList, RecyclerItemClickListener listener){
        this.context = context;
        this.playList = playList;
        this.listener = listener;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_row, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {

        Playlist playlist = playList.get(position);
        holder.nomePlaylist.setText(playlist.getNamePlaylist());
        popUp_playlist(context.getApplicationContext(), holder.card_pl, playlist);
        holder.bind(playlist, listener);

    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{

        private TextView nomePlaylist, num_brani_pl;
        private ImageView textOption_playlist;
        private CardView card_pl;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            nomePlaylist = itemView.findViewById(R.id.name_playlist);
            textOption_playlist = itemView.findViewById(R.id.textOption_song_pl);
            card_pl = itemView.findViewById(R.id.card_textOption_pl);
            //num_brani_pl = (TextView) itemView.findViewById(R.id.num_brani_pl);
        }

        public void bind(final Playlist playlist, final RecyclerItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickListener(playlist, getLayoutPosition());
                }
            });
        }

    }

    public interface RecyclerItemClickListener{
        void onClickListener(Playlist artist, int position);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    //POPUP OPTIONS SONG ALBUM ARTIST
    private static void popUp_playlist(final Context context, final CardView popUp, final Playlist playlist) {
        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Display options menu
                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context.getApplicationContext(), popUp);
                popupMenu.getMenuInflater().inflate(R.menu.playlist_set, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //case R.id.svuota:
                                //empty_pl(view.getContext(), playlist);
                                //break;
                            case R.id.renamePL:
                                rename_PL(view.getContext(),playlist);
                                break;
                            case R.id.deletePlaylist:
                                delete_playlist(view.getContext(), playlist);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    //svuota playlist
    private static void empty_pl(final Context context, final Playlist playlist) {
        MusicLibrary.deletePlaylistAllTracks(context, playlist.getPlaylistID());
        MusicLibrary.playListTrack.clear();
        MusicLibrary.getPlaylistTracks(context, playlist.getPlaylistID());
        //refresh fragments
        FragmentActivity activity = (FragmentActivity) context;
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction tr = manager.beginTransaction();
        tr.detach(parent.pf);
        tr.detach(parent.psf);
        tr.attach(parent.pf);
        tr.attach(parent.psf);
        tr.commit();
        Toast.makeText(context.getApplicationContext(), "Playlist svuotata!", Toast.LENGTH_LONG).show();
    }

    //rinomina playlist
    private static void rename_PL(final Context context, final Playlist playlist){

        final View addView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.alert_rename_playlist, null);
        final EditText rename_pl = (EditText) addView.findViewById(R.id.rename_playlist);

        AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
        a_builder.setView(addView).setPositiveButton("Rinomina", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //rinomina
                        MusicLibrary.renamePlaylist(context,rename_pl.getText().toString(), playlist.getPlaylistID());
                        MusicLibrary.playList.clear();
                        //get song
                        MusicLibrary.getPlaylists();
                        //refresh fragments
                        FragmentActivity activity = (FragmentActivity) context;
                        FragmentManager manager = activity.getSupportFragmentManager();
                        FragmentTransaction tr = manager.beginTransaction();
                        tr.detach(parent.pf);
                        tr.attach(parent.pf);
                        tr.commit();
                        Toast.makeText(context.getApplicationContext(), "Playlist Rinominata!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Annulla",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false);
        AlertDialog alert = a_builder.create();
        alert.setTitle("Rinomina Playlist");
        alert.show();
    }

    //delete playlist
    private static void delete_playlist(final Context context, final Playlist playlist){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
        a_builder.setMessage("Confermi di voler cancellare la playlist " +playlist.getNamePlaylist()+"?")
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //delete
                        MusicLibrary.deletePlaylist(context, playlist.getPlaylistID());
                        //clear array
                        MusicLibrary.playListTrack.clear();
                        MusicLibrary.playList.clear();
                        //get playlist
                        MusicLibrary.getPlaylists();
                        MusicLibrary.getPlaylistTracks(context, playlist.getPlaylistID());
                        //refresh fragments
                        FragmentActivity activity = (FragmentActivity) context;
                        FragmentManager manager = activity.getSupportFragmentManager();
                        FragmentTransaction tr = manager.beginTransaction();
                        tr.detach(parent.pf);
                        tr.attach(parent.pf);
                        tr.commit();
                        Toast.makeText(context.getApplicationContext(), "Playlist eliminata!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Annulla",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false);
        AlertDialog alert = a_builder.create();
        alert.setTitle("Elimina Playlist");
        alert.show();
    }
}

