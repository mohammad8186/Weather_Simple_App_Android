package com.example.weatherapp.model

data class WeatherInfo(
    val city: String,
    val province: String,
    val currentTemp: Int,
    val feelsLike: Int,
    val minTemp: Int,
    val maxTemp: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Int,
    val visibility: Int,
    val forecast: List<ForecastDay>
)

data class ForecastDay(
    val day: String,
    val condition: String,
    val minTemp: Int,
    val maxTemp: Int
) 