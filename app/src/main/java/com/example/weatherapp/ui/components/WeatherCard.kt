package com.example.weatherapp.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.WeatherResponse

private const val TAG = "WeatherCard"

@Composable
fun WeatherCard(data: WeatherResponse) {
    val (aqiDescription, aqiDetails, aqiColor) = when (data.aqi) {
        1 -> Triple("خیلی خوب", "هوا پاک است. مناسب برای همه. 🌿", Color(0xFF4CAF50))
        2 -> Triple("خوب", "هوا سالم است. بدون مشکل برای بیشتر افراد. 😊", Color(0xFF8BC34A))
        3 -> Triple("متوسط", "ممکن است برای افراد حساس آزاردهنده باشد. 😐", Color(0xFFFFC107))
        4 -> Triple("ناسالم", "برای گروه‌های حساس خطرناک است. 😷", Color(0xFFFF9800))
        5 -> Triple("خیلی ناسالم", "برای همه خطرناک است. توصیه می‌شود در خانه بمانید. ☠️", Color(0xFFF44336))
        else -> Triple("نامشخص", "اطلاعاتی موجود نیست.", Color(0xFF9E9E9E))
    }

    // لاگ کردن اطلاعات وضعیت هوا برای دیباگ
    val weatherMain = data.weather.firstOrNull()?.main ?: ""
    val weatherDescription = data.weather.firstOrNull()?.description ?: ""
    
    Log.d(TAG, "Weather data - Main: '$weatherMain', Description: '$weatherDescription'")
    
    // تعیین آیکون با تطبیق دقیق‌تر
    val weatherIcon = remember(weatherMain) {
        val mainLowerCase = weatherMain.lowercase().trim()
        Log.d(TAG, "Weather main (lowercase): '$mainLowerCase'")
        
        when {
            mainLowerCase.contains("clear") -> Icons.Rounded.WbSunny
            mainLowerCase.contains("cloud") -> Icons.Rounded.Cloud
            mainLowerCase.contains("rain") -> Icons.Rounded.WaterDrop
            mainLowerCase.contains("drizzle") -> Icons.Rounded.Grain
            mainLowerCase.contains("thunder") -> Icons.Rounded.Thunderstorm
            mainLowerCase.contains("snow") -> Icons.Rounded.AcUnit
            mainLowerCase.contains("mist") -> Icons.Rounded.Waves
            mainLowerCase.contains("fog") -> Icons.Rounded.Cloud
            mainLowerCase.contains("haze") -> Icons.Rounded.Cloud
            mainLowerCase.contains("smoke") -> Icons.Rounded.Cloud
            mainLowerCase.contains("dust") -> Icons.Rounded.Air
            mainLowerCase.contains("sand") -> Icons.Rounded.Landscape
            mainLowerCase.contains("ash") -> Icons.Rounded.Cloud
            mainLowerCase.contains("squall") -> Icons.Rounded.Air
            mainLowerCase.contains("tornado") -> Icons.Rounded.Cyclone
            else -> {
                Log.d(TAG, "No matching icon found for weather main: '$mainLowerCase', defaulting to WbSunny")
                Icons.Rounded.WbSunny
            }
        }
    }

    fun getWeatherDescription(desc: String?): String {
        return when (desc?.lowercase()?.trim()) {
            "clear sky" -> "آسمان صاف"
            "few clouds" -> "کمی ابری"
            "scattered clouds" -> "نیمه ابری"
            "broken clouds" -> "ابری"
            "shower rain" -> "رگبار باران"
            "rain" -> "بارانی"
            "thunderstorm" -> "رعد و برق"
            "snow" -> "برفی"
            "mist" -> "مه آلود"
            "overcast clouds" -> "کاملاً ابری"
            "light rain" -> "باران سبک"
            "moderate rain" -> "باران متوسط"
            "heavy intensity rain" -> "باران شدید"
            "very heavy rain" -> "باران خیلی شدید"
            "extreme rain" -> "باران بسیار شدید"
            "freezing rain" -> "باران یخ زده"
            "light intensity shower rain" -> "رگبار باران سبک"
            "heavy intensity shower rain" -> "رگبار باران شدید"
            "ragged shower rain" -> "رگبار نامنظم"
            "light snow" -> "برف سبک"
            "heavy snow" -> "برف سنگین"
            "sleet" -> "تگرگ و برف"
            "light shower sleet" -> "رگبار سبک تگرگ و برف"
            "shower sleet" -> "رگبار تگرگ و برف"
            "light rain and snow" -> "باران و برف سبک"
            "rain and snow" -> "باران و برف"
            "light shower snow" -> "رگبار برف سبک"
            "shower snow" -> "رگبار برف"
            "heavy shower snow" -> "رگبار برف سنگین"
            "fog" -> "مه غلیظ"
            "haze" -> "غبار"
            "smoke" -> "دود"
            "dust" -> "گرد و خاک"
            "sand" -> "گرد و خاک"
            "ash" -> "خاکستر"
            "squall" -> "تندباد"
            "tornado" -> "گردباد"
            else -> {
                Log.d(TAG, "No matching description found for: '$desc', using original value")
                desc ?: "نامشخص"
            }
        }
    }

    // گرادیان با رنگ‌های زیباتر
    val cardGradient = remember(weatherMain) {
        val mainLowerCase = weatherMain.lowercase().trim()
        when {
            mainLowerCase.contains("clear") -> Brush.verticalGradient(colors = listOf(Color(0xFF1E88E5), Color(0xFF64B5F6)))  // آبی روشن
            mainLowerCase.contains("cloud") -> Brush.verticalGradient(colors = listOf(Color(0xFF546E7A), Color(0xFF78909C)))  // خاکستری آبی
            mainLowerCase.contains("rain") || mainLowerCase.contains("drizzle") -> 
                Brush.verticalGradient(colors = listOf(Color(0xFF0277BD), Color(0xFF0288D1)))  // آبی تیره
            mainLowerCase.contains("thunder") -> 
                Brush.verticalGradient(colors = listOf(Color(0xFF303F9F), Color(0xFF3949AB)))  // آبی بنفش
            mainLowerCase.contains("snow") -> 
                Brush.verticalGradient(colors = listOf(Color(0xFF42A5F5), Color(0xFF90CAF9)))  // آبی روشن براق
            mainLowerCase.contains("mist") || mainLowerCase.contains("fog") || mainLowerCase.contains("haze") -> 
                Brush.verticalGradient(colors = listOf(Color(0xFF78909C), Color(0xFFB0BEC5)))  // خاکستری
            mainLowerCase.contains("dust") || mainLowerCase.contains("sand") || mainLowerCase.contains("ash") -> 
                Brush.verticalGradient(colors = listOf(Color(0xFFBCAAA4), Color(0xFFD7CCC8)))  // قهوه‌ای روشن
            else -> Brush.verticalGradient(colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6)))  // پیش‌فرض
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Header section with weather icon and temperature
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(brush = cardGradient)
                        .padding(vertical = 32.dp, horizontal = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = weatherIcon,
                            contentDescription = "آیکون وضعیت هوا",
                            tint = Color.White,
                            modifier = Modifier.size(100.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "${data.main.temp.toInt()}°C",
                            color = Color.White,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Card(
                            modifier = Modifier
                                .padding(8.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = getWeatherDescription(data.weather.firstOrNull()?.description),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                // Details section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "جزئیات آب و هوا",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF0D47A1),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    DetailRow(
                        icon = Icons.Rounded.Thermostat,
                        title = "دما",
                        value = "${data.main.temp.toInt()}°C",
                        iconTint = Color(0xFFE91E63)
                    )
                    
                    DetailRow(
                        icon = Icons.Rounded.WaterDrop,
                        title = "رطوبت",
                        value = "${data.main.humidity}%",
                        iconTint = Color(0xFF03A9F4)
                    )
                    
                    DetailRow(
                        icon = Icons.Rounded.Air,
                        title = "سرعت باد",
                        value = "${data.wind.speed} ثانیه/متر",
                        iconTint = Color(0xFF00BCD4)
                    )
                    
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp
                    )
                    
                    Text(
                        text = "کیفیت هوا",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF0D47A1),
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    
                    AqiCard(
                        aqiValue = data.aqi,
                        aqiDescription = aqiDescription,
                        aqiColor = aqiColor
                    )
                    
                    data.pm2_5?.let {
                        DetailRow(
                            icon = Icons.Rounded.BlurOn,
                            title = "ذرات معلق",
                            value = "${"%.1f".format(it)} μg/m³",
                            iconTint = Color(0xFF9C27B0)
                        )
                    }
                    
                    // AQI Details
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Text(
                            text = aqiDetails,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF424242),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AqiCard(aqiValue: Int, aqiDescription: String, aqiColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = aqiColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = aqiColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$aqiValue",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "شاخص کیفیت هوا",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = aqiDescription,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = aqiColor
            )
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    title: String,
    value: String,
    iconTint: Color = Color(0xFF1976D2)
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
        ) {
            Card(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = iconTint.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color(0xFF0D47A1)
                )
            }
        }
    }
}
