package rs.ac.bg.etf.running.workouts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Location;

@AndroidEntryPoint
public class WorkoutService extends LifecycleService {

    public static final String INTENT_ACTION_START = "rs.ac.bg.etf.running.workouts.START";
    public static final String INTENT_ACTION_POWER = "rs.ac.bg.etf.running.workouts.POWER";

    private static final String NOTIFICATION_CHANNEL_ID = "workout-notification-channel";
    private static final int NOTIFICATION_ID = 1;

    private boolean firstTime = true;
    private NotificationCompat.Builder builder;
    private String elapsedTime = "";
    private Timer timer;

    private boolean serviceStarted = false;

    @Inject
    public LifecycleAwareMeasurer measurer;

    @Inject
    public LifecycleAwareLocator locator;

    @Inject
    public LifecycleAwareStepCounter counter;

    @Override
    public void onCreate() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onCreate()");
        super.onCreate();

        getLifecycle().addObserver(measurer);
        getLifecycle().addObserver(locator);
        getLifecycle().addObserver(counter);


        timer = new Timer();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
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
                    measurer.start(this);
                    locator.startFollowing(this);
                    counter.start(this);
                }
                break;
            case INTENT_ACTION_POWER:
                break;
        }

        Handler handler = new Handler(Looper.getMainLooper());

        long startTimestamp = new Date().getTime();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long elapsed = new Date().getTime() - startTimestamp;

                int seconds = (int) ((elapsed / 1000) % 60);
                int minutes = (int) ((elapsed / (1000 * 60)) % 60);
                int hours = (int) ((elapsed / (1000 * 60 * 60)) % 60);

                StringBuilder workoutDuration = new StringBuilder();
                workoutDuration.append(String.format("%02d", hours)).append(":");
                workoutDuration.append(String.format("%02d", minutes)).append(":");
                workoutDuration.append(String.format("%02d", seconds));

                elapsedTime = workoutDuration.toString();

                NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, getNotification());
            }
        }, 0, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onBind()");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onDestroy() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onDestroy()");

        List<Location> coordinates =  locator.stopFollowing();
        int steps = counter.stop();

        Gson gson = new Gson();
        String data = gson.toJson(coordinates);

        SharedPreferences sharedPreferences = getSharedPreferences(WorkoutStartFragment.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putInt(WorkoutStartFragment.STEP_NUMBER, steps)
                .putString(WorkoutStartFragment.RUNNING_PATH, data)
                .commit();

        timer.cancel();

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

        if(firstTime) {
            firstTime = false;
            builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_directions_run_black_24)
                    .setContentTitle(getString(R.string.workout_notification_content_title))
                    .setContentText(getString(R.string.workout_notification_content_text))
                    .setColorized(true)
                    .setColor(ContextCompat.getColor(this, R.color.teal_200))
                    .setContentIntent(pendingIntent);
        }
        else {
            builder
                    .setContentText("Workout time: " + elapsedTime + " | Steps: " + counter.get());
        }

        return builder.build();
    }
}