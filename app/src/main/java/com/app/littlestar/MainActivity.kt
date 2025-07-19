package com.app.littlestar

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.littlestar.compose.CandleScreen
import com.app.littlestar.listener.MicVolumeListener

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WelcomeScreen()
        }
    }

    @Composable
    fun WelcomeScreen() {
        var text by remember { mutableStateOf("") }
        var isConfirmed by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                if (!isConfirmed) {
                    Text(
                        text = "Please enter your name:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Your Name here") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { isConfirmed = true },
                        enabled = text.isNotBlank()
                    ) {
                        Text(text = "Confirm")
                    }
                } else {
                    MainScreen()
                }

            }
        }

    }

    @Composable
    fun MainScreen() {
        val current = LocalContext.current

        var hasMicPermission by remember {
            mutableStateOf(ContextCompat.checkSelfPermission(
                current,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED)
        }

        val micPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasMicPermission = isGranted
        }

        LaunchedEffect(Unit) {
            if (!hasMicPermission) {
                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        if (hasMicPermission) {
            MicrophoneVolumeHandler()
        } else {
            Text("This app needs microphone permission to work properly.")
        }
    }


    @Composable
    fun MicrophoneVolumeHandler() {
        var micVolume by remember { mutableStateOf(0) }
        val threshold = 2000
        val isBlowing = micVolume > threshold

        val micListener = remember { MicVolumeListener() }

        LaunchedEffect(Unit) {
            micListener.startListening { volume ->
                micVolume = volume
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                micListener.stopListening()
            }
        }

        CandleScreen(isBlowing = isBlowing)
    }

}

