package com.example.outdoorsy.data.api

import com.example.outdoorsy.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,           // שם העיר
        @Query("appid") apiKey: String      // ה-API Key שלך
    ): WeatherModel  // מחזיר את המודל של תחזית מזג האוויר
}
