package kr.saintdev.gametrans.engine.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kr.saintdev.gametrans.engine.broadcast.GameToggleBroadcast
import kr.saintdev.gametrans.libs.util.BroadcastManager
import kr.saintdev.gametrans.libs.util.sql.SQLManager

class GameTranService : AccessibilityService() {
    private var nowPlaying: String? = null
    private val receiver = GameToggleBroadcast()

    /**
     * 서비스가 시작되었습니다.
     * 게임 번역기를 준비 합니다.
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        BroadcastManager.registerBroadcastReceiver(receiver, this, "kr.saintdev.pst.gametoggle")
    }

    /**
     * 서비스가 꺼졌습니다.
     * 게임 번역기도 종료합니다.
     */
    override fun onDestroy() {
        super.onDestroy()
        BroadcastManager.unregisterBroadcastReceiver(this, receiver)
    }

    val IGNORE_PACKAGES = arrayOf(
            "kr.saintdev.gametrans"
    )
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // 윈도우 변경이 감지 됨.
            val pkgName = event.packageName.toString()
            if(IGNORE_PACKAGES.contains(pkgName)) return

            val applicationObject = SQLManager.Game.get(pkgName, this)

            Log.d("GMT", "DETECT : $pkgName")

            // 이 앱이 게임 번역기에 등록되었는지 확인함
            if(nowPlaying == pkgName) return

            if(!(nowPlaying != null && nowPlaying.equals(pkgName)) && applicationObject != null) {
                gameRunningBroadcast(pkgName)
                nowPlaying = pkgName
            } else {
                if(nowPlaying != null) {
                    gameStoppingBroadcast(nowPlaying!!)
                    nowPlaying = null
                }
            }
        }
    }

    private fun gameRunningBroadcast(pkgName: String) {
        val intent = Intent()
        intent.action = "kr.saintdev.pst.gametoggle"
        intent.putExtra("package-name", pkgName)
        intent.putExtra("toggle", true)
        sendBroadcast(intent)

        Log.d("GMT", "RUNNING : $pkgName")
    }

    private fun gameStoppingBroadcast(pkgName: String) {
        val intent = Intent()
        intent.action = "kr.saintdev.pst.gametoggle"
        intent.putExtra("package-name", pkgName)
        intent.putExtra("toggle", false)
        sendBroadcast(intent)

        Log.d("GMT", "EXIT : $pkgName")
    }

    override fun onInterrupt() {

    }
}