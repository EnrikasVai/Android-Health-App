package com.example.bakis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bakis.viewmodel.HomeViewModel
import com.example.bakis.database.UserEntity
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

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome, please fill out the information below",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black, // Set text color to black
                    focusedBorderColor = Color.Black, // Optional: Set border color when focused
                )

            )
            Spacer(modifier = Modifier.height(16.dp))
            // Sliders for Age, Height, and Weight
            Text("Age: $age years")
            Slider(
                value = age.toFloat(),
                onValueChange = { age = it.toInt() },
                valueRange = 12f..100f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color.Black.copy(alpha = 0.24f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Height: $height cm")
            Slider(
                value = height.toFloat(),
                onValueChange = { height = it.toInt() },
                valueRange = 100f..220f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color.Black.copy(alpha = 0.24f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Displaying the weight rounded to one decimal place
            Text("Weight: ${"%.1f".format(weight)} kg")
            Slider(
                value = weight.toFloat(),
                onValueChange = { newValue ->
                    // Convert back to Double and round to one decimal place
                    weight = (newValue.toDouble() * 10).roundToInt() / 10.0
                },
                valueRange = 30f..200f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color.Black.copy(alpha = 0.24f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Gender Label
            Text("Sex/Gender", style = MaterialTheme.typography.bodyMedium)
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
                            }
                        )
                        Text(text = option, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            // Spacer for spacing before the Save button
            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val userEntity = UserEntity(name = name, age = age, height = height, weight = weight, sex = sex)
                    homeViewModel.insertUser(userEntity)
                    // Navigate to the home screen
                    navController.navigate("home")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Save my info", color = Color.White)
            }
        }
    }
}
