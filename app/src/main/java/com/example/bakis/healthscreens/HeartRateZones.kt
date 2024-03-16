package com.example.bakis.healthscreens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar

@Composable
fun HeartRateZones(navController: NavController){
    val items = listOf("Dashboard", "Health", "Me")
    val icons = listOf(Icons.Default.Build, Icons.Default.Favorite, Icons.Default.Person)
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Health Rate Zones"
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = items,
                icons = icons
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item{
                Text(text = "Display information on different heart rate zones (e.g., resting, fat burn, cardio, peak) and the time spent in each zone during workouts.", color = Color.White)
            }

        }
    }
}