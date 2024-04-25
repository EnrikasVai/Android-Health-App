package com.example.bakis

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.bakis.navigation.SetupNavigation
import com.example.bakis.ui.theme.BakisTheme
import com.example.bakis.viewmodel.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_WRITE)
            .build()
    }

    // Handling the result of Google Fit permissions request
    private val googleFitPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // TODO: Access Google Fit data here or indicate the user that the app is ready
                Toast.makeText(this, "Permissions granted, ready to access Google Fit data.", Toast.LENGTH_LONG).show()
                setupContent()
            } else {
                // TODO: Inform the user about the necessity of Google Fit permissions for functionality
                Toast.makeText(this, "Permissions denied, the app needs these permissions to function.", Toast.LENGTH_LONG).show()
                requestGoogleFitPermissions()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if permissions are already granted
        if (checkGoogleFitPermissionGranted()) {
            setupContent()
        } else {
            // Request Google Fit permissions
            requestGoogleFitPermissions()
        }
    }
    private fun setupContent() {
        setContent {
            BakisTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val homeViewModel: HomeViewModel = viewModel()
                    SetupNavigation(navController, homeViewModel)
                }
            }
        }
    }
    private fun checkGoogleFitPermissionGranted(): Boolean {
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        return GoogleSignIn.hasPermissions(account, fitnessOptions)
    }

    private fun requestGoogleFitPermissions() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // Or any other options you need
            .addExtension(fitnessOptions) // Important for Google Fit
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, signInOptions)

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            googleFitPermissionLauncher.launch(googleSignInClient.signInIntent)
        } else {
            // TODO: Access Google Fit data here or indicate the user that the app is ready
            Toast.makeText(this, "Permissions already granted", Toast.LENGTH_LONG).show()
            setupContent()
        }
    }
}