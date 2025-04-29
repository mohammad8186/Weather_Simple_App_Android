package com.example.weatherapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    companion object {
        private const val TAG = "WeatherViewModel"
    }

    var weatherData by mutableStateOf<WeatherResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadWeather(city: String) {
        if (city.isBlank()) {
            errorMessage = "لطفاً نام شهر را وارد کنید"
            return
        }
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                Log.d(TAG, "درخواست دریافت آب و هوا برای شهر: $city")
                val weather = repository.getWeather(city)
                weatherData = weather
                Log.d(TAG, "اطلاعات آب و هوا برای $city دریافت شد")
            } catch (e: UnknownHostException) {
                Log.e(TAG, "خطای اتصال به اینترنت: ${e.message}", e)
                errorMessage = "لطفاً اتصال اینترنت خود را بررسی کنید"
                weatherData = null
            } catch (e: Exception) {
                Log.e(TAG, "خطا در دریافت اطلاعات آب و هوا: ${e.message}", e)
                errorMessage = e.message ?: "خطا در دریافت اطلاعات آب و هوا"
                weatherData = null
            } finally {
                isLoading = false
            }
        }
    }

    fun loadWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                Log.d(TAG, "درخواست دریافت آب و هوا برای مختصات: lat=$lat, lon=$lon")
                val weather = repository.getWeatherByCoordinates(lat, lon)
                
                // بررسی کامل بودن داده‌های دریافتی
                if (weather.weather.isNullOrEmpty()) {
                    Log.e(TAG, "داده‌های آب و هوا ناقص دریافت شد")
                    throw Exception("داده‌های آب و هوا ناقص دریافت شد")
                }
                
                // اطلاعات مهم را لاگ می‌کنیم
                Log.d(TAG, "داده‌های آب و هوا دریافت شد:")
                Log.d(TAG, "شهر: ${weather.name}")
                Log.d(TAG, "دما: ${weather.main.temp}")
                Log.d(TAG, "وضعیت: ${weather.weather.firstOrNull()?.description}")
                Log.d(TAG, "رطوبت: ${weather.main.humidity}")
                Log.d(TAG, "سرعت باد: ${weather.wind.speed}")
                Log.d(TAG, "کیفیت هوا: ${weather.aqi}")
                
                weatherData = weather
            } catch (e: UnknownHostException) {
                Log.e(TAG, "خطای اتصال به اینترنت: ${e.message}", e)
                errorMessage = "لطفاً اتصال اینترنت خود را بررسی کنید"
                weatherData = null
            } catch (e: Exception) {
                Log.e(TAG, "خطا در دریافت اطلاعات آب و هوا: ${e.message}", e)
                errorMessage = e.message ?: "خطا در دریافت اطلاعات آب و هوای موقعیت فعلی"
                weatherData = null
            } finally {
                isLoading = false
            }
        }
    }

    // پاک کردن داده‌ها هنگام تغییر مسیر
    fun clearWeatherData() {
        weatherData = null
        errorMessage = null
    }
}
