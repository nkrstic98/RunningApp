package rs.ac.bg.etf.running.musicplayer;

import java.io.Serializable;

public class Audio implements Serializable {
    private String data;
    private String title;
    private String album;
    private String artist;
    private int duration;

    public Audio() {

    }

    public Audio(String data, String title, String album, String artist, int duration) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
