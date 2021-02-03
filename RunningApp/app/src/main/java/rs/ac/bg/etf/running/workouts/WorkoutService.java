package rs.ac.bg.etf.running.workouts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;

public class WorkoutService extends Service {

    public static final String INTENT_ACTION_START = "rs.ac.bg.etf.running.workouts.START";
    public static final String INTENT_ACTION_POWER = "rs.ac.bg.etf.running.workouts.POWER";

    private static final String NOTIFICATION_CHANNEL_ID = "workout-notification-channel";
    private static final int NOTIFICATION_ID = 1;

    private Timer timer = new Timer();

    private boolean serviceStarted = false;

    private int motivationMessageIndex = 0;
    private final AtomicReference<String> motivationMessage = new AtomicReference<>(null);

    private void scheduleTimer() {
        if(motivationMessage.get() == null) {
            String[] motivationMessages =
                getResources().getStringArray(R.array.workout_toast_motivation);
            motivationMessage.set(motivationMessages[0]);
            motivationMessageIndex = (motivationMessageIndex + 1) % motivationMessages.length;
        }

        serviceStarted = true;

        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> Toast.makeText(
                        WorkoutService.this,
                        getResources().getStringArray(
                                R.array.workout_toast_motivation)[motivationMessageIndex],
                        Toast.LENGTH_SHORT).show()
                );
            }
        }, 0, 7000);
    }

    public void changeMotivationMessage() {
        String[] motivationMessages =
                getResources().getStringArray(R.array.workout_toast_motivation);
        motivationMessage.set(motivationMessages[motivationMessageIndex]);
        motivationMessageIndex = (motivationMessageIndex + 1) % motivationMessages.length;

        Toast.makeText(this, "changeMotivationMessage", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreate() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onStartCommand()");

        createNotificationChannel();
        this.startForeground(NOTIFICATION_ID, getNotification());

        switch(intent.getAction())
        {
            case INTENT_ACTION_START:
                if(!serviceStarted) {
                    scheduleTimer();
                }
                break;
            case INTENT_ACTION_POWER:
                if(serviceStarted) {
                    changeMotivationMessage();
                }
                break;
        }


        return START_STICKY;
    }

    public class PrimitiveHandler extends Handler {
        public static final int CHANGE_MOTIVATION_MESSAGE = 1;

        public PrimitiveHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case CHANGE_MOTIVATION_MESSAGE:
                    changeMotivationMessage();
                    break;
                default: super.handleMessage(msg);
            }
        }
    }

    public class AidlBinder extends WorkoutServiceInterface.Stub {

        @Override
        public void changeMotivationMessage() throws RemoteException {
            WorkoutService.this.changeMotivationMessage();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onBind()");
        return new AidlBinder();
    }

    @Override
    public void onDestroy() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onDestroy()");
        super.onDestroy();

        if(serviceStarted) {
            timer.cancel();
        }
    }

    private void createNotificationChannel() {
        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
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