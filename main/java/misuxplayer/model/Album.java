package com.example.francesco.masterplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Album implements Parcelable {

    public Album(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    private long id;
    private String albumName;
    private String artistName;
    private String path;
    private int nr_of_songs;
    private String year;

    public Album(long id, String albumName, String artistName,  String path, int nr_of_songs, String year) {
        this.id = id;
        this.albumName = albumName;
        this.artistName = artistName;
        this.path = path;
        this.nr_of_songs = nr_of_songs;
        this.year = year;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public void setNr_of_songs(int nr_of_songs) {
        this.nr_of_songs = nr_of_songs;
    }
    public void setYear (String year) { this.year = year; }

    public long getID(){
        return id;
    }
    public String getAlbumName(){
        return albumName;
    }
    public String getArtistName() {
        return artistName;
    }
    public String getPath() {return path; }
    public int getNr_of_songs() {
        return nr_of_songs;
    }
    public String getYear() { return year; }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.albumName = in.readString();
        this.artistName = in.readString();
        this.nr_of_songs = in.readInt();
        this.path = in.readString();
        this. year = in.readString();
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.albumName);
        dest.writeString(this.artistName);
        dest.writeInt(this.nr_of_songs);
        dest.writeString(this.path);
        dest.writeString(this.albumName);
        dest.writeString(this.year);
    }
}