package com.example.bakis.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bakis.database.UserEntity
import com.example.bakis.viewmodel.HomeViewModel
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTimeScreen(homeViewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    //Database variables
    var name by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(18) }
    var height by remember { mutableIntStateOf(160) }
    var weight by remember { mutableDoubleStateOf(60.0) }
    var sex by remember { mutableStateOf(true) }

    // Added for gender selection
    val genderOptions = listOf("Female", "Male")
    var selectedOption by remember { mutableStateOf(if (sex) "Male" else "Female") }


    BackHandler(enabled = true) {
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
                    focusedBorderColor = Color(0xFFd3d3d3),
                )

            )
            Spacer(modifier = Modifier.height(16.dp))
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

            Text(text = "Weight: ${"%.1f".format(weight)} kg", color =Color(0xFFd3d3d3))
            Slider(
                value = weight.toFloat(),
                onValueChange = { newValue ->
                    weight = (round(newValue * 2) / 2.0)
                },
                valueRange = 30f..200f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFD3D3D3),
                    activeTrackColor = Color(0xFFD3D3D3),
                    inactiveTrackColor = Color(0xFFD3D3D3).copy(alpha = 0.24f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Sex/Gender", color = Color(0xFFd3d3d3))
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                genderOptions.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 8.dp)) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = {
                                selectedOption = option
                                sex = option == "Male"
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFd3d3d3),
                            ),
                        )
                        Text(text = option, modifier = Modifier.padding(start = 4.dp), color = Color(0xFFd3d3d3))
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {
                    val userEntity = UserEntity(name = name, age = age, height = height, weight = weight, sex = sex)
                    homeViewModel.insertUser(userEntity)
                    navController.navigate("home") {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffff5500))
            ) {
                Text("Save my info", color = Color.White)
            }
        }
    }
}
