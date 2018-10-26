package kr.saintdev.gametrans.views.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.network.interfaces.KakaoAuthService
import kr.saintdev.gametrans.libs.network.models.KakaoLoginResponse
import kr.saintdev.gametrans.libs.network.netlib.AESSecurity
import kr.saintdev.gametrans.libs.network.netlib.DefaultValue.EMAIL_DEFAULT
import kr.saintdev.gametrans.libs.network.netlib.DefaultValue.PHOTO_DEFAULT
import kr.saintdev.gametrans.libs.network.netlib.NetContant.retrofit
import kr.saintdev.gametrans.libs.network.netlib.Netstat
import kr.saintdev.gametrans.libs.network.netlib.aesDecode
import kr.saintdev.gametrans.libs.network.netlib.aesEncode
import kr.saintdev.gametrans.libs.util.getStr
import kr.saintdev.gametrans.libs.window.openAlert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class AuthActivity : AppCompatActivity() {
    private lateinit var callback: KakaoLoginCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Network status
        if(Netstat.isNetworkConnecting(this)) {
            // Kakao login session
            callback = KakaoLoginCallback()
            Session.getCurrentSession().addCallback(callback)
            Session.getCurrentSession().checkAndImplicitOpen()
        } else {
            // Network is not connected.
            (R.string.error_network_not_connect.getStr(this)).openAlert(R.string.error, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback);
    }

    /**
     * 카카오 로그인이 완료되었다.
     */
    inner class KakaoLoginCallback : ISessionCallback {
        override fun onSessionOpenFailed(exception: KakaoException?) {
            // 오류 경고창 띄우기
            (R.string.error_kakao_failed.getStr(this@AuthActivity) + exception?.message)
                    .openAlert(R.string.error, this@AuthActivity)
        }

        override fun onSessionOpened() {
            requestMe()         // 세션을 열고 사용자 인증을 시작한다.
        }

        private fun requestMe() {
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response) {
                    val email = (result.kakaoAccount.email?: EMAIL_DEFAULT).aesEncode(this@AuthActivity)       // null 위험 제거
                    val photoUrl = (result.profileImagePath?: PHOTO_DEFAULT).aesEncode(this@AuthActivity)
                    val uuid = result.id.toString().aesEncode(this@AuthActivity)
                    val nickname = result.nickname.aesEncode(this@AuthActivity)

                    if((arrayOf(email, photoUrl, uuid, nickname).filter { it == null }).count() == 0) {
                        // null 이 전혀 없다면
                        authForGameTranslate(uuid!!, nickname!!, email!!, photoUrl!!)
                    }
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    // 오류 경고창 띄우기
                    (R.string.error_kakao_failed.getStr(this@AuthActivity) + errorResult?.errorMessage)
                            .openAlert(R.string.error, this@AuthActivity)
                }
            })
        }

        /**
         * 카카오 로그인을 마치고 게임 번역기에 인증을 요청한다.
         */
        private fun authForGameTranslate(authKey: String, nickname: String, email: String = EMAIL_DEFAULT, photoUrl: String = PHOTO_DEFAULT) {
            val kakaoAuthService = retrofit.create(KakaoAuthService::class.java)
            val call = kakaoAuthService.kakaoLoginAuth(authKey, nickname, email, photoUrl)
            call.enqueue(object : Callback<KakaoLoginResponse> {
                override fun onResponse(call: Call<KakaoLoginResponse>, response: Response<KakaoLoginResponse>) {
                    val loginResponse = response.body()
                    Log.d("GMT", "결과 : ${loginResponse?.secretKey?.aesDecode(this@AuthActivity)}")
                }

                override fun onFailure(call: Call<KakaoLoginResponse>?, t: Throwable?) {
                    (R.string.error_network_auth.getStr(this@AuthActivity) + t?.message)
                            .openAlert(R.string.error, this@AuthActivity)
                }
            })
        }
    }
}