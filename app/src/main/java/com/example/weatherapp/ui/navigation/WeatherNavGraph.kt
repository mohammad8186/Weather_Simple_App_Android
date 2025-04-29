package com.example.weatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.ui.screens.CitySelectionScreen
import com.example.weatherapp.ui.screens.HomeScreen
import com.example.weatherapp.ui.screens.ProvinceSelectionScreen
import com.example.weatherapp.ui.screens.WeatherScreen

@Composable
fun WeatherNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        
        composable("province") {
            ProvinceSelectionScreen(navController = navController)
        }
        
        composable(
            route = "city/{province}",
            arguments = listOf(
                navArgument("province") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val province = backStackEntry.arguments?.getString("province") ?: ""
            CitySelectionScreen(
                province = province,
                navController = navController
            )
        }
        
        composable(
            route = "weather/{city}",
            arguments = listOf(
                navArgument("city") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city") ?: ""
            WeatherScreen(city = city)
        }
    }
} 