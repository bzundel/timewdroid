package com.example.timewdroid

import java.util.concurrent.TimeUnit
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.ModifierLocalModifierNode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timewdroid.ui.theme.TimewdroidTheme
import kotlinx.coroutines.delay
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimewdroidTheme {
                Scaffold(modifier = Modifier.fillMaxWidth()) { innerPadding ->
                    TimerDisplay()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
@Preview
fun TimerDisplay(
    modifier: Modifier = Modifier,
    onTimerStopped: (elapsedSeconds: Long) -> Unit = {}
) {
    var tag by remember { mutableStateOf("") }
    var timerRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }

    LaunchedEffect(timerRunning) {
        while (timerRunning) {
            delay(1000L)
            elapsedTime += 1
        }
    }
    
    val formattedTime = remember(elapsedTime) {
        val hours = TimeUnit.SECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.SECONDS.toMinutes(elapsedTime) % 60
        val seconds = elapsedTime % 60
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = formattedTime, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (timerRunning) {
                    timerRunning = false
                    onTimerStopped(elapsedTime)
                }
                else {
                    elapsedTime = 0L
                    timerRunning = true
                }
            }) {
                Text(if (timerRunning) "Stop" else "Start")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = tag,
                onValueChange = { tag = it },
                label = { Text("Label") },
                enabled = !timerRunning
            )
        }
    }
}
