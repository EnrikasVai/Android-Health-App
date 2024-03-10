package com.example.bakis.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(navController: NavHostController, onNavigate: (String) -> Unit) {
    val items = listOf("Dashboard", "Health", "Me")
    val icons = listOf(Icons.Default.Build, Icons.Default.Favorite, Icons.Default.Person)
    val data = listOf(
        HealthScreenData(R.drawable.footsteps,"HEALTH RISK CHECK", 0xFFFF7518, 0xFFFF3131, "screen1"),
        HealthScreenData(R.drawable.footsteps,"HEALTH RISK CHECK", 0xFFFF7518, 0xFFFF3131, "screen1"),
        HealthScreenData(R.drawable.footsteps,"HEALTH RISK CHECK", 0xFFFF7518, 0xFFFF3131, "screen1"),
        HealthScreenData(R.drawable.footsteps,"HEALTH RISK CHECK", 0xFFFF7518, 0xFFFF3131, "screen1")
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
                        .wrapContentHeight(), // Make LazyColumn wrap its content
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
            .clip(RoundedCornerShape(12.dp)) // Clip applies to this outer Box
            .background(Brush.horizontalGradient(colors = listOf(color1, color2)))
            .clickable { onNavigate(data.rout) },
    ) {
        Box(
            modifier = Modifier
                .matchParentSize() // Ensures this Box fills the clipped parent
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.text,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

