package com.example.bakis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bakis.viewmodel.HomeViewModel

@Composable
fun WelcomeScreen(homeViewModel: HomeViewModel, navController: NavHostController){
    val hasUsers by homeViewModel.hasUsers.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.checkIfUsersExist() // Trigger the check when the WelcomeScreen is launched
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF333333))
    ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Welcome to Your Health App",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (hasUsers == true) {
                        navController.navigate("home") {
                            // Clear the back stack
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        navController.navigate("registration") {
                            // Clear the back stack
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(250.dp)
                        .height(60.dp)
                    ,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFe8e209)) // Set button color to red
                    ) {
                    Text(
                        text = "Continue",
                        fontSize = 20.sp,

                    )
                }
            }
        }
    }
}
