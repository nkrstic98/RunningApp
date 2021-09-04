package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.rest.CurrentWeatherModel;
import rs.ac.bg.etf.running.rest.OpenWeatherMapService;

import static rs.ac.bg.etf.running.MainActivity.LOG_TAG;
import static rs.ac.bg.etf.running.notifications.NotificationBroadcast.ALARM_NOTIFICATION_ID;

public class LifecycleAwareLocator implements DefaultLifecycleObserver {

    @Inject
    public LifecycleAwareLocator() {
    }

    public void getLocation(Context context) {
        FusedLocationProviderClient locationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);

        CancellationTokenSource token = new CancellationTokenSource();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "permission not granted");
            return;
        }

        locationProviderClient
                .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token.getToken())
                .addOnSuccessListener(location -> {

                    if(location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        Log.d(LOG_TAG, "lat: " + latitude + ", long: " + longitude);

                        OpenWeatherMapService openWeatherMapService = new OpenWeatherMapService();
                        openWeatherMapService.getCurrentWeather(context, longitude, latitude);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(LOG_TAG, e.getMessage());
                });
    }
}
