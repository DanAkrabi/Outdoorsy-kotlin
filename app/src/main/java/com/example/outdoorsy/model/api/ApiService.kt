package com.example.outdoorsy.data.api

import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.model.WeatherModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {


//    @GET("weather")
//    suspend fun getWeather(@Query("q") city: String): WeatherModel
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("apiKey") apiKey: String
    ): WeatherModel
}
