package com.example.francesco.masterplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {

    public Artist(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    private long id;
    private String artistName;
    private int nr_of_albums;
    private int nr_of_songs;

    public Artist(long id, String artistName, int nr_of_albums, int nr_of_songs) {
        this.id = id;
        this.artistName = artistName;
        this.nr_of_albums = nr_of_albums;
        this.nr_of_songs = nr_of_songs;
    }

    public long getID(){
        return id;
    }
    public String getNameArtist(){
        return artistName;
    }
    public int getNumAlbum() {
        return nr_of_albums;
    }
    public int getNumBrani() {
        return nr_of_songs;
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.artistName = in.readString();
        this.nr_of_albums = in.readInt();
        this.nr_of_songs = in.readInt();
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.artistName);
        dest.writeInt(this.nr_of_albums);
        dest.writeInt(this.nr_of_songs);
    }
}
