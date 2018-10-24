package kr.saintdev.gametrans.views.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.LoginButton
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import kr.saintdev.gametrans.R

class AuthActivity : AppCompatActivity() {
    private lateinit var callback: KakaoLoginCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Kakao login session
        callback = KakaoLoginCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()
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

    inner class KakaoLoginCallback : ISessionCallback {
        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.d("GMT", "ERROR!! : ${exception?.message}")
        }

        override fun onSessionOpened() {
            requestMe()
        }

        private fun requestMe() {
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response) {
                    Log.d("GMT", "NICKNAME : ${result.nickname}")
                    Log.d("GMT", "EMAIL : ${result.kakaoAccount.email}")
                    Log.d("GMT", "PROFILE PATH : ${result.nickname}")
                    Log.d("GMT", "THUME PATH : ${result.nickname}")
                    Log.d("GMT", "UUID : ${result.nickname}")
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.e("GMT", "msg : ${errorResult?.errorMessage}")
                }
            })
        }
    }
}