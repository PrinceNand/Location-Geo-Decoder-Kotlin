package com.project.locationdecoder

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.locationdecoder.ui.theme.LocationDecoderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationDecoderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LocationViewModel = viewModel()
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(locationViewModel: LocationViewModel) {
    val context = LocalContext.current
    val location = LocationUtils(context)
    LocationDisplay(locationUtils = location, context = context, locationViewModel)
}

@Composable
fun LocationDisplay(locationUtils: LocationUtils, context: Context, viewModel: LocationViewModel) {

    val location = viewModel.locationData.value

    // Launcher to get multiple permission
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permission ->
                if (permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    && permission[Manifest.permission.ACCESS_FINE_LOCATION] == true
                ) {
                    // I HAVE ACCESS to location
                    locationUtils.requestLocationUpdates(viewModel = viewModel)
                } else {

                    val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)

                    if(rationaleRequired){
                        Toast.makeText(context,
                            "Location Permission is required for this feature to work", Toast.LENGTH_LONG)
                            .show()
                    }else{
                        Toast.makeText(context,
                            "Location Permission is required. Please enable it in the Android Settings",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }

            })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if(location != null){
            Text("Address: ${location.latitude} ${location.longitude}")
        }else{
            Text(text = "Location not available")
        }

        Button(onClick = {
            if (locationUtils.hasLocationPermission(context)) {
                // Permission granted
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                // Request for permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get Location")
        }
    }
}