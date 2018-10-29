package kr.saintdev.gametrans.libs.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kr.saintdev.gametrans.libs.network.netlib.aesDecode
import kr.saintdev.gametrans.libs.properties.EnvProperties
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID

object AuthManager {
    /**
     * 사용자가 로그인 되어 있는지 확인한다.
     * @return 로그인 타입
     * @return null 은 로그인 되지 않음
     */
    fun isLoginned() =
            when {
                Session.getCurrentSession().isOpened -> "kakao"
                FirebaseAuth.getInstance().currentUser != null -> "google"
                else -> null
            }

    /**
     * 사용자가 게임 번역기 서비스에 인증되어있는지 확인
     */
    fun isGameTranslateAuth(context: Context) =
        EnvProperties.get(EnvPropertiesID.AUTH_SECRET_KEY, context) != null

    /**
     * 인증 키를 요청 한다.
     */
    fun requestAuthKey(auth: OnResponseAuthKey) {
        val type = isLoginned()
        when(type) {
            "kakao" -> {
                UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                    override fun onSuccess(result: MeV2Response?) {
                        val key = result?.id?.toString()
                        auth.onResponse(key)
                    }

                    override fun onSessionClosed(errorResult: ErrorResult?) = auth.onResponse(null)
                })
            }
            "google" -> auth.onResponse(FirebaseAuth.getInstance().currentUser?.uid)
        }
    }

    interface OnResponseAuthKey {
        fun onResponse(key: String?)
    }

    /**
     * 보안 키를 가져온다.
     */
    fun getSecretKey(context: Context) = EnvProperties.get(EnvPropertiesID.AUTH_SECRET_KEY, context)?.aesDecode(context)
}