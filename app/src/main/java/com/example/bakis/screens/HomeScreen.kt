package com.example.bakis.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bakis.R
import com.example.bakis.consumables.CustomBottomNavigationBar
import com.example.bakis.consumables.CustomTopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, onNavigate: (String) -> Unit) {
    val items = listOf("Dashboard", "Health", "Me")
    val icons = listOf(Icons.Default.Build, Icons.Default.Favorite, Icons.Default.Person)

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Health Dashboard"
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
                .background(Color.White)
                .padding(paddingValues) // Apply the padding here
                .background(Color(0xFF262626)), // Set the background color here

        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the parent's width
                        .padding(top= 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp), // Padding around the entire Row
                    horizontalArrangement = Arrangement.Center // Center items horizontally
                ) {
                    // First square
                    Box(
                        modifier = Modifier
                            .size(170.dp) // Set both width and height to 150.dp
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)) // Add shadow with rounded corners
                            .clip(RoundedCornerShape(10.dp)) // Round the corners of the Box
                            .background(Color(0xFF333333)) // Set the background color to white
                            .clickable {
                                navController.navigate("data") // On click, navigate to DataScreen
                            }                    ) {
                        // Icon and Steps text at the top left
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart) // Align Row to the top start corner of the Box
                                .padding(12.dp) // Add some padding to separate from edges
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.footsteps), // Use the footsteps icon
                                contentDescription = "Steps",
                                modifier = Modifier
                                    .size(20.dp)
                                ,
                                tint = Color(0xFFFF7518)
                            )
                            Text(
                                text = "Steps",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 4.dp)
                                ,
                                color = Color(0xFFFF7518),
                            )
                        }
                        Text(
                            text = "2000",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)

                        )
                        // Number of steps below the icon and text
                        Text(
                            text = "Today",
                            color = Color(0xFFFF7518),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 115.dp, start = 8.dp, end= 16.dp) // Adjust padding to position below the icon and steps text
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Gap between the squares
                    // Second square
                    Box(
                        modifier = Modifier
                            .size(170.dp) // Set both width and height to 150.dp
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)) // Add shadow with rounded corners
                            .clip(RoundedCornerShape(10.dp)) // Round the corners of the Box
                            .background(Color(0xFF333333)) // Set the background color to white
                    ) {
                        // Icon and Steps text at the top left
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart) // Align Row to the top start corner of the Box
                                .padding(12.dp) // Add some padding to separate from edges
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.bed), // Use the footsteps icon
                                contentDescription = "Bed",
                                modifier = Modifier
                                    .size(20.dp)
                                ,
                                tint = Color(0xFF09bfe8)
                            )
                            Text(
                                text = "Time In Bed",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 4.dp)
                                ,
                                color = Color(0xFF09bfe8),
                            )
                        }
                        Text(
                            text = "8hr 35min",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)

                        )
                        // Number of steps below the icon and text
                        Text(
                            text = "Today",
                            color = Color(0xFF09bfe8),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 115.dp, start = 8.dp, end= 16.dp) // Adjust padding to position below the icon and steps text
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the parent's width
                        .padding(top= 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp), // Padding around the entire Row
                    horizontalArrangement = Arrangement.Center // Center items horizontally
                ) {
                    // First square
                    Box(
                        modifier = Modifier
                            .size(170.dp) // Set both width and height to 150.dp
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)) // Add shadow with rounded corners
                            .clip(RoundedCornerShape(10.dp)) // Round the corners of the Box
                            .background(Color(0xFF333333)) // Set the background color to white
                    ) {
                        // Icon and Steps text at the top left
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart) // Align Row to the top start corner of the Box
                                .padding(12.dp) // Add some padding to separate from edges
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.heart_beat), // Use the footsteps icon
                                contentDescription = "Bed",
                                modifier = Modifier
                                    .size(20.dp)
                                ,
                                tint = Color(0xFFFF3131)
                            )
                            Text(
                                text = "Heart Beat",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 4.dp)
                                ,
                                color = Color(0xFFFF3131),
                            )
                        }
                        Text(
                            text = "67",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)

                        )
                        Text(
                            text = "Today",
                            color = Color(0xFFFF3131),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 115.dp, start = 8.dp, end= 16.dp) // Adjust padding to position below the icon and steps text
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Gap between the squares
                    // Second square
                    Box(
                        modifier = Modifier
                            .size(170.dp) // Set both width and height to 150.dp
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)) // Add shadow with rounded corners
                            .clip(RoundedCornerShape(10.dp)) // Round the corners of the Box
                            .background(Color(0xFF333333)) // Set the background color to white
                    ) {
                        // Icon and Steps text at the top left
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart) // Align Row to the top start corner of the Box
                                .padding(12.dp) // Add some padding to separate from edges
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.bed), // Use the footsteps icon
                                contentDescription = "Bed",
                                modifier = Modifier
                                    .size(20.dp)
                                ,
                                tint = Color(0xFF09bfe8)
                            )
                            Text(
                                text = "Time In Bed",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 4.dp)
                                ,
                                color = Color(0xFF09bfe8),
                            )
                        }
                        Text(
                            text = "20hr 35min",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)

                        )
                        Text(
                            text = "Today",
                            color = Color(0xFF09bfe8),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 115.dp, start = 8.dp, end= 16.dp) // Adjust padding to position below the icon and steps text
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the parent's width
                        .padding(top= 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp), // Padding around the entire Row
                    horizontalArrangement = Arrangement.Center // Center items horizontally
                ) {
                    // First square
                    Box(
                        modifier = Modifier
                            .size(170.dp) // Set both width and height to 150.dp
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)) // Add shadow with rounded corners
                            .clip(RoundedCornerShape(10.dp)) // Round the corners of the Box
                            .background(Color(0xFF333333)) // Set the background color to white
                    ) {
                        // Icon and Steps text at the top left
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart) // Align Row to the top start corner of the Box
                                .padding(12.dp) // Add some padding to separate from edges
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.heart_beat), // Use the footsteps icon
                                contentDescription = "Bed",
                                modifier = Modifier
                                    .size(20.dp)
                                ,
                                tint = Color(0xFFFF3131)
                            )
                            Text(
                                text = "Heart Beat",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 4.dp)
                                ,
                                color = Color(0xFFFF3131),
                            )
                        }
                        Text(
                            text = "67",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)

                        )
                        Text(
                            text = "Today",
                            color = Color(0xFFFF3131),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 115.dp, start = 8.dp, end= 16.dp) // Adjust padding to position below the icon and steps text
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Gap between the squares
                    // Second square
                    Box(
                        modifier = Modifier
                            .size(170.dp) // Set both width and height to 150.dp
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)) // Add shadow with rounded corners
                            .clip(RoundedCornerShape(10.dp)) // Round the corners of the Box
                            .background(Color(0xFF333333)) // Set the background color to white
                    ) {
                        // Icon and Steps text at the top left
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart) // Align Row to the top start corner of the Box
                                .padding(12.dp) // Add some padding to separate from edges
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.bed), // Use the footsteps icon
                                contentDescription = "Bed",
                                modifier = Modifier
                                    .size(20.dp)
                                ,
                                tint = Color(0xFF09bfe8)
                            )
                            Text(
                                text = "Time In Bed",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 4.dp)
                                ,
                                color = Color(0xFF09bfe8),
                            )
                        }
                        Text(
                            text = "20hr 35min",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)

                        )
                        Text(
                            text = "Today",
                            color = Color(0xFF09bfe8),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 115.dp, start = 8.dp, end= 16.dp) // Adjust padding to position below the icon and steps text
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(30.dp)) // Gap between the squares
            }
            // Replace these colors with your theme colors
            val colors = listOf(Color.Green, Color.Blue, Color.Magenta, Color.Cyan)
            val texts = listOf("HEALTH RISK CHECK", "SPORTS EVALUATION", "EXERCISE TRAINING", "DNA TESTING")
            val routes = listOf("screen1", "screen2", "screen3", "dnaTesting")

            for (i in texts.indices) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(300.dp)
                            .height(100.dp)
                            .clickable { onNavigate(routes[i]) },
                        shape = RoundedCornerShape(40.dp),
                        colors = CardDefaults.cardColors(containerColor = colors[i])
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

