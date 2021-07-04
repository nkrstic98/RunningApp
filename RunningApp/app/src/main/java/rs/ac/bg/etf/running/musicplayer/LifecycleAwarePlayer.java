package rs.ac.bg.etf.running.musicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class LifecycleAwarePlayer implements DefaultLifecycleObserver {

    private MediaPlayer mediaPlayer = null;
    private AudioManager audioManager;
    private List<Audio> audioList;
    private Audio activeAudio;

    private int audioIndex = -1;
    private int resumePosition;

    @Inject
    public LifecycleAwarePlayer() {

    }

    public void start(Context context) {
        if(mediaPlayer == null) {
            try {
                String song = "Megamix Band - Ljubavnik i Drug.mp3";
                String path = context.getFilesDir().getAbsolutePath() + File.separator + song;

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
