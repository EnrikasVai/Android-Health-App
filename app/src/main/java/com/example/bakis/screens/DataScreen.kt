package com.example.bakis.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.consumables.CustomBottomNavigationBar
import com.example.bakis.consumables.CustomTopAppBar
import com.example.bakis.viewmodel.HomeViewModel


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DataScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Steps Data",
                onEditClick = { /* ... */ },
                showEditIcon = false // Only show the edit icon in the ProfileScreen
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Build, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF262626)) // Set the background color here
                .padding(top = 40.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {
                BarChart()
            }
        }
    }
}

@Composable
fun BarChart() {
    val barChartData = listOf(3000f, 5000f, 2500f, 4000f, 1500f, 5500f, 2000f) // Your data goes here
    val maxValue = barChartData.maxOrNull() ?: 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            barChartData.forEachIndexed { index, value ->
                val barHeight = (value / maxValue) * size.height
                drawRoundRect(
                    color = Color.Red,
                    topLeft = Offset(
                        x = (index * (size.width / barChartData.size)).toFloat(),
                        y = size.height - barHeight
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = (size.width / barChartData.size) * 0.5f,
                        height = barHeight
                    ),
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
                )
            }
        }
    }
}