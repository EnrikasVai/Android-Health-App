package com.example.bakis.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bakis.R
import com.example.bakis.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(homeViewModel: HomeViewModel, navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) } // Initially, set loading to true
    val hasUsers by homeViewModel.hasUsers.collectAsState()
    val activity = LocalContext.current as Activity

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

            // Automatic navigation after loading is complete
            if (hasUsers == true) {
                navController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            } else {
                navController.navigate("registration") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        }
    }
    // Handle the back button press
    BackHandler(enabled = true) {
        activity.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF333333)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.Center // This aligns the contents of the Box (including the Column) to the center
            ) {
                Column(
                    modifier = Modifier
                        .padding(50.dp), // Apply padding to the Column, not affecting its alignment
                    horizontalAlignment = Alignment.CenterHorizontally // This will center the Column's children horizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_app),
                        contentDescription = "Your content description here",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "Welcome to Your Health App",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

