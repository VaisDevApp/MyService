package ru.vais.serviceexample

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlin.concurrent.thread

class MyService : Service() {
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val str = intent?.getStringExtra("STR") ?: "0"
        log("onStartCommand: $str")
        thread {
            for (i in 0..10) {
                Thread.sleep(1000)
                log("Timer $i")
            }
        }


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyService ${this.hashCode()}: $message")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {
        fun newIntent(context: Context, str: String): Intent {
            val intent = Intent(context, MyService::class.java)
            intent.putExtra("STR", str)
            return intent
        }
    }
}
