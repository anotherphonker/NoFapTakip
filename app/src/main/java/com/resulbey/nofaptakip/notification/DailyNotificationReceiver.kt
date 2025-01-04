package com.resulbey.nofaptakip.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.resulbey.nofaptakip.R
import java.util.*

class DailyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("NoFapPrefs", Context.MODE_PRIVATE)
        val startDate = prefs.getLong("startDate", 0)
        if (startDate == 0L) return

        val currentStreak = calculateStreak(startDate)
        val message = when {
            currentStreak == 1 -> "İlk günü başarıyla tamamladın! Bu yeni başlangıcın tadını çıkar."
            currentStreak == 3 -> "3 gün oldu! Kararlılık rozetini kazandın. Devam et!"
            currentStreak == 7 -> "Bir hafta oldu! Beynin iyileşmeye başladı bile."
            currentStreak == 14 -> "İki haftalık savaşçı oldun! Yeni alışkanlıklar oluşturmaya başladın."
            currentStreak == 30 -> "Bir aylık şampiyon! Dopamin seviyen dengeleniyor."
            currentStreak == 90 -> "90 gün! Beynin tamamen yenilendi."
            currentStreak == 180 -> "180 gün! Artık tam bir kontrol ustasısın."
            currentStreak == 365 -> "Bir yıl! Sen artık bir ilham kaynağısın."
            else -> "Bugün ${currentStreak}. gün! Her gün yeni bir zafer!"
        }

        showNotification(context, message)
    }

    private fun calculateStreak(startDate: Long): Int {
        val start = Calendar.getInstance().apply { timeInMillis = startDate }
        val now = Calendar.getInstance()
        return ((now.timeInMillis - start.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()
    }

    private fun showNotification(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_motivation"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Günlük Motivasyon",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Günlük motivasyon mesajları ve rozet bildirimleri"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("NoFap Takip")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
} 