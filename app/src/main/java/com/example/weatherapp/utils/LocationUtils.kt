package com.example.weatherapp.utils

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale


fun getCurrentLocation(
    context: Context,
    onLocationReceived: (Location) -> Unit,
    onError: (String) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
        onError("لطفا دسترسی لوکیشن را فعال کنید")
        return
    }

    // First try to get last location
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationReceived(location)
            } else {
                // If last location is null, try to get current location
                val cancellationToken = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
                    .addOnSuccessListener { currentLocation: Location? ->
                        currentLocation?.let {
                            onLocationReceived(it)
                        } ?: run {
                            onError("موقعیت شما یافت نشد")
                        }

                    }
                    .addOnFailureListener {
                        onError("خطا در دریافت موقعیت")
                    }

            }
        }
        .addOnFailureListener {
            onError("خطا در دریافت موقعیت")
        }
}

fun getCityFromLocation(context: Context, location: Location, onCityReceived: (String?) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // استفاده از روش جدید برای اندروید 13 و بالاتر
        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
            val cityName = addresses.firstOrNull()?.locality
            onCityReceived(cityName)
        }
    } else {
        // استفاده از روش قدیمی برای نسخه‌های پایین‌تر
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val cityName = addresses?.firstOrNull()?.locality
        onCityReceived(cityName)
    }
}
