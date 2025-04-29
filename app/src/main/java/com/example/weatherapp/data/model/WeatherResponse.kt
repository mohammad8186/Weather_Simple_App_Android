package com.example.weatherapp.data.model
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main") val main: MainData,
    @SerializedName("wind") val wind: WindData,
    @SerializedName("weather") val weather: List<WeatherDesc>,
    @SerializedName("coord") val coord: Coord,
    @SerializedName("name") val name: String,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("aqi") var aqi: Int,
    var pm2_5: Float? = null
)

data class MainData(
    val temp: Double,
    val humidity: Int,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double
)

data class WindData(
    val speed: Double
)

data class WeatherDesc(
    val main: String,
    val description: String
)

data class Coord(
    val lat: Double,
    val lon: Double
)
