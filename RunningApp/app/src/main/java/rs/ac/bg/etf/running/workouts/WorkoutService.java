package rs.ac.bg.etf.running.workouts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;

@AndroidEntryPoint
public class WorkoutService extends LifecycleService {

    public static final String INTENT_ACTION_START = "rs.ac.bg.etf.running.workouts.START";
    public static final String INTENT_ACTION_POWER = "rs.ac.bg.etf.running.workouts.POWER";

    private static final String NOTIFICATION_CHANNEL_ID = "workout-notification-channel";
    private static final int NOTIFICATION_ID = 1;

    private boolean serviceStarted = false;

    @Inject
    public LifecycleAwareMotivator motivator;

    @Inject
    public LifecycleAwarePlayer player;

    @Override
    public void onCreate() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onCreate()");
        super.onCreate();

        getLifecycle().addObserver(motivator);
        getLifecycle().addObserver(player);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onStartCommand()");

        createNotificationChannel();
        this.startForeground(NOTIFICATION_ID, getNotification());

        switch(intent.getAction())
        {
            case INTENT_ACTION_START:
                if(!serviceStarted) {
                    serviceStarted = true;
                    motivator.start(this);
                    player.start(this);
                }
                break;
            case INTENT_ACTION_POWER:
                if(serviceStarted) {
                    motivator.changeMessage(this);
                }
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onBind()");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onDestroy()");
        super.onDestroy();
    }

    private void createNotificationChannel() {
        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(getString(R.string.workout_notification_channel_name))
                .build();

        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
    }

    private Notification getNotification() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setAction(MainActivity.INTENT_ACTION_NOTIFICATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_directions_run_black_24)
                .setContentTitle(getString(R.string.workout_notification_content_title))
                .setContentText(getString(R.string.workout_notification_content_text))
                .setColorized(true)
                .setColor(ContextCompat.getColor(this, R.color.teal_200))
                .setContentIntent(pendingIntent)
                .build();
    }
}