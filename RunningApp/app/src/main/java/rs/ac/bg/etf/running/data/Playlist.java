package rs.ac.bg.etf.running.data;

import java.util.List;

public class Playlist {
    private String title;
    private List<Audio> audioList;

    public Playlist() {

    }

    public Playlist(String name) {
        title = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Audio> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
    }
}
