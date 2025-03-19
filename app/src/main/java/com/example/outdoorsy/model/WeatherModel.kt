package com.example.outdoorsy.model
data class WeatherModel(
    val weather: List<WeatherDescription>,
    val main: MainWeather,
    val name: String
)

data class WeatherDescription(
    val description: String,
    val icon: String
)

data class MainWeather(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)


