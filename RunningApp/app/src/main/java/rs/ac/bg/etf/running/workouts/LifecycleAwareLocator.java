package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.rest.CurrentWeatherModel;
import rs.ac.bg.etf.running.rest.OpenWeatherMapService;

import static rs.ac.bg.etf.running.MainActivity.LOG_TAG;
import static rs.ac.bg.etf.running.notifications.NotificationBroadcast.ALARM_NOTIFICATION_ID;

public class LifecycleAwareLocator implements DefaultLifecycleObserver {

    private List<Location> coordinates = new ArrayList<>();

    Timer timer;

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

                    if (location != null) {
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

    public void startFollowing(Context context) {
        SharedPreferences sp = context.getSharedPreferences(WorkoutStartFragment.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        FusedLocationProviderClient locationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);

        CancellationTokenSource token = new CancellationTokenSource();

        timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void run() {

                handler.post(() -> {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(LOG_TAG, "permission not granted");
                        timer.cancel();
                        return;
                    }

                    locationProviderClient
                            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token.getToken())
                            .addOnSuccessListener(location -> {

                                if (location != null) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();

//                                    if(coordinates.size() > 0) {
//                                        if (latitude != coordinates.get(coordinates.size() - 1).getLatitude()
//                                                || longitude != coordinates.get(coordinates.size() - 1).getLongitude()) {
//
                                            coordinates.add(new Location(latitude, longitude));
//                                        }
//                                    }

                                    Gson gson = new Gson();
                                    String data = gson.toJson(coordinates);
                                    sp.edit()
                                            .putString(WorkoutStartFragment.RUNNING_PATH, data)
                                            .commit();

                                    Log.d(LOG_TAG, "lat: " + latitude + ", long: " + longitude);
                                }
                            });
                });

            }
        }, 0, 1000);
    }

    public List<Location> stopFollowing() {
        timer.cancel();
        return coordinates;
    }
}
