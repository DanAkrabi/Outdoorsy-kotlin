package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.WeatherModel
import com.example.outdoorsy.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _weather = MutableLiveData<WeatherModel?>()
    val weather: MutableLiveData<WeatherModel?> get() = _weather

    // Function to fetch weather data
    fun getWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                // Fetch weather data from the repository
                val weatherData = weatherRepository.getWeather(city, apiKey)
                // Post the fetched data to LiveData
                _weather.postValue(weatherData)
            } catch (e: Exception) {
                // Handle any exceptions
                _weather.postValue(null)
            }
        }
    }
}

