package com.example.bakis.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bakis.viewmodel.HomeViewModel
import com.example.bakis.database.UserEntity
import kotlin.math.round
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTimeScreen(homeViewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    //Database variables
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(18) }
    var height by remember { mutableStateOf(160) } // Default 160 cm
    var weight by remember { mutableStateOf(60.0) } // Default 60 kg
    var sex by remember { mutableStateOf(true) } // true for male, false for female

    // Added for gender selection
    val genderOptions = listOf("Female", "Male")
    var selectedOption by remember { mutableStateOf(if (sex) "Male" else "Female") }

    val activity = LocalContext.current as Activity

    // Handle the back button press
    BackHandler(enabled = true) {
        // This block will be empty to intercept the back press without any action
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF333333))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome, please fill out the information below",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name (optional)", color = Color(0xFFd3d3d3)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    //textColor = Color(0xFFd3d3d3),
                    focusedBorderColor = Color(0xFFd3d3d3),
                )

            )
            Spacer(modifier = Modifier.height(16.dp))
            // Sliders for Age, Height, and Weight
            Text(text = "Age: $age years", color =Color(0xFFd3d3d3))
            Slider(
                value = age.toFloat(),
                onValueChange = { age = it.toInt() },
                valueRange = 12f..100f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFd3d3d3),
                    activeTrackColor = Color(0xFFd3d3d3),
                    inactiveTrackColor = Color(0xFFd3d3d3).copy(alpha = 0.24f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Height: $height cm",color = Color(0xFFd3d3d3))
            Slider(
                value = height.toFloat(),
                onValueChange = { height = it.toInt() },
                valueRange = 100f..220f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFd3d3d3),
                    activeTrackColor = Color(0xFFd3d3d3),
                    inactiveTrackColor = Color(0xFFd3d3d3).copy(alpha = 0.24f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Displaying the weight rounded to one decimal place
            Text(text = "Weight: ${"%.1f".format(weight)} kg", color =Color(0xFFd3d3d3))
            Slider(
                value = weight.toFloat(),
                onValueChange = { newValue ->
                    // More refined adjustment for .0 and .5 increments
                    weight = (round(newValue * 2) / 2.0).toDouble()
                },
                valueRange = 30f..200f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFD3D3D3),
                    activeTrackColor = Color(0xFFD3D3D3),
                    inactiveTrackColor = Color(0xFFD3D3D3).copy(alpha = 0.24f)
                )
                // Steps parameter is removed since we are manually adjusting the value to conform to .5 steps.
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Gender Label
            Text("Sex/Gender", color = Color(0xFFd3d3d3))
            Spacer(modifier = Modifier.height(8.dp))

            // Gender Selection Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                genderOptions.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 8.dp)) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = {
                                selectedOption = option
                                sex = option == "Male" // Update sex based on selection
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFd3d3d3), // Color when selected
                                //unselectedColor = Color() // Color when not selected
                            ),
                        )
                        Text(text = option, modifier = Modifier.padding(start = 4.dp), color = Color(0xFFd3d3d3))
                    }
                }
            }
            // Spacer for spacing before the Save button
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {
                    val userEntity = UserEntity(name = name, age = age, height = height, weight = weight, sex = sex)
                    homeViewModel.insertUser(userEntity)
                    // Navigate to the home screen
                    navController.navigate("home") {
                        // Remove all entries from the back stack
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFe8e209))
            ) {
                Text("Save my info", color = Color.White)
            }
        }
    }
}
