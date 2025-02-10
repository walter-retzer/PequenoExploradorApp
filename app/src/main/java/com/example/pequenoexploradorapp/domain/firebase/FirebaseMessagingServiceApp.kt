package com.example.pequenoexploradorapp.domain.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.media.RingtoneManager
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.ConstantsApp.Companion.CHANNEL_FIREBASE_CLOUD_MESSAGING
import com.example.pequenoexploradorapp.domain.util.ConstantsApp.Companion.TAG_FIREBASE_MESSAGING
import com.example.pequenoexploradorapp.presentation.MainActivity
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseMessagingServiceApp : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { message ->
            sendNotification(message)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG_FIREBASE_MESSAGING, "Refreshed token: $token")
    }

    private fun sendNotification(message: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 1001, intent, FLAG_IMMUTABLE
        )

        val channelId = CHANNEL_FIREBASE_CLOUD_MESSAGING
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_astronaut)
            .setColor(mainColor.toArgb())
            .setColorized(true)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "FCM", IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
        manager.notify(Random.nextInt(), notificationBuilder.build())
    }
}
