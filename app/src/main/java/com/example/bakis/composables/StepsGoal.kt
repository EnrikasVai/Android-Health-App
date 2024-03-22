package com.example.bakis.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bakis.viewmodel.HomeViewModel

@Composable
fun StepProgressBar(stepsWalked: Int, stepGoal: Int, viewModel: HomeViewModel = hiltViewModel()) {
    val progress = (stepsWalked.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f)

    val userId by viewModel.userId.collectAsState()
    var showGoalDialog by remember { mutableStateOf(false) }
    val userStepGoal by viewModel.userStepGoal.collectAsState()

    var StepsForGoal = stepsWalked
    if(stepsWalked>=userStepGoal)
        StepsForGoal=userStepGoal

    Box(modifier = Modifier
        .padding(end = 10.dp)
        .fillMaxWidth()
        .background(color = Color.DarkGray, shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(color = Color.DarkGray)
        ) {
            Text(text = "Today's Steps", color = Color.White, fontWeight = FontWeight(500), fontSize = 22.sp)
            Spacer(modifier = Modifier.height(15.dp))
            if (stepsWalked>=userStepGoal)
                Text(text = "Steps goal of $stepGoal reached!", color = Color.White)
            else
                Text(text = "Steps: $StepsForGoal/$stepGoal", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                ,
            ) {
                val barWidth = size.width * progress
                drawRect(color = Color.LightGray)
                drawRect(color = Color.Green, size = this.size.copy(width = barWidth))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier.width(135.dp),
                onClick = { showGoalDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF08012e)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Change goal", color = Color.White)
            }

            if (showGoalDialog) {
                GoalDialogStep(
                    currentGoal = userStepGoal,
                    onGoalSet = { newGoal ->
                        viewModel.updateUserStepGoal(userId,newGoal)
                        showGoalDialog = false
                    },
                    onDismissRequest = { showGoalDialog = false }
                )
            }
        }
    }
}

@Composable
fun GoalDialogStep(
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
                Text("Set Your Goal Steps", color = Color.White)
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


@Preview(showBackground = true)
@Composable
fun PreviewStepProgressBar() {
    StepProgressBar(stepsWalked = 3000, stepGoal = 10000)
}