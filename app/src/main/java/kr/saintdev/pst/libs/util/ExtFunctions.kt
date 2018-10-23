package kr.saintdev.pst.libs.util

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

fun Int.getStr(context: Context) = context.getString(this)

fun Int.getDrawable(context: Context) = context.getDrawable(this)

object BroadcastManager {
    /**
     * @param receiver Broadcast receiver
     * @param context 컨텍스트
     * @param actions 방송 송출할 엑션
     * actions 를 브로드케스트 리시버에 등록합니다.
     */
    @TargetApi(26)
    fun registerBroadcastReceiver(receiver: BroadcastReceiver, context: Context, vararg actions: String) {
        if (checkAPILevel(Build.VERSION_CODES.O)) {
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            for (a in actions) filter.addAction(a)
            context.registerReceiver(receiver, filter)
        }
    }

    /**
     * @param receiver Broadcast receiver
     * @param context 컨텍스트
     * 브로드케스트 리시버를 해제 합니다.
     */
    @TargetApi(26)
    fun unregisterBroadcastReceiver(context: Context, receiver: BroadcastReceiver) {
        if (checkAPILevel(Build.VERSION_CODES.O)) {
            context.unregisterReceiver(receiver)
        }
    }
}

object SystemOverlay {
    /**
     * @param context 컨텍스트
     * @return System overlay 승인 여부 (승인 : true)
     */
    fun isGrantedSystemOverlay(context: Context) =
            !(checkAPILevel(Build.VERSION_CODES.M) && !Settings.canDrawOverlays(context))

    /**
     * @param context 컨텍스트
     */
    fun openSystemOverlayGrantActivity(context: Context) {
        if(!isGrantedSystemOverlay(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
            context.startActivity(intent)
        }
    }
}

/**
 * @param level 타겟 sdk 레벨 입니다.
 * @return 해당 sdk 이상 만족하는지 에 대한 결과값
 */
fun checkAPILevel(level: Int) = android.os.Build.VERSION.SDK_INT >= level