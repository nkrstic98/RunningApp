package rs.ac.bg.etf.running.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApi {

    @GET("weather")
    Call<CurrentWeatherModel> getCurrentWeather(
            @Query("appid") String apiKey,
            @Query("lat")double latitude,
            @Query("lon")double longitude,
            @Query("units")String units
    );
}
