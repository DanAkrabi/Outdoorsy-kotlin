package com.example.outdoorsy.repository

import com.example.outdoorsy.data.api.RetrofitInstance
import com.example.outdoorsy.data.api.WeatherApiService
import com.example.outdoorsy.model.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApiService: WeatherApiService) {


suspend fun getWeather(city: String, apiKey: String): WeatherModel {
    return weatherApiService.getWeather(city, apiKey) // Directly return WeatherModel
}

}
