package ru.vais.serviceexample

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.coroutineScope
import kotlin.concurrent.thread

class MyForegroundService : Service() {

    private val notificationBuilder by lazy {
        createNotificationBuilder()
    }

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
    var onProgressChanged: ((Int) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        createNotificationChannel()

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        thread {
            for (i in 0..100 step (5)) {
                Thread.sleep(1000)
                val notification = notificationBuilder.setProgress(100, i, false)
                    .build()
                notificationManager.notify(NOTIFICATION_ID, notification)
                onProgressChanged?.invoke(i)
                log("Timer $i")
            }
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyForegroundService ${this.hashCode()}: $message")
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    private fun createNotificationBuilder() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Title")
        .setContentText("Запущен сервис")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setProgress(100, 0, false)
        .setOnlyAlertOnce(true)


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    inner class LocalBinder: Binder() {
        fun gerService() = this@MyForegroundService
    }

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel"
        private const val NOTIFICATION_ID = 1

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MyForegroundService::class.java)
            return intent
        }
    }
}
