package com.example.weatherapp.data.model

data class WeatherData(
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rain: String,
    val aqi: Int
)
