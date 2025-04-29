package com.example.weatherapp.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.utils.getCurrentLocation
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.ui.components.WeatherCard
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(navController: NavController, viewModel: WeatherViewModel = viewModel()) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A237E), 
            Color(0xFF303F9F),
            Color(0xFF3F51B5),
            Color(0xFF5C6BC0)
        )
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with App Logo and Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.size(120.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WbSunny,
                                contentDescription = "لوگوی برنامه",
                                tint = Color.White,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "هواشناسی ایران",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Text(
                        text = "اطلاعات دقیق آب و هوا در لحظه",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // دکمه‌های اصلی با طراحی جدید
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    // کارت دکمه انتخاب استان و شهر
                    MainActionCard(
                        icon = Icons.Rounded.Search,
                        title = "انتخاب استان و شهر",
                        subtitle = "جستجوی وضعیت آب و هوا در شهرهای ایران",
                        backgroundColor = Color.White.copy(alpha = 0.15f),
                        iconColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        onClick = { navController.navigate("province") }
                    )

                    // کارت دکمه نمایش وضعیت هوای موقعیت فعلی
                    MainActionCard(
                        icon = Icons.Rounded.LocationOn,
                        title = "نمایش وضعیت هوای موقعیت فعلی",
                        subtitle = "با استفاده از GPS موقعیت شما",
                        backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        iconColor = Color(0xFF4CAF50).copy(alpha = 0.3f),
                        onClick = {
                            // قبل از درخواست موقعیت، حالت لودینگ را فعال می‌کنیم
                            viewModel.isLoading = true
                            
                            Toast.makeText(context, "در حال دریافت موقعیت شما...", Toast.LENGTH_SHORT).show()
                            
                            getCurrentLocation(
                                context = context,
                                onLocationReceived = { location ->
                                println("مختصات: ${location.latitude}, ${location.longitude}")
                                    // اینجا مختصات دریافت شده است
                                    Toast.makeText(
                                        context, 
                                        "موقعیت دریافت شد: ${location.latitude}, ${location.longitude}", 
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    
                                    // لود کردن آب و هوا
                                viewModel.loadWeatherByCoordinates(location.latitude, location.longitude)
                                    println("درخواست آب و هوا برای موقعیت فعلی ارسال شد")
                                },
                                onError = { errorMessage ->
                                    // در صورت خطا در دریافت موقعیت
                                    viewModel.isLoading = false
                                    viewModel.errorMessage = errorMessage
                                    Toast.makeText(
                                        context,
                                        "خطا: $errorMessage",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    )
                }

                // بخش نمایش اطلاعات، خطا و لودینگ
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isLoading) {
                        LoadingCard()
                    } else if (viewModel.errorMessage != null) {
                        ErrorCard(viewModel.errorMessage!!)
                    } else if (viewModel.weatherData != null) {
                        WeatherCard(data = viewModel.weatherData!!)
                    } else {
                        StartInfoCard()
                    }
                }
                
                // اضافه کردن فضای خالی در انتها برای اطمینان از اسکرول کامل
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun MainActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
                
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun LoadingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(56.dp),
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "در حال دریافت اطلاعات آب و هوا...",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(id = android.R.drawable.ic_dialog_alert),
                contentDescription = "خطا",
                tint = Color(0xFFF44336),
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "خطا در دریافت اطلاعات",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFD32F2F)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StartInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Rounded.WbSunny,
                contentDescription = "آب و هوا",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "به اپلیکیشن هواشناسی خوش آمدید",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "لطفاً یک شهر انتخاب کنید یا از موقعیت فعلی خود استفاده کنید تا وضعیت آب و هوا را مشاهده نمایید.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}
