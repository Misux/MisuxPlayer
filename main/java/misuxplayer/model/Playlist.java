package com.example.francesco.masterplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable {

    public Playlist(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Playlist createFromParcel(Parcel in) {return new Playlist(in);}
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    private long id;
    private String namePlaylist;

    public Playlist(long playlistID, String namePlaylist) {
        this.id = playlistID;
        this.namePlaylist = namePlaylist;
    }

    public long getPlaylistID() {
        return id;
    }
    public String getNamePlaylist() {return namePlaylist;}

    private void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.namePlaylist = in.readString();
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.namePlaylist);
    }
}

