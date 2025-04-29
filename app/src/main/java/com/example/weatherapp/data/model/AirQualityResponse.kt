package com.example.weatherapp.data.model

data class AirQualityResponse(
    val list: List<AirData>
)

data class AirData(
    val main: AQIMain,
    val components: Components
)

data class AQIMain(val aqi: Int)

data class Components(
    val pm2_5: Float
)
