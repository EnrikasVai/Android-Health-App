package com.example.bakis.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.bakis.R
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
import com.example.bakis.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val userAge by viewModel.userAge.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userWeight by viewModel.userWeight.collectAsState()
    val userHeight by viewModel.userHeight.collectAsState()
    val userSex by viewModel.userSex.collectAsState()
    val userId by viewModel.userId.collectAsState()

    //TOP BAR EDIT BOOL
    var isEditMode by remember { mutableStateOf(false) }

    // Mutable states for temporary values
    var tempUserName by remember { mutableStateOf(userName) }
    var tempUserAge by remember { mutableStateOf(userAge.toString()) }
    var tempUserWeight by remember { mutableStateOf(userAge.toString()) }
    var tempUserHeight by remember { mutableStateOf(userAge.toString()) }
    var tempUserSex by remember { mutableStateOf(userAge.toString()) }


    // Dialog states
    var showNameEditDialog by remember { mutableStateOf(false) }
    var showAgeEditDialog by remember { mutableStateOf(false) }
    var showSexEditDialog by remember { mutableStateOf(false) }
    var showWeightEditDialog by remember { mutableStateOf(false) }
    var showHeightEditDialog by remember { mutableStateOf(false) }

    //ALERT USER BEFORE DELETING*******************************************************
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            // Enclose your dialog content inside a Surface to customize background color
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                color = Color(0xFF262626), // Set the background color to grey
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon at the top of the dialog
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_info_24),
                        tint = Color.Red,
                        contentDescription = "Info Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    // Dialog title
                    Text(
                        fontSize = 20.sp,
                        color = Color.Red,
                        text = "Warning!!!",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Dialog message
                    Text(
                        text = "Are you sure you want to delete your profile? This action cannot be undone. All your data in this application will be lost forever!",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Buttons in a row at the bottom of the dialog
                    Row(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        // Cancel button
                        Button(
                            onClick = { showDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613)) // Set button color to red
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Confirm button
                        Button(
                            onClick = {
                                showDialog = false
                                viewModel.viewModelScope.launch {
                                    viewModel.deleteUserAll()
                                    withContext(Dispatchers.Main) {
                                        navController.navigate("welcome")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Set button color to red

                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Profile",
                onEditClick = { isEditMode = !isEditMode },
                showEditIcon = true // Only show the edit icon in the ProfileScreen
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF262626)) // Set the background color here
                .padding(top = 40.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = (LocalConfiguration.current.screenWidthDp.dp * 0.1f) / 2)
                        .background(Color(0xFF262626))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)), // Adjust the corner radius as needed
                    ) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF333333))
                        ) {
                            ProfileItem(label = "Name", value = userName, isEditMode) { showNameEditDialog = true }
                            Divider(
                                color = Color(0xFF666666), // Set the divider color to white
                                modifier = Modifier
                                    .padding(horizontal = 10.dp) // Adjust padding to make the divider shorter
                                    .fillMaxWidth()
                            )
                            ProfileItem(label = "Age", value = "$userAge", isEditMode) { showAgeEditDialog = true }
                            Divider(
                                color = Color(0xFF666666), // Set the divider color to white
                                modifier = Modifier
                                    .padding(horizontal = 10.dp) // Adjust padding to make the divider shorter
                                    .fillMaxWidth()
                            )
                            ProfileItem(label = "Weight", value = "$userWeight kg", isEditMode){showWeightEditDialog = true}
                            Divider(
                                color = Color(0xFF666666), // Set the divider color to white
                                modifier = Modifier
                                    .padding(horizontal = 10.dp) // Adjust padding to make the divider shorter
                                    .fillMaxWidth()
                            )
                            ProfileItem(label = "Height", value = "$userHeight cm", isEditMode){showHeightEditDialog = true}
                            Divider(
                                color = Color(0xFF666666), // Set the divider color to white
                                modifier = Modifier
                                    .padding(horizontal = 10.dp) // Adjust padding to make the divider shorter
                                    .fillMaxWidth()
                            )
                            ProfileItem(label = "Gender", value = if (userSex) "Male" else "Female", isEditMode){showSexEditDialog = true}
                            Divider(
                                color = Color(0xFF666666), // Set the divider color to white
                                modifier = Modifier
                                    .padding(horizontal = 10.dp) // Adjust padding to make the divider shorter
                                    .fillMaxWidth()
                            )
                            ProfileItem(label = "ID(istrynt paskui)", value = "$userId", isEditMode){showSexEditDialog = true}

                        }
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(), // Ensures the Column takes the full width
                    horizontalAlignment = Alignment.CenterHorizontally // Centers its children horizontally
                ) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .padding(8.dp)
                            .width(250.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Set button color to red
                    ) {
                        Text(
                            text = "Delete Profile",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
    if (showNameEditDialog) {
        // Ensure tempUserName is updated to the latest userName before showing the dialog
        tempUserName = userName
        EditDialog(
            title = "Edit Name",
            textValue = tempUserName,
            userId = userId,
            onConfirm ={ newName ->
                viewModel.updateUserName(userId, newName)
                tempUserName = newName // Update the temporary holder with the new value
                showNameEditDialog = false },
            onDismiss = { showNameEditDialog = false }
        )
    }
    if (showSexEditDialog) {
        val initialSex = if (userSex) "Male" else "Female" // Adjust this line based on your actual `userSex` type
        EditDialogSex(
            title = "Edit Sex",
            initialSex = initialSex,
            userId = userId,
            onConfirm = { newSex ->
                viewModel.updateUserSex(userId, newSex.toBoolean()) // Make sure this method exists and is correctly implemented
                // Update the UI or state as needed after the sex is updated
                showSexEditDialog = false},
            onDismiss = {showSexEditDialog = false}
        )
    }
    if (showAgeEditDialog) {
        // Ensure tempUserAge is set to the current age just before showing the dialog
        tempUserAge = userAge.toString() // Assuming userAge is up-to-date
        // Safely convert tempUserAge to an integer, providing a default value if needed
        val currentAge = tempUserAge.toIntOrNull() ?: 18 // Use a sensible default if conversion fails
        EditDialogAge(
            title = "Edit Age",
            ageValue = currentAge,
            userId = userId,
            onConfirm = { newAge ->
                viewModel.viewModelScope.launch {
                    // Update the age in your ViewModel here
                    viewModel.updateUserAge(userId, newAge) // Assuming updateUserAge exists
                }
                // After updating, you may want to also update userAge in your ViewModel to reflect the change immediately
                showAgeEditDialog = false },
            onDismiss = {showAgeEditDialog = false}
        )
    }
    if (showHeightEditDialog) {
        // Ensure tempUserAge is set to the current age just before showing the dialog
        tempUserHeight = userHeight.toString() // Assuming userAge is up-to-date
        // Safely convert tempUserAge to an integer, providing a default value if needed
        val currentHeight = tempUserHeight.toIntOrNull() ?: 160 // Use a sensible default if conversion fails
        EditDialogHeight(
            title = "Edit Height",
            heightValue = currentHeight,
            userId = userId,
            onConfirm = { newHeight ->
                viewModel.viewModelScope.launch {
                    // Update the age in your ViewModel here
                    viewModel.updateUserHeight(userId, newHeight) // Assuming updateUserAge exists
                }
                // After updating, you may want to also update userAge in your ViewModel to reflect the change immediately
                showHeightEditDialog = false},
            onDismiss = {showHeightEditDialog = false}
        )
    }
    if (showWeightEditDialog) {
        tempUserWeight = userWeight.toString() // Make sure userWeight is a Double and up-to-date
        // Convert tempUserWeight safely to Double, providing a default if needed
        val currentWeight: Double = tempUserWeight.toDoubleOrNull() ?: 160.0 // Explicitly typed as Double

        EditDialogWeight(
            title = "Edit Weight",
            weightValue = currentWeight,
            userId = userId,
            onConfirm = { newWeight ->
                viewModel.viewModelScope.launch {
                    // Make sure updateUserWeight method accepts Double
                    viewModel.updateUserWeight(userId, newWeight)
                }
                // Close the dialog and possibly refresh userWeight in your ViewModel to reflect the update
                showWeightEditDialog = false},
            onDismiss = {showWeightEditDialog = false}
        )
    }

}

@Composable
fun ProfileItem(label: String, value: String, isEditMode: Boolean, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(isEditMode) { onEdit() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White
        )
        Text(
            text = value,
            color = if (isEditMode) Color.Yellow else Color(0xFFffffff)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialog(title: String, textValue: String, userId: Int, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(textValue) }
    val viewModel: HomeViewModel = hiltViewModel()

    Dialog(onDismissRequest = { onDismiss() }) {
        // Container for dialog content
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626), // Set the background color to grey
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 16.dp)
            ) {
                // Dialog title
                Text(
                    text = title,
                    color= Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth() // Make the Text composable fill the maximum width of its parent
                        .wrapContentSize(align = Alignment.Center)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                // Input field
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.LightGray),
                    colors = TextFieldDefaults.textFieldColors(
                        //textColor = Color.Black, // Color of the text
                        cursorColor = Color.Blue, // keep
                        focusedIndicatorColor = Color.Blue, // Keep
                        unfocusedIndicatorColor = Color.Black, // Color of the indicator (bottom line) when not focused
                        disabledIndicatorColor = Color.Black, // Color of the indicator (bottom line) when disabled
                    ),
                )
                Spacer(modifier = Modifier.padding(16.dp))
                // Button row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End // Aligns the buttons to the end (right side) of the row
                ) {
                    // Dismiss button
                    Button(
                        onClick = { onDismiss() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red), // Set button color to red
                        modifier = Modifier.padding(end = 8.dp) // Adds spacing between the buttons
                    ) {
                        Text("Cancel")
                    }

                    // Confirm button
                    Button(
                        onClick = {
                            onConfirm(text)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserName(userId, text) // Make sure userId is accessible here
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613)) // Set button color to red
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

}
@Composable
fun EditDialogSex(title: String, initialSex: String, userId: Int, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    val viewModel: HomeViewModel = hiltViewModel()
    val genderOptions = listOf("Female", "Male")
    // Track selected sex with a proper state
    var selectedSex by remember { mutableStateOf(initialSex) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626), // Set the background color to grey
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(
                    text = title,
                    color= Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth() // Make the Text composable fill the maximum width of its parent
                        .wrapContentSize(align = Alignment.Center)
                        .padding(top= 16.dp)
                )
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    // Your options list
                    genderOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { selectedSex = option }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedSex == option,
                                onClick = { selectedSex = option }
                            )
                            Text(text = option,color=Color.White, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))

                Row(
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp) // Adds spacing between the buttons
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp)) // Space between buttons
                    Button(
                        onClick = {
                            val isMale = selectedSex == "Male"
                            onConfirm(selectedSex)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserSex(userId, isMale)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613)) // Set button color to red
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

}

@Composable
fun EditDialogAge(title: String, ageValue: Int, userId: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    // Remember the current age value and convert it to a mutable state to allow changes
    var age by remember { mutableStateOf(ageValue) }
    val viewModel: HomeViewModel = hiltViewModel()

    Dialog(onDismissRequest = { /* Do something if needed when the dialog is dismissed */ }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626), // Use the grey color for the background as in the previous prompt
            shape = RoundedCornerShape(8.dp) // Use rounded corners for the shape
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth() // Fill the maximum width
                        .wrapContentSize(Alignment.Center)
                        .padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp)) // Space between buttons
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    Text("Age: $age years", color = Color(0xFFd3d3d3))
                    Slider(
                        value = age.toFloat(),
                        onValueChange = { age = it.toInt() },
                        valueRange = 12f..100f, // Age range
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFd3d3d3),
                            activeTrackColor = Color(0xFFd3d3d3),
                            inactiveTrackColor = Color(0xFFd3d3d3).copy(alpha = 0.24f)
                        )
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red), // Cancel button with red color
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp)) // Space between Cancel and Save buttons
                    Button(
                        onClick = {
                            onConfirm(age)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserAge(userId, age) // Perform the save operation
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613)) // Save button with green color
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

}
@Composable
fun EditDialogHeight(title: String, heightValue: Int, userId: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    // Remember the current age value and convert it to a mutable state to allow changes
    var height by remember { mutableStateOf(heightValue) }
    val viewModel: HomeViewModel = hiltViewModel()

    Dialog(onDismissRequest = { /* Do something if needed when the dialog is dismissed */ }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626), // Using the grey color for the background as before
            shape = RoundedCornerShape(8.dp) // Rounded corners for the dialog
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth() // Make the Text composable fill the maximum width
                        .wrapContentSize(Alignment.Center)
                        .padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp)) // Space between Cancel and Save buttons
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    Text("Height: $height cm", color = Color(0xFFd3d3d3))
                    Slider(
                        value = height.toFloat(),
                        onValueChange = { height = it.toInt() },
                        valueRange = 100f..220f, // Height range
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFd3d3d3),
                            activeTrackColor = Color(0xFFd3d3d3),
                            inactiveTrackColor = Color(0xFFd3d3d3).copy(alpha = 0.24f)
                        )
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red), // Cancel button with red color
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp)) // Space between Cancel and Save buttons
                    Button(
                        onClick = {
                            onConfirm(height)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserHeight(userId, height) // Action on Save button click
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613)) // Save button with green color
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

}
@Composable
fun EditDialogWeight(title: String, weightValue: Double, userId: Int, onConfirm: (Double) -> Unit, onDismiss: () -> Unit) {
    var weight by remember { mutableStateOf(weightValue) }
    val viewModel: HomeViewModel = hiltViewModel()

    Dialog(onDismissRequest = { /* Do something if needed when the dialog is dismissed */ }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626), // Set the background color to grey as in previous examples
            shape = RoundedCornerShape(8.dp) // Rounded corners for the dialog
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth() // Fill the maximum width
                        .wrapContentSize(Alignment.Center)
                        .padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp)) // Space between Cancel and Save buttons
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    Text("Weight: $weight kg", color = Color(0xFFd3d3d3))
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
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red), // Cancel button with red color
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp)) // Space between Cancel and Save buttons
                    Button(
                        onClick = {
                            onConfirm(weight)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserWeight(userId, weight) // Update user weight
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613)) // Save button with green color
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
