package com.example.gestordeproyectis.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.gestordeproyectis.MainActivity
import com.example.gestordeproyectis.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val titulo = intent.getStringExtra("titulo")
            ?: "Actividad pendiente"

        val actividadId = intent.getIntExtra(
            "actividadId",
            0
        )

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channelId = "recordatorios"

        val channel = NotificationChannel(
            channelId,
            "Recordatorios",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(channel)

        val abrirIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            putExtra("actividadId", actividadId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            actividadId,
            abrirIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Recordatorio")
            .setContentText(titulo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(
            actividadId,
            notification
        )
    }
}
