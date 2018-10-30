package kr.saintdev.gametrans.libs.permission

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.support.v7.app.AppCompatActivity

object MediaProj {
    private var mediaProjectionIntent: Intent? = null
    const val REQUEST_MEDIA_PROJECTION = 0x0

    /**
     * @param activity Target Activity
     * MediaProjection 을 요청하는 함수
     */
    fun openRequestMediaProjection(activity: AppCompatActivity) {
        val mpm = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        activity.startActivityForResult(mpm.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
    }

    /**
     * @param data MediaProjection Intent
     */
    fun setMediaProjectionIntent(data: Intent) {
        this.mediaProjectionIntent = data
    }

    /**
     * @return MediaProjection Intent (nullable)
     */
    fun getMediaProjectionIntent() = mediaProjectionIntent
}