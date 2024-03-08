package com.example.bakis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.bakis.navigation.SetupNavigation
import com.example.bakis.ui.theme.BakisTheme
import com.example.bakis.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BakisTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Remember the navigation controller
                    val navController = rememberNavController()

                    // Create an instance of HomeViewModel
                    val homeViewModel: HomeViewModel = viewModel()

                    // Set up the navigation graph
                    SetupNavigation(navController, homeViewModel)
                }
            }
        }
    }
}