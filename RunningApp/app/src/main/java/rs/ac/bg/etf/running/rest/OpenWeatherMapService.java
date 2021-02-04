package rs.ac.bg.etf.running.rest;

import android.Manifest;
import android.os.HardwarePropertiesManager;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rs.ac.bg.etf.running.MainActivity;

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
    }

    public void getCurrentWeather(double longitude, double latitude) {
        Call<CurrentWeatherModel> call =
                openWeatherMapApi.getCurrentWeather(API_KEY, longitude, latitude, "metric");

        call.enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
                if(response.isSuccessful()) {
                    CurrentWeatherModel currentWeatherModel = response.body();
                    Log.d(MainActivity.LOG_TAG, currentWeatherModel.toString());
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {

            }
        });
    }
}
