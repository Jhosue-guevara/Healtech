package com.example.healtech

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    lateinit var intet_nombre: String
    private lateinit var mediaPlayer: MediaPlayer

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarmId",-1)

        val nombreM = intent.getStringExtra("nombreM")

        // Detén el sonido de la alarma
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarmasong)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Crea el canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "Alarmas"
            val channelDescription = "Notificaciones para tomar medicamentos"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription
            channel.enableLights(true)
            channel.lightColor = Color.RED

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


        // Crea una notificación para mostrar la alarma
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra("alarmId", alarmId)
        val pendingIntent = PendingIntent.getActivity(context, alarmId, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(context, "my_channel_id")
            .setContentTitle("Alarma Medicamento")
            .setContentText("!Es hora del Medicamento $nombreM!")
            .setSmallIcon(R.drawable.ic_receta)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(alarmId, notificationBuilder.build())
    }

}
