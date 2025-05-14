package com.example.timewdroid

import android.Manifest
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import java.util.concurrent.TimeUnit
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.timewdroid.ui.theme.TimewdroidTheme
import kotlinx.coroutines.delay
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var channel = NotificationChannel(
            "timer_channel",
            "Timer Notifications",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun showTimerNotification(context: Context, label: String, time: String) {
    val notification = NotificationCompat.Builder(context, "timer_channel")
        .setContentTitle(label)
        .setContentText(time)
        .setSmallIcon(R.drawable.ic_lock_idle_alarm)
        .setOngoing(true)
        .build()

    val manager = NotificationManagerCompat.from(context)
    manager.notify(1, notification)
}

fun cancelTimerNotification(context: Context) {
    val manager = NotificationManagerCompat.from(context)
    manager.cancel(1)
}

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
fun TimerDisplay(
    modifier: Modifier = Modifier,
    onTimerStopped: (elapsedSeconds: Long) -> Unit = {}
) {
    val context = LocalContext.current

    var tag by remember { mutableStateOf("") }
    var timerRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timerRunning) {
                delay(1000L)
                elapsedTime += 1

                val formattedTime = formatTime(elapsedTime)
                showTimerNotification(context, tag, formattedTime)
            }
        }
        else {
            cancelTimerNotification(context)
        }
    }

    val formattedTime = formatTime(elapsedTime)

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


fun formatTime(elapsedTime: Long): String {
    val hours = TimeUnit.SECONDS.toHours(elapsedTime)
    val minutes = TimeUnit.SECONDS.toMinutes(elapsedTime) % 60
    val seconds = elapsedTime % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
