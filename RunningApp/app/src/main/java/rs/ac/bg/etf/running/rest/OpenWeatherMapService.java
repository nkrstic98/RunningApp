package rs.ac.bg.etf.running.rest;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.HardwarePropertiesManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;

import static rs.ac.bg.etf.running.MainActivity.LOG_TAG;
import static rs.ac.bg.etf.running.notifications.NotificationBroadcast.ALARM_NOTIFICATION_ID;

public class OpenWeatherMapService {
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String API_KEY = "c11ce0b6196622ceaf3868ac00d06919";

    private OpenWeatherMapApi openWeatherMapApi;

    public OpenWeatherMapService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(loggingInterceptor);

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        openWeatherMapApi = retrofit.create(OpenWeatherMapApi.class);


        Log.d(LOG_TAG, "Weather constructor");
    }

    public void getCurrentWeather(Context context, double longitude, double latitude) {
        Call<CurrentWeatherModel> call =
                openWeatherMapApi.getCurrentWeather(API_KEY, latitude, longitude, "metric");

        call.enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
                if(response.isSuccessful()) {
                    CurrentWeatherModel currentWeatherModel = response.body();
                    Log.d(MainActivity.LOG_TAG, currentWeatherModel.toString());

                    createUserNotification(context, currentWeatherModel);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {

            }
        });
    }

    private void createUserNotification(Context context, CurrentWeatherModel weather) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String []array = user.getDisplayName().split(" ");

        StringBuilder text = new StringBuilder("Hey ");
        text.append(array[0]);
        text.append(", it's time for your next run!");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ALARM_NOTIFICATION_ID)
                .setSmallIcon(R.drawable.baseline_directions_run_black_48)
                .setContentTitle("Ready for a run?")
                .setContentText(text.toString())
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(text.toString())
                        .addLine("Current weather in " + weather.name + ", " + weather.sys.country + ": " + weather.weather.get(0).description.toUpperCase())
                        .addLine("Temperature: " + weather.main.temp + "°C")
                        .addLine("Feels like: " + weather.main.feels_like + "°C")
                )
                .setColor(context.getResources().getColor(R.color.teal_200))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200, builder.build());
    }
}
