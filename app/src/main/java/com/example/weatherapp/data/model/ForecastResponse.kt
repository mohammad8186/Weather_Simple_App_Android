package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: City
)

data class ForecastItem(
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("main") val main: ForecastMain,
    @SerializedName("weather") val weather: List<ForecastWeather>,
    @SerializedName("dt_txt") val dt_txt: String
)

data class ForecastMain(
    @SerializedName("temp") val temp: Double,
    @SerializedName("temp_min") val temp_min: Double,
    @SerializedName("temp_max") val temp_max: Double,
    @SerializedName("humidity") val humidity: Int
)

data class ForecastWeather(
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String
)

data class City(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String
) 