package ru.vais.serviceexample

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import ru.vais.serviceexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var page = 1
    lateinit var binding: ActivityMainBinding
    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = (service as? MyForegroundService.LocalBinder) ?: return
            val foregroundService = binder.gerService()
            foregroundService.onProgressChanged = {progress ->
                binding.progressBarLoading.progress = progress
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.simpleService.setOnClickListener {
            startService(MyService.newIntent(this, "Посылка из Активити"))
        }
        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this))
        }
        binding.workManager.setOnClickListener {
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.enqueueUniqueWork(
                MyWorker.WORK_NAME,
                ExistingWorkPolicy.APPEND,
                MyWorker.makeRequest(page = page++)
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    override fun onStart() {
        super.onStart()
        bindService(MyForegroundService.newIntent(this),
            serviceConnection,
            0)
    }
}