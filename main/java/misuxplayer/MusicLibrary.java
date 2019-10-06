package com.example.francesco.masterplayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.francesco.masterplayer.model.Album;
import com.example.francesco.masterplayer.model.Artist;
import com.example.francesco.masterplayer.model.Playlist;
import com.example.francesco.masterplayer.model.PlaylistTrack;
import com.example.francesco.masterplayer.model.Song;

import java.util.ArrayList;

public class MusicLibrary {

    public static ArrayList<Song> songList = new ArrayList<Song>();
    public static ArrayList <Album> albumList = new ArrayList<Album>();
    public static ArrayList <Artist> artistList = new ArrayList<Artist>();
    public static ArrayList <Playlist> playList = new ArrayList<Playlist>();
    public static ArrayList<PlaylistTrack> playListTrack = new ArrayList<PlaylistTrack>();
    private static final String TAG = "MusicLibrary";
    private static Context context;
    private MainActivity parent;

    public MusicLibrary(Context context, MainActivity parent){
        this.context = context;
        this.parent = parent;
        getSongList();
        getAlbumsLists();
        getArtistsLists();
        getPlaylists();
    }

    /* ----------------------------------------------- SONGS --------------------------------------------------*/
    public static Boolean getSongList() {
        Log.v("getSongList = ", TAG);
        boolean no_song = true;

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Media._ID;
        final String title_song = MediaStore.Audio.Media.TITLE;
        final String artist = MediaStore.Audio.Media.ARTIST;
        final String duration = MediaStore.Audio.Media.DURATION;
        final String path = android.provider.MediaStore.Audio.Media.DATA;
        final String albumId = MediaStore.Audio.Media.ALBUM_ID;
        final String numTrack = MediaStore.Audio.Media.TRACK;
        final String size = MediaStore.Audio.Media.SIZE;
        final String composer = MediaStore.Audio.Media.COMPOSER;
        final String year = MediaStore.Audio.Media.YEAR;

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sort=null;
        //ordinamento
        String where = Integer.toString(MainActivity.order);
        switch (where) {
            case "1":
                sort = MediaStore.Audio.Media.ALBUM;
                break;
            case "2":
                sort = MediaStore.Audio.Media.TITLE;
                break;
            case "3":
                sort = MediaStore.Audio.Media.ARTIST;
                break;
            case "4":
                sort = MediaStore.Audio.Media.DURATION;
                break;
            case "5":
                sort = MediaStore.Audio.Media.YEAR;
                break;
            case "6":
                sort = MediaStore.Audio.Media.TRACK;
                break;
        }

        final String[] columns = { _id, title_song, artist, duration, path, albumId, numTrack, size, year };
        Cursor cursor = context.getContentResolver().query(uri, columns, selection, null, sort);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    long id = cursor.getLong(cursor.getColumnIndex(_id));
                    String title = cursor.getString(cursor.getColumnIndex(title_song));
                    String artist2 = cursor.getString(cursor.getColumnIndex(artist));
                    long duration2 = cursor.getLong(cursor.getColumnIndex(duration));
                    String thisPath = cursor.getString(cursor.getColumnIndex(path)); //path per il play
                    long thisAlbumId = cursor.getLong(cursor.getColumnIndex(albumId));
                    String track = cursor.getString(cursor.getColumnIndex(numTrack));
                    String dim = cursor.getString(cursor.getColumnIndex(size));
                    String yearS = cursor.getString(cursor.getColumnIndex(year));

                    songList.add(new Song(id, title, artist2, duration2, thisPath, thisAlbumId, track, dim, yearS));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            cursor.close();
        }

        if (songList.size() != 0) no_song = false;
        return no_song;
    }

    public static void deleteSong(Context context, String path) {
        Uri rootUri = MediaStore.Audio.Media.getContentUriForPath(path);
        context.getContentResolver().delete(rootUri, MediaStore.MediaColumns.DATA + "=?", new String[]{path});
    }

    /* ----------------------------------------------- ALBUM --------------------------------------------------_*/

    public static void getAlbumsLists(){
        Log.v("getAlbumsList = ", TAG);

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM; //name
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        final String year = MediaStore.Audio.Albums.FIRST_YEAR;


        final String[] columns = { _id, album_name, artist, albumart, tracks, year };
        Cursor cursor = context.getContentResolver().query(uri, columns, null, null, album_name);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    long id = cursor.getLong(cursor.getColumnIndex(_id));
                    String name = cursor.getString(cursor.getColumnIndex(album_name));
                    String artist2 = cursor.getString(cursor.getColumnIndex(artist));
                    String artPath = cursor.getString(cursor.getColumnIndex(albumart));
                    int nr = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));
                    String yearAlbum = cursor.getString(cursor.getColumnIndex(year));

                    albumList.add(new Album(id, name, artist2, artPath, nr, yearAlbum));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            cursor.close();
        }
    }

    /* ----------------------------------------------- ARTIST --------------------------------------------------*/

    public static void getArtistsLists() {
        Log.v("getArtistLists = ", TAG);

        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Artists._ID;
        final String artist_name = MediaStore.Audio.Artists.ARTIST; //name
        final String num_albums = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
        final String tracks = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;

        final String[] columns = { _id, artist_name, num_albums, tracks };
        Cursor cursor = context.getContentResolver().query(uri, columns, null, null, artist_name);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    long id = cursor.getLong(cursor.getColumnIndex(_id));
                    String artistName = cursor.getString(cursor.getColumnIndex(artist_name));
                    int numAlbum = Integer.parseInt(cursor.getString(cursor.getColumnIndex(num_albums)));
                    int numBrani = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));

                    artistList.add(new Artist(id, artistName, numAlbum, numBrani));

                } while (cursor.moveToNext());
            }

        } catch(Exception e){
            cursor.close();}
    }

    /* -------------------------------------------- PLAYLIST --------------------------------------------------*/

    public static void getPlaylists(){
        Log.v("getNamePlaylist = ", TAG);

        final Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        final String _id = MediaStore.Audio.Playlists._ID;
        final String playlist_name = MediaStore.Audio.Playlists.NAME; //name playlist

        final String[] columns = { _id, playlist_name};
        Cursor cursor = context.getContentResolver().query(uri, columns, null, null, playlist_name);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    long id = cursor.getLong(cursor.getColumnIndex(_id));
                    String playlistName = cursor.getString(cursor.getColumnIndex(playlist_name));
                    playList.add(new Playlist(id, playlistName));

                } while (cursor.moveToNext());
            }
        } catch(Exception e) {
            cursor.close();
        }
    }

    //aggiungi
    public static boolean addPlaylist(String pname) {

        Uri playlists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(playlists, new String[] { "*" }, null, null, null);
        long playlistId = 0;
        try{
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String plname = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
                    if (plname.equalsIgnoreCase(pname)) {
                        playlistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch(Exception e) {
            cursor.close();
        }

        if (playlistId != 0) {
            Uri deleteUri = ContentUris.withAppendedId(playlists, playlistId);
            Log.d(TAG, "REMOVING Existing Playlist: " + playlistId);
            // delete the playlist
            context.getContentResolver().delete(deleteUri, null, null);
        }

        Log.d(TAG, "CREATING PLAYLIST: " + pname);
        ContentValues v1 = new ContentValues();
        v1.put(MediaStore.Audio.Playlists.NAME, pname);
        v1.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
        Uri newpl = context.getContentResolver().insert(playlists, v1);
        Log.d(TAG, "Added PlayLIst: " + newpl);
        Boolean flag=true;
        return flag;
    }

    //cancella
    public static void deletePlaylist(Context context, long playlist_id) {
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {Long.toString(playlist_id)};
        context.getContentResolver().delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
    }

    //svuota
    public static void deletePlaylistAllTracks(Context context, long playlist_id) {
        try {
            final ContentResolver resolver = context.getContentResolver();
            final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
            String where = MediaStore.Audio.Playlists._ID + "=?";
            String[] whereVal = {Long.toString(playlist_id)};
            resolver.delete(uri, where, whereVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //rinomina
    public static void renamePlaylist(Context context, String newplaylist, long playlist_id) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        String where = MediaStore.Audio.Playlists._ID + " =? ";
        String[] whereVal = { Long.toString(playlist_id) };
        values.put(MediaStore.Audio.Playlists.NAME, newplaylist);
        resolver.update(uri, values, where, whereVal);
    }

    /* ----------------------------------------- PLAYLIST TRACK ------------------------------------------------*/

    public static void getPlaylistTracks(Context context, Long playlist_id) {
        Log.v("getPlaylistSongs = ", TAG);

        final Uri newuri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
        final String audio_id = MediaStore.Audio.Playlists.Members.AUDIO_ID;
        String pl_title = MediaStore.Audio.Playlists.Members.TITLE;

        String[] columns = {audio_id, pl_title};
        Cursor cursor = context.getContentResolver().query(newuri, columns, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    long audioID = cursor.getLong(cursor.getColumnIndex(audio_id));
                    String title = cursor.getString(cursor.getColumnIndex(pl_title));
                    playListTrack.add(new PlaylistTrack(audioID, title));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            cursor.close();
        }
    }

    public static void addTrackToPlaylist(final Context context, long audio_id, long playlist_id, String name_track) {
        Uri newuri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audio_id);
        values.put(MediaStore.Audio.Playlists.Members.TITLE, name_track);
        resolver.insert(newuri, values);
        resolver.notifyChange(Uri.parse("content://media"), null);
    }


    public static int deletePlaylistSingleTrack(Context context, long playlistId, long audioId) {
        ContentResolver resolver = context.getContentResolver();
        int countDel = 0;
        try {
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(
                    "external", playlistId);
            String where = MediaStore.Audio.Playlists.Members._ID + "=?" ; // my mistake was I used .AUDIO_ID here

            String audioId1 = Long.toString(audioId);
            String[] whereVal = { audioId1 };
            countDel=resolver.delete(uri, where,whereVal);
            Log.d("TAG", "tracks deleted=" + countDel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countDel;
    }
}
