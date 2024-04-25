package com.example.bakis.composables

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun CustomTopAppBar(
    title: String,
    onEditClick: () -> Unit = {},
    showEditIcon: Boolean = false,
    showBackButton: Boolean = false,
) {
    var inEditMode by remember { mutableStateOf(false) }
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    // Custom AppBar layout
    Surface(color = Color(0xFF262626)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Back button or spacer
            if (showBackButton) {
                IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(0.1f))
            } else {
                Spacer(modifier = Modifier.weight(0.2f))
            }

            // Title, centered within its space
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier
                    .weight(0.6f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            // Edit/Done button or spacer
            if (showEditIcon) {
                Spacer(modifier = Modifier.weight(0.1f))
                IconButton(onClick = {
                    inEditMode = !inEditMode
                    onEditClick()
                }) {
                    Icon(
                        imageVector = if (inEditMode) Icons.Filled.Done else Icons.Filled.Edit,
                        contentDescription = if (inEditMode) "Done" else "Edit",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(0.2f))
            }
        }
    }
}



