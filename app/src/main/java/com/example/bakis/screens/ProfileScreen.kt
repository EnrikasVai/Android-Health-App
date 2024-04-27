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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.launch
import kotlin.math.round

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                color = Color(0xFF262626),
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
                    Row(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        // Cancel button
                        Button(
                            onClick = { showDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613))
                        ) {
                            Text(text = "Cancel", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Confirm button
                        Button(
                            onClick = {
                                showDialog = false
                                viewModel.viewModelScope.launch {
                                    viewModel.disconnect()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)

                        ) {
                            Text("Confirm", color = Color.White)
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
                showEditIcon = true
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
                .background(Color(0xFF262626))
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
                            .clip(RoundedCornerShape(8.dp)),
                    ) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF333333))
                        ) {
                            ProfileItem(label = "Name", value = userName, isEditMode) { showNameEditDialog = true }
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                color = Color(0xFF666666)
                            )
                            ProfileItem(label = "Age", value = "$userAge", isEditMode) { showAgeEditDialog = true }
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                color = Color(0xFF666666)
                            )
                            ProfileItem(label = "Weight", value = "$userWeight kg", isEditMode){showWeightEditDialog = true}
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                color = Color(0xFF666666)
                            )
                            ProfileItem(label = "Height", value = "$userHeight cm", isEditMode){showHeightEditDialog = true}
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                color = Color(0xFF666666)
                            )
                            ProfileItem(label = "Gender", value = if (userSex) "Male" else "Female", isEditMode){showSexEditDialog = true}
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .padding(8.dp)
                            .width(250.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(
                            text = "Delete Profile &\n Disconnect from Google Fit",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
    if (showNameEditDialog) {
        tempUserName = userName
        EditDialog(
            title = "Edit Name",
            textValue = tempUserName,
            userId = userId,
            onConfirm ={ newName ->
                viewModel.updateUserName(userId, newName)
                tempUserName = newName
                showNameEditDialog = false },
            onDismiss = { showNameEditDialog = false }
        )
    }
    if (showSexEditDialog) {
        val initialSex = if (userSex) "Male" else "Female"
        EditDialogSex(
            title = "Edit Sex",
            initialSex = initialSex,
            userId = userId,
            onConfirm = { newSex ->
                viewModel.updateUserSex(userId, newSex.toBoolean())
                showSexEditDialog = false},
            onDismiss = {showSexEditDialog = false}
        )
    }
    if (showAgeEditDialog) {
        tempUserAge = userAge.toString()
        val currentAge = tempUserAge.toIntOrNull() ?: 18
        EditDialogAge(
            title = "Edit Age",
            ageValue = currentAge,
            userId = userId,
            onConfirm = { newAge ->
                viewModel.viewModelScope.launch {
                    viewModel.updateUserAge(userId, newAge)
                }
                showAgeEditDialog = false },
            onDismiss = {showAgeEditDialog = false}
        )
    }
    if (showHeightEditDialog) {
        tempUserHeight = userHeight.toString()
        val currentHeight = tempUserHeight.toIntOrNull() ?: 160
        EditDialogHeight(
            title = "Edit Height",
            heightValue = currentHeight,
            userId = userId,
            onConfirm = { newHeight ->
                viewModel.viewModelScope.launch {
                    viewModel.updateUserHeight(userId, newHeight)
                }
                showHeightEditDialog = false},
            onDismiss = {showHeightEditDialog = false}
        )
    }
    if (showWeightEditDialog) {
        tempUserWeight = userWeight.toString()
        val currentWeight: Double = tempUserWeight.toDoubleOrNull() ?: 160.0

        EditDialogWeight(
            title = "Edit Weight",
            weightValue = currentWeight,
            userId = userId,
            onConfirm = { newWeight ->
                viewModel.viewModelScope.launch {
                    viewModel.updateUserWeight(userId, newWeight)
                }
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
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626),
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
                        .fillMaxWidth()
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
                        cursorColor = Color.Blue,
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Black,
                        disabledIndicatorColor = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.padding(16.dp))
                // Button row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Dismiss button
                    Button(
                        onClick = { onDismiss() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel" , color = Color.White)
                    }

                    // Confirm button
                    Button(
                        onClick = {
                            onConfirm(text)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserName(userId, text)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613))
                    ) {
                        Text("Save" , color = Color.White)
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
    var selectedSex by remember { mutableStateOf(initialSex) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626),
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
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.Center)
                        .padding(top= 16.dp)
                )
                Column(modifier = Modifier.padding(all = 8.dp)) {
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
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text ="Cancel", color = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val isMale = selectedSex == "Male"
                            onConfirm(selectedSex)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserSex(userId, isMale)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613))
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }

}

@Composable
fun EditDialogAge(title: String, ageValue: Int, userId: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var age by remember { mutableIntStateOf(ageValue) }
    val viewModel: HomeViewModel = hiltViewModel()

    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    Text("Age: $age years", color = Color(0xFFd3d3d3))
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
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel" , color = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(age)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserAge(userId, age)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613))
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }

}
@Composable
fun EditDialogHeight(title: String, heightValue: Int, userId: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var height by remember { mutableIntStateOf(heightValue) }
    val viewModel: HomeViewModel = hiltViewModel()

    Dialog(onDismissRequest = {  }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    Text("Height: $height cm", color = Color(0xFFd3d3d3))
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
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(height)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserHeight(userId, height)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613))
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }

}
@Composable
fun EditDialogWeight(title: String, weightValue: Double, userId: Int, onConfirm: (Double) -> Unit, onDismiss: () -> Unit) {
    var weight by remember { mutableDoubleStateOf(weightValue) }
    val viewModel: HomeViewModel = hiltViewModel()

    Dialog(onDismissRequest = {  }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = Color(0xFF262626),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    Text("Weight: $weight kg", color = Color(0xFFd3d3d3))
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
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel" , color = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(weight)
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserWeight(userId, weight)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00a613))
                    ) {
                        Text("Save" , color = Color.White)
                    }
                }
            }
        }
    }
}
