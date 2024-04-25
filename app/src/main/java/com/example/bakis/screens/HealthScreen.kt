package com.example.bakis.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bakis.R
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar


data class HealthScreenData(
    val iconId: Int,
    val text: String,
    val color1: Long,
    val color2: Long,
    val rout: String
)

@SuppressLint("SuspiciousIndentation")
@Composable
fun HealthScreen(navController: NavHostController, onNavigate: (String) -> Unit) {
    val items = listOf("Dashboard", "Health", "Me")
    val icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
    val data = listOf(
        HealthScreenData(R.drawable.heartratezones,"Heart Rate Vitals", 0xFFFF7518, 0xFFFF3131, "heartRateZones"),
        HealthScreenData(R.drawable.exercise,"Activity Tracking", 0xFFC492E6, 0xFF8A2BE2, "exerciseTracking"),
        HealthScreenData(R.drawable.nutrition,"Nutritional Tracking", 0xFFD147AB, 0xFFF8BBD0, "nutritionalTracking")
    )
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF262626)),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(data.size) { index ->
                        NavButton(data = data[index], onNavigate = onNavigate)
                    }
                }
            }
    }
}
@Composable
fun NavButton(
    data: HealthScreenData,
    onNavigate: (String) -> Unit
){
    val color1 = Color(data.color1)
    val color2 = Color(data.color2)
    Box(
        modifier = Modifier
            .padding(10.dp)
            .width(300.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(colors = listOf(color1, color2)))
            .clickable { onNavigate(data.rout) },
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = data.iconId),
                    contentDescription = data.text,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = data.text,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
        }
    }
}

