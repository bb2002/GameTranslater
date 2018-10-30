package kr.saintdev.gametrans.engine.service

import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.permission.MediaProj
import kr.saintdev.gametrans.libs.permission.MediaProj.getMediaProjectionIntent
import kr.saintdev.gametrans.libs.util.SystemOverlay
import kr.saintdev.gametrans.libs.util.checkAPILevel
import kr.saintdev.gametrans.libs.util.getStr

/**
 * 사용자의 디바이스에 시작 버튼 오버레이를 표시합니다.
 * 이 서비스를 시작하여 게임 번역기 프로시저 시작점이 됩니다.
 * 화면 캡쳐도 시작합니다.
 */

class RunButtonOverlay : Service(), View.OnClickListener {
    private var wmManager: WindowManager? = null
    private val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if(checkAPILevel(Build.VERSION_CODES.O)) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
    )

    private lateinit var view: View
    private lateinit var startButton: Button
    private var isReady = false         // 번역기 준비 여부

    override fun onCreate() {
        super.onCreate()

        // inflate view
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.view = inflater.inflate(R.layout.layout_start_button, null)
        this.startButton = this.view.findViewById(R.id.overlay_trans_start)
        params.gravity = Gravity.RIGHT or Gravity.TOP

        this.wmManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        this.startButton.setOnClickListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // show overlay
        if(SystemOverlay.isGrantedSystemOverlay(this)) {
            try {
                this.wmManager?.addView(this.view, params)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            SystemOverlay.openSystemOverlayGrantActivity(this)
            stopSelf()
        }

        // Display capture 서비스를 시작 한다.
        if(getMediaProjectionIntent() == null) {
            view.findViewById<TextView>(R.id.overlay_trans_start).text = R.string.prepare_translate.getStr(this)
            isReady = false
        } else {
            val i = Intent(this, DisplayCaptureService::class.java)
            i.putExtra("command", "init")
            startService(i)
            this.isReady = true
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            val i = Intent(this, DisplayCaptureService::class.java)
            stopService(i)
            this.wmManager?.removeView(this.view)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        // [디스플레이 캡쳐 서비스] -> 번역 오버레이 서비스 -> 시작 버튼 재시작
        val i = Intent(this, DisplayCaptureService::class.java)
        i.putExtra("command", "capture")
        startService(i)

        stopSelf()
    }

    override fun onBind(intent: Intent?) = null
}