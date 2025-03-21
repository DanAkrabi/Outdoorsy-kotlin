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
import com.example.outdoorsy.BuildConfig
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
        val apiKey = BuildConfig.weather_key  // Your actual API key

        weatherViewModel.getWeather(cityName, apiKey)

        weatherViewModel.weather.observe(viewLifecycleOwner, Observer { weather ->
            weather?.let {
                val temperatureInCelsius = it.main.temp - 273.15
                binding.textViewTemperature.text = "Temperature: ${String.format("%.2f", temperatureInCelsius)}°C"
                binding.textViewDescription.text = "Description: ${it.weather[0].description}"

                val iconCode = it.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                Glide.with(this)
                    .load(iconUrl)
                    .placeholder(R.drawable.ic_weather_placeholder)
                    .error(R.drawable.ic_weather_placeholder)
                    .into(binding.weatherIcon)
            }
        })

        binding.refreshWeatherButton.setOnClickListener {
            weatherViewModel.getWeather(cityName, apiKey)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
