package com.example.outdoorsy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.databinding.FragmentWeatherBinding
import com.example.outdoorsy.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityName = "Tel Aviv"  // Use your desired city
        val apiKey = "dc20172f8d61a2ab56998e21a59ca110"  // Your actual API key

        // Fetch weather data initially when the fragment is created
        weatherViewModel.getWeather(cityName, apiKey)

        // Observe the weather data
        weatherViewModel.weather.observe(viewLifecycleOwner, Observer { weather ->
            weather?.let {
                // Update the UI with weather information
                val temperatureInCelsius = it.main.temp - 273.15
                binding.textViewTemperature.text = "Temperature: ${String.format("%.2f", temperatureInCelsius)}°C"
                binding.textViewDescription.text = "Description: ${it.weather[0].description}"

                // Update weather icon
                val iconCode = it.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                Glide.with(this)
                    .load(iconUrl)
                    .placeholder(R.drawable.ic_weather_placeholder)
                    .error(R.drawable.ic_weather_placeholder)
                    .into(binding.weatherIcon)
            }
        })

        // Set up the Refresh Weather button to fetch new weather data
        binding.refreshWeatherButton.setOnClickListener {
            // Fetch weather data again on button click
            weatherViewModel.getWeather(cityName, apiKey)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
