package com.example.francesco.masterplayer.adapter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Playlist;
import com.example.francesco.masterplayer.model.PlaylistTrack;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.MusicLibrary;
import com.example.francesco.masterplayer.R;
import com.example.francesco.masterplayer.utility.Utility;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    public static Context context_songAdpter;
    private ArrayList<Song> songList;
    private RecyclerItemClickListener listener;
    private int selectedPosition;
    public static MainActivity parent;

    public SongAdapter(Context context_songAdpter, ArrayList <Song> songList, RecyclerItemClickListener listener){
        this.context_songAdpter = context_songAdpter;
        this.songList = songList;
        this.listener = listener;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row, parent, false);
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

                    art = decodeSampledBitmapFromResource(context_songAdpter.getResources(),R.drawable.phonograph,41,41);
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
                }
            }
            holder.song_cover.setImageBitmap(art);
            holder.tv_title.setText(song.getTitle());
            holder.tv_artist.setText(song.getArtist());
            String duration = Utility.convertDuration(song.getDuration());
            holder.tv_duration.setText(duration);
            //popUpMenu
            popUp(context_songAdpter.getApplicationContext(), holder.card_textOption_song, song);
            holder.bind(song, listener);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_title, tv_artist, tv_duration;
        private ImageView song_cover;
        private CardView card_album, card_textOption_song;

        public SongViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            song_cover = itemView.findViewById(R.id.song_cover);
            card_textOption_song = itemView.findViewById(R.id.card_textOption_song);
            card_album = itemView.findViewById(R.id.card_album);
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

    /* ---------------------------------------- OPTIONS SONG -------------------------------------------------------- */

    //POPUP OPTIONS SONG/ALBUM/ARTIST
    public static void popUp(final Context context, final CardView popUp, final Song song) {
        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Display options menu
                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context.getApplicationContext(), popUp);
                popupMenu.getMenuInflater().inflate(R.menu.song_set, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addPlaylist:
                                addSongToPl(view.getContext(), song);
                                break;
                            case R.id.share:
                                shareIt(song.getPath());                                break;
                            case R.id.dettagli:
                                dialog_det(view.getContext(), song);
                                break;
                            case R.id.setRington:
                                checkPermissionRington(song);
                                break;
                            case R.id.deleteSong:
                                delete_song(view.getContext(), song);
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

    //POPUP OPTIONS PLAYLIST
    public static void popUpPL(final Context context, final CardView popUp, final Song song) {
        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Display options menu
                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context.getApplicationContext(), popUp);
                popupMenu.getMenuInflater().inflate(R.menu.playlist_song_set, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sharePL:
                                shareIt(song.getPath());
                                break;
                            case R.id.dettagliPL:
                                dialog_det(view.getContext(), song);
                                break;
                            case R.id.setRingtonPL:
                                checkPermissionRington(song);
                                break;
                            case R.id.deleteSongPL:
                                delete_song(view.getContext(), song);
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

    public static void addSongToPl(final Context context, final Song song){

        //inserisco dentro un array string le playlist
        int size = MusicLibrary.playList.size();
        final String[] namePL = new String[size];
        final Long[] idPL = new Long[size];
        for(int i=0; i<size; i++) {
            namePL[i] = MusicLibrary.playList.get(i).getNamePlaylist(); //nomi PL
            idPL[i] = MusicLibrary.playList.get(i).getPlaylistID(); //id PL
        }

        final AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
        final View addView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.alert_add_playlist, null);
        ListView lv = addView.findViewById(R.id.list_playlist);

        a_builder.setView(addView);
        a_builder.setTitle("Aggiungi a Playlist");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, namePL);
        lv.setAdapter(adapter);
        lv.setClickable(true);

        a_builder.setNegativeButton("Annulla",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setCancelable(true);
        final AlertDialog alert = a_builder.create();
        alert.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.v(Long.toString(song.getID()),Long.toString(idPL[position]));
                MusicLibrary.addTrackToPlaylist(context, song.getID(), idPL[position], namePL[position]);
                alert.dismiss();
                Toast.makeText(context, "Aggiunto a playlist!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void shareIt(String mypath) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("audio/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///"+mypath));
        context_songAdpter.startActivity(Intent.createChooser(sharingIntent, "Condividi con"));
    }

    public static boolean checkPermissionRington(Song song){
        boolean retVal = true;
        if (Settings.System.canWrite(context_songAdpter)) {
            setRington(song);
        }
        else {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context_songAdpter.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context_songAdpter.startActivity(intent);
            Toast.makeText(context_songAdpter, "Abilita permessi e potrai impostare la tua suoneria!", Toast.LENGTH_LONG).show();

        }
        return retVal;
    }

    public static void setRington(Song song){
        File ringtoneFile = new File(song.getPath()); //path song

        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA,ringtoneFile.getAbsolutePath());
        content.put(MediaStore.MediaColumns.TITLE, song.getTitle());
        content.put(MediaStore.MediaColumns.SIZE, 215454);
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        content.put(MediaStore.Audio.Media.ARTIST, song.getArtist());
        content.put(MediaStore.Audio.Media.DURATION, 230);
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        content.put(MediaStore.Audio.Media.IS_ALARM, false);
        content.put(MediaStore.Audio.Media.IS_MUSIC, false);

        //Insert it into the database
        Log.i("TAG", "the absolute path of the file is :"+ ringtoneFile.getAbsolutePath());
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());

        context_songAdpter.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"",
                null);
        Uri newUri = context_songAdpter.getContentResolver().insert(uri, content);
        System.out.println("uri=="+uri);
        Log.i("TAG","the ringtone uri is :"+newUri);
        RingtoneManager.setActualDefaultRingtoneUri(
                context_songAdpter.getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                newUri);
        Toast.makeText(context_songAdpter, "Suoneria impostata!", Toast.LENGTH_LONG).show();

    }

    public static void dialog_det(Context context, Song song){

        //dettagli tecnici
        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(song.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mf = mex.getTrackFormat(0);
        int bitRate  = mf.getInteger(MediaFormat.KEY_BIT_RATE);
        int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
                a_builder.setMessage(
                        "Percorso file: "+ song.getPath()
                        + "\n\nTitolo brano: "+ song.getTitle()
                        + "\n\nNome artista: " + song.getArtist()
                        + "\n\nDimensione file: "+ Math.floor(Integer.parseInt(song.getSize())/1024/1024) + " MB"
                        + "\n\nDurata brano: "+ Utility.convertDuration(song.getDuration())
                        + "\n\nAnno brano: " +song.getYear()
                        + "\n\nBitrate: " +bitRate/1000 + " kb/s"
                        + "\n\nFrequenza di campionamento:\n" +sampleRate + " Hz")
                .setCancelable(false)
                .setNegativeButton("Chiudi",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Dettagli Brano");
        alert.show();
    }

    //delete song
    public static void delete_song(final Context context, final Song song){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
        a_builder.setMessage("Confermi di voler cancellare il brano?")
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete
                MusicLibrary.deleteSong(context, song.getPath());
                //clear array
                MusicLibrary.songList.clear();
                MusicLibrary.albumList.clear();
                MusicLibrary.artistList.clear();
                MusicLibrary.playListTrack.clear();
                MusicLibrary.playList.clear();
                //get song
                MusicLibrary.getSongList();
                MusicLibrary.getAlbumsLists();
                MusicLibrary.getArtistsLists();
                MusicLibrary.getPlaylists();
                //refresh fragments
                FragmentActivity activity = (FragmentActivity) context;
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction tr = manager.beginTransaction();
                tr.detach(parent.bf);
                tr.detach(parent.alf);
                tr.detach(parent.arf);
                tr.detach(parent.pf);
                tr.attach(parent.bf);
                tr.attach(parent.alf);
                tr.attach(parent.arf);
                tr.attach(parent.pf);
                tr.commit();
                Toast.makeText(context.getApplicationContext(), "Brano eliminato!", Toast.LENGTH_LONG).show();
            }
        })
                .setNegativeButton("Annulla",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false);
        AlertDialog alert = a_builder.create();
        alert.setTitle("Elimina Brano");
        alert.show();
    }

    //delete song in playlist (predisposta ma non implementata)
    public void delete_song_playlist(final Context context, final Playlist playlist, final PlaylistTrack playlistTrack){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
        a_builder.setMessage("Confermi di voler cancellare il brano all'interno della playlist " +playlistTrack.getTitlePL()+"?")
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //delete
                        MusicLibrary.deletePlaylistSingleTrack(context, playlist.getPlaylistID(), playlistTrack.getAudio_id());
                        //clear array
                        MusicLibrary.playListTrack.clear();
                        //get song
                        MusicLibrary.getPlaylistTracks(context,playlist.getPlaylistID());
                        //refresh fragments
                        FragmentActivity activity = (FragmentActivity) context;
                        FragmentManager manager = activity.getSupportFragmentManager();
                        FragmentTransaction tr = manager.beginTransaction();
                        tr.detach(parent.psf);
                        tr.attach(parent.psf);
                        tr.commit();
                        Toast.makeText(context.getApplicationContext(), "Brano eliminato dalla playlist!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Annulla",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false);
        AlertDialog alert = a_builder.create();
        alert.setTitle("Elimina Brano Playlist");
        alert.show();
    }

    //Compressione immagini
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
