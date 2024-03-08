package com.example.bakis.consumables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    onEditClick: () -> Unit = {},
    showEditIcon: Boolean = false,
) {
    var inEditMode by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF262626) ),
        title = {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF262626))
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White

                )
            }
        },

        actions = {
            if (showEditIcon) {
                IconButton(onClick = {
                    inEditMode = !inEditMode
                    onEditClick() // This now effectively toggles edit mode.
                }) {
                    Icon(
                        imageVector = if (inEditMode) Icons.Default.Done else Icons.Default.Edit,
                        contentDescription = if (inEditMode) "Done" else "Edit",
                        tint = Color.White // Set the icon color to white
                    )
                }
            }
        }

    )
}

