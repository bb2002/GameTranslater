package kr.saintdev.pst.engine.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import kr.saintdev.pst.R
import kr.saintdev.pst.libs.util.SystemOverlay
import kr.saintdev.pst.libs.util.checkAPILevel

/**
 * 사용자의 디바이스에 시작 버튼 오버레이를 표시합니다.
 * 이 서비스를 시작하여 게임 번역기 프로시저 시작점이 됩니다.
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

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            this.wmManager?.removeView(this.view)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        Toast.makeText(this, "CLICK", Toast.LENGTH_SHORT).show();
    }

    override fun onBind(intent: Intent?) = null
}