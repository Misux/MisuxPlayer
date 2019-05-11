package com.example.francesco.masterplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    public Song(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    private long id;
    private String title;
    private String artist;
    private long duration;
    private String path;
    private long idAlbum;
    private String track;
    private String size;
    private String year;

    public Song(long songID, String songTitle, String songArtist, long songDuration,String songPath,
                long songIdAlbum, String songTrack, String songSize, String songYear) {
        this.id=songID;
        this.title=songTitle;
        this.artist=songArtist;
        this.duration = songDuration;
        this.path = songPath;
        this.idAlbum = songIdAlbum;
        this.track = songTrack;
        this.size = songSize;
        this.year = songYear;
    }

    public long getID() {return id;}
    public String getTitle() {
        return title;
    }
    public String getArtist() {return artist;}
    public long getDuration() {return duration;}
    public String getPath() { return path; }
    public long getIdAlbum() {return idAlbum; }
    public String getTrack() {return track;}
    public String getSize() {return size;}
    public String getYear() {return year;}


    public void readFromParcel(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.artist = in.readString();
        this.duration = in.readLong();
        this.path = in.readString();
        this.idAlbum = in.readLong();
        this.track = in.readString();
        this.size = in.readString();
        this.year = in.readString();
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeLong(this.duration);
        dest.writeString(this.path);
        dest.writeLong(this.idAlbum);
        dest.writeString(this.track);
        dest.writeString(this.size);
        dest.writeString(this.year);
    }
}
