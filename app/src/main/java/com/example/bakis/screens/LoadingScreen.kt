package com.example.bakis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bakis.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(homeViewModel: HomeViewModel, navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) } // Initially, set loading to true

    // Observe hasUsers state
    val hasUsers by homeViewModel.hasUsers.collectAsState()

    SideEffect {
        coroutineScope.launch {
            val startTime = System.currentTimeMillis()
            homeViewModel.checkIfUsersExist()
            val checkDuration = System.currentTimeMillis() - startTime
            val minLoadingTime = 1000L // Minimum loading time in milliseconds
            // If the check finished quicker than minLoadingTime, delay the remaining time
            if (checkDuration < minLoadingTime) {
                delay(minLoadingTime - checkDuration)
            }
            isLoading = false
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF333333)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to Your Health App",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Button(
                    onClick = {
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
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFe8e209))
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
