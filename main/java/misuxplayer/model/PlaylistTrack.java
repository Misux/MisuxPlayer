package com.example.francesco.masterplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaylistTrack implements Parcelable {

    public PlaylistTrack(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Creator CREATOR = new Creator() {
        public PlaylistTrack createFromParcel(Parcel in) {return new PlaylistTrack(in);}
        public PlaylistTrack[] newArray(int size) {
            return new PlaylistTrack[size];
        }
    };

    private long id_audio;
    private String title;

    public PlaylistTrack(long playlistID, String title) {
        this.id_audio = playlistID;
        this.title = title;
    }

    public long getAudio_id() {return id_audio;}
    public String getTitlePL() {return title;}

    public void readFromParcel(Parcel in) {
        this.id_audio = in.readLong();
        this.title = in.readString();
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id_audio);
        dest.writeString(this.title);
    }
}

