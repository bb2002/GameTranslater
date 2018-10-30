package kr.saintdev.gametrans.engine.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class TranslateOverlayService : Service() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val path = intent?.getStringExtra("path")
        Log.d("GMT", "CAPTURE ::: $path")

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}