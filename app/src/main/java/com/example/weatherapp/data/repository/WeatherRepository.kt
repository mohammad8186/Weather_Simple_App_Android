package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.model.WeatherInfo
import com.example.weatherapp.model.ForecastDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
import kotlin.math.roundToInt

class WeatherRepository {
    companion object {
        private const val TAG = "WeatherRepository"
    }

    private val api = RetrofitClient.instance
    private val apiKey = "4b777ef458fff0971aa6b19979acf57d" // ← کلیدت اینجا بذار

    suspend fun getWeather(city: String): WeatherResponse = withContext(Dispatchers.IO) {
        try {
            // ابتدا سعی می‌کنیم مستقیماً با نام شهر اطلاعات را دریافت کنیم
            val weather = api.getWeatherByCity(city, apiKey)
            try {
                // سپس کیفیت هوا را اضافه می‌کنیم
                val airQuality = api.getAirQuality(weather.coord.lat, weather.coord.lon, apiKey)
                val first = airQuality.list.firstOrNull()
                weather.aqi = first?.main?.aqi ?: 0
                weather.pm2_5 = first?.components?.pm2_5
            } catch (e: Exception) {
                // اگر کیفیت هوا دریافت نشد، همچنان اطلاعات آب و هوا را نمایش می‌دهیم
                weather.aqi = 0
            }
            return@withContext weather
        } catch (e: UnknownHostException) {
            // خطای عدم اتصال به اینترنت
            throw Exception("لطفاً اتصال اینترنت خود را بررسی کنید.")
        } catch (e: Exception) {
            try {
                // روش دوم: ابتدا مختصات جغرافیایی را پیدا می‌کنیم
                val geoList = api.getCoordinatesByCityName(city, apiKey = apiKey)
                if (geoList.isNotEmpty()) {
                    val lat = geoList[0].lat
                    val lon = geoList[0].lon
                    return@withContext getWeatherByCoordinates(lat, lon)
                } else {
                    throw Exception("شهر $city در سیستم هواشناسی یافت نشد. لطفاً نام شهر دیگری را انتخاب کنید.")
                }
            } catch (e2: UnknownHostException) {
                throw Exception("لطفاً اتصال اینترنت خود را بررسی کنید.")
            } catch (e2: Exception) {
                // اگر خطای دیگری رخ داد
                throw Exception("خطا در دریافت اطلاعات هواشناسی: ${e2.message ?: "دلیل نامشخص"}")
            }
        }
    }

    suspend fun getAQIByLocation(lat: Double, lon: Double): Int = withContext(Dispatchers.IO) {
        try {
            val response = api.getAirQuality(lat, lon, apiKey)
            return@withContext response.list.firstOrNull()?.main?.aqi ?: 0
        } catch (e: Exception) {
            // اگر نتوانستیم کیفیت هوا را دریافت کنیم، مقدار پیش‌فرض 0 برمی‌گردانیم
            return@withContext 0
        }
    }

    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): WeatherResponse = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "درخواست دریافت آب و هوا برای مختصات: lat=$lat, lon=$lon")
            val weather = api.getWeatherByCoordinates(lat, lon, apiKey = apiKey)
            Log.d(TAG, "دریافت آب و هوا موفقیت‌آمیز: ${weather.name}, دما: ${weather.main.temp}")
            
            try {
                Log.d(TAG, "درخواست دریافت کیفیت هوا برای مختصات: lat=$lat, lon=$lon")
                val airQuality = api.getAirQuality(lat, lon, apiKey)
                val first = airQuality.list.firstOrNull()
                weather.aqi = first?.main?.aqi ?: 0
                weather.pm2_5 = first?.components?.pm2_5
                Log.d(TAG, "دریافت کیفیت هوا موفقیت‌آمیز: AQI=${weather.aqi}, PM2.5=${weather.pm2_5}")
            } catch (e: Exception) {
                // اگر کیفیت هوا دریافت نشد، همچنان اطلاعات آب و هوا را نمایش می‌دهیم
                Log.e(TAG, "خطا در دریافت کیفیت هوا: ${e.message}", e)
                weather.aqi = 0
            }
            
            return@withContext weather
        } catch (e: UnknownHostException) {
            Log.e(TAG, "خطای اتصال به اینترنت", e)
            throw Exception("لطفاً اتصال اینترنت خود را بررسی کنید.")
        } catch (e: HttpException) {
            val errorCode = e.code()
            Log.e(TAG, "خطای HTTP در دریافت آب و هوا: $errorCode", e)
            throw Exception("خطای سرور ${errorCode}: لطفاً بعداً مجدداً تلاش کنید.")
        } catch (e: Exception) {
            Log.e(TAG, "خطا در دریافت اطلاعات هواشناسی: ${e.message}", e)
            throw Exception("خطا در دریافت اطلاعات هواشناسی برای موقعیت فعلی: ${e.message ?: "دلیل نامشخص"}")
        }
    }

    suspend fun getWeatherSafe(city: String): WeatherResponse = withContext(Dispatchers.IO) {
        try {
            val geo = api.getCoordinatesByCityName(city, apiKey = apiKey).firstOrNull()
                ?: throw Exception("شهر $city در سیستم هواشناسی یافت نشد.")

            val lat = geo.lat
            val lon = geo.lon

            val weather = api.getWeatherByCoordinates(lat, lon, apiKey = apiKey)
            
            try {
                val airQuality = api.getAirQuality(lat, lon, apiKey)
                val first = airQuality.list.firstOrNull()
                weather.aqi = first?.main?.aqi ?: 0
                weather.pm2_5 = first?.components?.pm2_5
            } catch (e: Exception) {
                // اگر کیفیت هوا دریافت نشد، مقدار پیش‌فرض را قرار می‌دهیم
                weather.aqi = 0
            }

            return@withContext weather
        } catch (e: UnknownHostException) {
            throw Exception("لطفاً اتصال اینترنت خود را بررسی کنید.")
        } catch (e: Exception) {
            throw Exception("خطا در دریافت اطلاعات هواشناسی: ${e.message ?: "دلیل نامشخص"}")
        }
    }

    suspend fun getForecast(city: String): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            // Get coordinates for the city
            val geoList = api.getCoordinatesByCityName(city, apiKey = apiKey)
            if (geoList.isEmpty()) {
                throw Exception("شهر $city در سیستم هواشناسی یافت نشد. لطفاً نام شهر دیگری را انتخاب کنید.")
            }
            
            val lat = geoList[0].lat
            val lon = geoList[0].lon
            
            // Get current weather for basic info
            val currentWeather = api.getWeatherByCoordinates(lat, lon, apiKey = apiKey)
            
            // Get forecast data
            val forecastResponse = api.getForecast(city = city, apiKey = apiKey)
            
            // Process forecast data
            val forecastDays = processForecastData(forecastResponse)
            
            // Create and return WeatherInfo object
            return@withContext WeatherInfo(
                city = currentWeather.name,
                province = forecastResponse.city.country ?: "",
                currentTemp = currentWeather.main.temp.roundToInt(),
                feelsLike = currentWeather.main.feels_like.roundToInt(),
                minTemp = currentWeather.main.temp_min.roundToInt(),
                maxTemp = currentWeather.main.temp_max.roundToInt(),
                condition = currentWeather.weather.firstOrNull()?.description ?: "",
                humidity = currentWeather.main.humidity,
                windSpeed = currentWeather.wind.speed.roundToInt(),
                visibility = (currentWeather.visibility / 1000), // Convert to km
                forecast = forecastDays
            )
        } catch (e: UnknownHostException) {
            throw Exception("لطفاً اتصال اینترنت خود را بررسی کنید.")
        } catch (e: Exception) {
            throw Exception("خطا در دریافت اطلاعات پیش‌بینی هوا: ${e.message ?: "دلیل نامشخص"}")
        }
    }
    
    private fun processForecastData(forecastResponse: ForecastResponse): List<ForecastDay> {
        // Group forecast items by day
        val groupedByDay = forecastResponse.list.groupBy { item ->
            // Extract date from dt_txt (format: "yyyy-MM-dd HH:mm:ss")
            item.dt_txt.split(" ")[0]
        }
        
        // Create a day format for display
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE", Locale("fa", "IR")) // Persian day names
        
        return groupedByDay.map { (date, items) ->
            // Find min and max temperatures for the day
            val minTemp = items.minOfOrNull { it.main.temp_min }?.roundToInt() ?: 0
            val maxTemp = items.maxOfOrNull { it.main.temp_max }?.roundToInt() ?: 0
            
            // Get most common weather condition for the day
            val conditions = items.groupBy { it.weather.firstOrNull()?.description ?: "" }
            val mostCommonCondition = conditions.maxByOrNull { it.value.size }?.key ?: ""
            
            // Format the day name
            val dayName = try {
                val parsedDate = inputFormat.parse(date)
                if (parsedDate != null) {
                    outputFormat.format(parsedDate)
                } else {
                    date
                }
            } catch (e: Exception) {
                date
            }
            
            ForecastDay(
                day = dayName,
                condition = mostCommonCondition,
                minTemp = minTemp,
                maxTemp = maxTemp
            )
        }
    }
}
