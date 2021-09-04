package rs.ac.bg.etf.running.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.workouts.LifecycleAwareLocator;

@AndroidEntryPoint
public class NotificationBroadcast extends BroadcastReceiver {
    public static final String ALARM_NOTIFICATION_ID = "notification-alarm";

    @Inject
    public LifecycleAwareLocator locator;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, NotificationBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
        );

        Log.d(MainActivity.LOG_TAG, "Log received");

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isLocationEnabled()) {
            locator.getLocation(context);
        }
        else {
            createUserNotification(context);
        }
    }

    private void createUserNotification(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String []array = user.getDisplayName().split(" ");

        StringBuilder text = new StringBuilder("Hey ");
        text.append(array[0]);
        text.append(", it's time for your next run!");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ALARM_NOTIFICATION_ID)
                .setSmallIcon(R.drawable.baseline_directions_run_black_48)
                .setContentTitle("Ready for a run?")
                .setContentText(text.toString())
                .setColor(context.getResources().getColor(R.color.teal_200))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200, builder.build());
    }
}
