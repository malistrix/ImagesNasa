package ru.evdokimova.imagesnasa.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.evdokimova.imagesnasa.R


/**
 * Создать всплывающее уведомление
 */
fun makeStatusNotification(
    message: String,
    context: Context,
    myChannel: MyChannels,
    notificationId: Int,
    notificationTitle: String,
    paddingIntent: PendingIntent?
) {
    // Создать NotificationChannel для API 26+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(myChannel.id, myChannel.nameChannel, importance)
        channel.description = myChannel.description
        // Добавление канала
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }

    // Создать уведомление
    val builder = NotificationCompat.Builder(context, myChannel.id)
        .setSmallIcon(R.drawable.ic_launcher_grade__foreground_24)
        .setContentTitle(notificationTitle)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setVibrate(LongArray(0))
        .setContentIntent(paddingIntent)

    // Показать уведомление
    NotificationManagerCompat.from(context).notify(notificationId, builder.build())
}

/**
 * Каналы уведомлений
 */
enum class MyChannels(
    val id: String,
    val nameChannel: String,
    val description: String,
) {
    SAVE_IMAGE(
        "Saving image",
        "Image save notification",
        "Shows notifications whenever image saving",
    )
}



