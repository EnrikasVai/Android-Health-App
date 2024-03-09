package com.example.bakis.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bakis.consumables.CustomBottomNavigationBar
import com.example.bakis.consumables.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(navController: NavHostController, onNavigate: (String) -> Unit) {
    val items = listOf("Dashboard", "Health", "Me")
    val icons = listOf(Icons.Default.Build, Icons.Default.Favorite, Icons.Default.Person)

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Health Screen"
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF262626))
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                // Replace these colors with your theme colors
                val colors = listOf(Color.Green, Color.Blue, Color.Magenta, Color.Cyan)
                val texts = listOf("HEALTH RISK CHECK", "SPORTS EVALUATION", "EXERCISE TRAINING", "DNA TESTING")
                val routes = listOf("screen1", "screen2", "screen3", "dnaTesting")

                for (i in texts.indices) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .width(300.dp)
                                .height(100.dp)
                                .clickable { onNavigate(routes[i]) }
                                .background(colors[i])
                                .clip(RoundedCornerShape(12.dp)),
                            ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = texts[i],
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center) // Explicitly align text to center
                                )
                            }
                        }
                    }
                }

        }
    }
}

