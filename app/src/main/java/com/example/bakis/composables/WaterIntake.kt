package com.example.bakis.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bakis.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WaterIntakeTracker(homeViewModel: HomeViewModel=hiltViewModel()) {
    val userId by homeViewModel.userId.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    // UI state
    var cupSize by remember { mutableIntStateOf(200) }
    var showCupSizeDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    // Observing water intake records from the ViewModel
    val waterIntakeRecords by homeViewModel.waterIntakeRecords.collectAsState()

    //
    val userWaterGoal by homeViewModel.userWaterGoal.collectAsState()

    val todaysWaterIntake by homeViewModel.totalDailyIntake.collectAsState()
    // Fetch records on composable composition
    LaunchedEffect(key1 = userId) {
        homeViewModel.fetchWaterIntakeRecords(userId)
        homeViewModel.fetchDailyWaterIntakeForUser(userId, today)
    }
    Box(
        modifier = Modifier
            .padding(end = 20.dp, start = 10.dp)
            .fillMaxWidth()
            .background(color = Color(0xFF333333), shape = RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            Text("Today's Water Intake",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth() ,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Today: $todaysWaterIntake / $userWaterGoal ml", color = Color.White, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(10.dp))
                Button(modifier = Modifier.width(135.dp),
                    onClick = {  showGoalDialog = true},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF08012e)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Change goal", color = Color.White)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cup Size: $cupSize ml", color = Color.White, modifier = Modifier.weight(1f))
                Button(modifier = Modifier.width(135.dp),
                    onClick = { showCupSizeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF08012e)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Set Cup Size", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            if (showCupSizeDialog) {
                CupSizeDialog(
                    cupSize = cupSize,
                    onCupSizeSet = { newSize ->
                        cupSize = newSize
                        showCupSizeDialog = false
                    },
                    onDismissRequest = { showCupSizeDialog = false }
                )
            }
            if (showGoalDialog) {
                GoalDialog(
                    currentGoal = userWaterGoal,
                    onGoalSet = { newGoal ->
                        homeViewModel.updateUserWaterGoal(userId, newGoal)
                    },
                    onDismissRequest = { showGoalDialog = false }
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                homeViewModel.addWaterIntake(userId, cupSize, today)
            },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1c37ff)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
                ) {
                Text("Add Water Intake", color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            // Display water intake records
            waterIntakeRecords.filter { it.date == today }.forEach { intake ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 10.dp)) {
                    Text("${intake.intakeAmount} ml of water Today", color = Color.White, modifier = Modifier.weight(1f), fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .clickable {
                                // Call ViewModel to delete this record
                                homeViewModel.deleteWaterIntake(intake)
                            }
                            .size(24.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
@Composable
fun CupSizeDialog(
    cupSize: Int,
    onCupSizeSet: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var input by remember { mutableStateOf(cupSize.toString()) }
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .background(Color(0xFF262626), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text("Set Cup Size (ml)", color = Color.White)
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Cup Size") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    unfocusedLeadingIconColor = Color.White
                )
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    onCupSizeSet(input.toIntOrNull() ?: cupSize)
                    onDismissRequest()
                },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Set" , color = Color.White)
                }
            }
        }
    }
}
@Composable
fun GoalDialog(
    currentGoal: Int,
    onGoalSet: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var input by remember { mutableStateOf(currentGoal.toString()) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFF262626), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text("Set Your Goal (ml)", color = Color.White)
                OutlinedTextField(
                    value = input,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        unfocusedLeadingIconColor = Color.White
                    ),
                    onValueChange = { input = it},
                    label = { Text("Goal", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {
                        val newGoal = input.toIntOrNull() ?: currentGoal
                        onGoalSet(newGoal) // Let the parent composable handle the actual setting
                        onDismissRequest()
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Set")
                    }
                }
            }
        }
    }
}

