package com.kenant42.jpcnotificationwithworkmanagerstudy
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class CustomWorkerNotification(appContext:Context,workerParams:WorkerParameters):Worker(appContext,workerParams) {
    override fun doWork(): Result {
        buildNotification(applicationContext)
        return Result.success()
    }

    fun buildNotification(context: Context){
        val builder:NotificationCompat.Builder
        val notManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channelID = "1"
            val channelName ="ChannelName"
            val channelDescription = "ChannelDescription"
            val channelPriority = NotificationManager.IMPORTANCE_HIGH


            var customChannel:NotificationChannel? = notManager.getNotificationChannel(channelID)

            if (customChannel == null){
                customChannel = NotificationChannel(channelID,channelName,channelPriority)
                customChannel.description = channelDescription
                notManager.createNotificationChannel(customChannel)
            }

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            builder = NotificationCompat.Builder(context,channelID)

            builder.setContentTitle("Başlık")
                .setContentText("İçerik")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.airplane)
                .setAutoCancel(true)

        }else{
            builder = NotificationCompat.Builder(context)

            builder.setContentTitle("HEADING")
                .setContentText("BODY")
                .setSmallIcon(R.drawable.airplane)
                .setAutoCancel(true)
                .priority = Notification.PRIORITY_HIGH
        }

        notManager.notify(1,builder.build())
    }

}