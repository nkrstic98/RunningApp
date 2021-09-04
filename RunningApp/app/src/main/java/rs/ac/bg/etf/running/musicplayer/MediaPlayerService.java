package rs.ac.bg.etf.running.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.util.ArrayList;

import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Audio;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutStartBinding;
import rs.ac.bg.etf.running.workouts.WorkoutStartFragment;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener
{

    private MediaPlayer mediaPlayer;
    //path to the audio file
    private String mediaFile;
    //Used to pause/resume MediaPlayer
    private int resumePosition;

    private AudioManager audioManager;

    //List of available audio files
    private ArrayList<Audio> audioList;
    private int audioIndex = -1;
    private Audio activeAudio; //currently playing

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeAudio.getData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate() {
        super.onCreate();

        //Manage incoming calls during playback
        //Pause MediaPlayer on incoming call
        //Resume on hangup
        callStateListener();

        registerBecomingNoisyListener();

        register_playNewAudio();
    }

    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            StorageUtil storageUtil = new StorageUtil(getApplicationContext());
            audioList = storageUtil.loadAudio();
            audioIndex = storageUtil.loadAudioindex();

            if(audioIndex != -1 && audioIndex < audioList.size()) {
                activeAudio = audioList.get(audioIndex);
            }
            else {
                stopSelf();
            }
        }
        catch (NullPointerException e) {
            stopSelf();
        }

        if(requestAudioFocus() == false) {
            stopSelf();
        }

        if(mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            createNotificationChannel();
            buildNotification(PlaybackStatus.PLAYING);
        }

        handleIncomingAction(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }

        removeAudioFocus();

        if(phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        unregisterReceiver(playNewAudio);
        unregisterReceiver(becomingNoisyReceiver);

        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        //Invoked when playback of a media source has completed.
//        stopMedia();
//        //stop the service
//        stopSelf();
        skipToNext();
    }

    //Handle errors
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info.
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
        resumePosition = mp.getCurrentPosition();
        resumeMedia();
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    //-----------------------------------------------------------------------------------
    //BROADCAST RECEIVERS

    //Noisy Receiver
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyListener() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    //Handling incoming calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    private void callStateListener() {
        //get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state)
                {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if(mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        if(mediaPlayer != null) {
                            if(ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    //Play new audio Broadcast receiver
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            audioIndex = new StorageUtil(getApplicationContext()).loadAudioindex();
            if(audioIndex != -1 && audioIndex < audioList.size()) {
                activeAudio = audioList.get(audioIndex);
            }
            else {
                stopSelf();
            }

            //reset mediaPlayer for new Audio
            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void register_playNewAudio() {
        IntentFilter intentFilter = new IntentFilter(WorkoutStartFragment.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, intentFilter);
    }

    //---------------------------------------------------------------------------------------
    //Working with media sessions

    public static final String ACTION_PLAY = "rs.etf.pmu.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "rs.etf.pmu.musicplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "rs.etf.pmu.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "rs.etf.pmu.musicplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "rs.etf.pmu.musicplayer.ACTION_STOP";

    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    private static final String NOTIFICATION_CHANNEL_ID = "music-notification-channel";
    private static final int NOTIFICATION_ID = 101;

    private void initMediaSession() throws RemoteException {
        if(mediaSessionManager != null) return;

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        //Create new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //Set MediaSession -> ready to receive media commands
        //through its MediaSessionCompat.Callback
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        updateMetaData();

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                resumePosition = (int) pos;
                mediaPlayer.seekTo(resumePosition);
            }


        });
    }

    public void skipToNext() {
        if(audioIndex == audioList.size() - 1) {
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        }
        else {
            activeAudio = audioList.get(++audioIndex);
        }

        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        mediaPlayer.reset();
        initMediaPlayer();
    }

    public void skipToPrevious() {
        if(audioIndex == 0) {
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        }
        else {
            activeAudio = audioList.get(--audioIndex);
        }

        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(), R.drawable.image1);

        mediaSession.setMetadata(
                new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                    .build()
        );
    }

    //-----------------------------------------------------
    //Building notifications

    private void createNotificationChannel() {
        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName("Music player notifications")
                .build();

        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
    }

    private void buildNotification(PlaybackStatus playbackStatus) {
        int notificationAction = android.R.drawable.ic_media_pause;

        PendingIntent play_pauseAction = null;

        if(playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            play_pauseAction = playbackAction(1);
        }
        else if(playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.image1);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        mediaSession.setMetadata(
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadata.METADATA_KEY_TITLE, activeAudio.getTitle())
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, activeAudio.getArtist())
                        .putString(MediaMetadata.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                        .putLong(MediaMetadata.METADATA_KEY_DURATION, activeAudio.getDuration())
                        .build()
        );

        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                .setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        mediaPlayer.getCurrentPosition(),
                        1
                )
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
        );

        builder
                .setShowWhen(false)
                .setStyle(
                        new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(
                                    mediaSession.getSessionToken()
                            )
                            .setShowActionsInCompactView(0, 1, 2)
                )
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
//                .setContentText(activeAudio.getAlbum())
//                .setContentTitle(activeAudio.getTitle())
//                .setContentInfo(activeAudio.getArtist())
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2))
                .setProgress(0, 0, true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(play_pauseAction);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notification);

//        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
//                .notify(NOTIFICATION_ID, notification);
    }

    private void removeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);

        switch (actionNumber)
        {
            case 0:
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
        }
        return null;
    }

    //-------------------------------------------------------------------------------------

    private void handleIncomingAction(Intent playbackAction) {
        if(playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if(actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if(actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if(actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if(actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if(actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }
}
