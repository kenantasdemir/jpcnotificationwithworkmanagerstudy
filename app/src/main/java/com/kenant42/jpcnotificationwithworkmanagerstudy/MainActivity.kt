package com.kenant42.jpcnotificationwithworkmanagerstudy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kenant42.jpcnotificationwithworkmanagerstudy.ui.theme.JpcnotificationwithworkmanagerstudyTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JpcnotificationwithworkmanagerstudyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Sayfa()
                }
            }
        }
    }
}


@Composable
fun Sayfa() {
    var isGranted by remember { mutableStateOf(false) }
    val mContext = LocalContext.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                isGranted = true
            } else {
                isGranted = false
            }
        }
    Column(
        modifier = Modifier.padding(top = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(onClick = {
            //  requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }) {
            Text(text = "Bildirim İzni Al")
        }

        Button(onClick = {
            //createNotification(mContext )
            buildNotification(mContext)
            Log.e("HATA", "HATA")
        }) {
            Text(text = "CREATE NOTİFİCATİON")
        }

        Button(onClick = {

            val workConstraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            val request = OneTimeWorkRequestBuilder<CustomWorkerNotification>()
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setConstraints(workConstraint)
                .build()


            WorkManager.getInstance(mContext).enqueue(request)
        }) {
            Text(text = "ONE TIME WORK REQUEST")
        }



        Button(onClick = { /*TODO*/ }) {
            Text(text = "PERIODIC WORK REQUEST")

            val periodicWorkRequest =
                PeriodicWorkRequestBuilder<CustomWorkerNotification>(10, TimeUnit.SECONDS)
                    .setInitialDelay(5, TimeUnit.SECONDS)
                    .build()

            WorkManager.getInstance(mContext).enqueue(periodicWorkRequest)

        }


    }
}


fun buildNotification(context: Context) {
    val builder: NotificationCompat.Builder
    val notManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val channelID = "1"
        val channelName = "ChannelName"
        val channelDescription = "ChannelDescription"
        val channelPriority = NotificationManager.IMPORTANCE_HIGH


        var customChannel: NotificationChannel? = notManager.getNotificationChannel(channelID)

        if (customChannel == null) {
            customChannel = NotificationChannel(channelID, channelName, channelPriority)
            customChannel.description = channelDescription
            notManager.createNotificationChannel(customChannel)
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        builder = NotificationCompat.Builder(context, channelID)

        builder.setContentTitle("Başlık")
            .setContentText("İçerik")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.airplane)
            .setAutoCancel(true)

    } else {
        builder = NotificationCompat.Builder(context)

        builder.setContentTitle("HEADING")
            .setContentText("BODY")
            .setSmallIcon(R.drawable.airplane)
            .setAutoCancel(true)
            .priority = Notification.PRIORITY_HIGH
    }

    notManager.notify(1, builder.build())
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JpcnotificationwithworkmanagerstudyTheme {
        Sayfa()
    }
}