package com.example.weatherapp.data.model

data class GeocodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)