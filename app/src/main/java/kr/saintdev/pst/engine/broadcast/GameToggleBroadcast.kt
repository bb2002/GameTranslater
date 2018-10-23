package kr.saintdev.pst.engine.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kr.saintdev.pst.engine.service.RunButtonOverlay

class GameToggleBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val toggle = intent.getBooleanExtra("toggle", false)
        val intent = Intent(context, RunButtonOverlay::class.java)

        if(toggle) {
            // 오버레이 서비스 시작
            context?.startService(intent)
        } else {
            // 오버레이 서비스 종료
            context?.stopService(intent)
        }
    }

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }
}