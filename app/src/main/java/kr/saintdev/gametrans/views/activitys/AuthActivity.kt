package kr.saintdev.gametrans.views.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.network.interfaces.GoogleAuthService
import kr.saintdev.gametrans.libs.network.interfaces.KakaoAuthService
import kr.saintdev.gametrans.libs.network.models.GameTranslateLoginResponse
import kr.saintdev.gametrans.libs.network.netlib.DefaultValue.EMAIL_DEFAULT
import kr.saintdev.gametrans.libs.network.netlib.DefaultValue.PHOTO_DEFAULT
import kr.saintdev.gametrans.libs.network.netlib.NetContant.retrofit
import kr.saintdev.gametrans.libs.network.netlib.Netstat
import kr.saintdev.gametrans.libs.network.netlib.aesDecode
import kr.saintdev.gametrans.libs.network.netlib.aesEncode
import kr.saintdev.gametrans.libs.util.ForDevelopment
import kr.saintdev.gametrans.libs.util.getStr
import kr.saintdev.gametrans.libs.window.openAlert
import kr.saintdev.gametrans.libs.window.openProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.AuthCredential
import com.kakao.usermgmt.callback.LogoutResponseCallback
import kr.saintdev.gametrans.MainActivity
import kr.saintdev.gametrans.libs.properties.EnvProperties
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID.AUTH_SECRET_KEY
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID.AUTH_TYPE
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID.PROPERTY_CHARGE_SCENE
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID.PROPERTY_CONSUME_SCENE
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID.PROPERTY_REMAIN_SCENE


class AuthActivity : AppCompatActivity() {
    private lateinit var callback: KakaoLoginCallback
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.e("GMT", ForDevelopment.getSecretKey(this))

        // 로그인 전, 모두 로그아웃 한다.
        FirebaseAuth.getInstance().signOut()
        UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() { override fun onCompleteLogout() {} })
        EnvProperties.reset(this)       // 환경 변수 초기화

        // Network status
        if(Netstat.isNetworkConnecting(this)) {
            // Kakao login session
            callback = KakaoLoginCallback()
            Session.getCurrentSession().addCallback(callback)
            Session.getCurrentSession().checkAndImplicitOpen()

            // google login setup
            googleLoginSetup()
            auth_google_login.setOnClickListener {
                val signIn = Auth.GoogleSignInApi.getSignInIntent(this.googleApiClient)
                startActivityForResult(signIn, AUTH_GOOGLE_LOGIN)
            }

        } else {
            // Network is not connected.
            (R.string.error_network_not_connect.getStr(this)).openAlert(R.string.error, this)
        }
    }

    private fun googleLoginSetup() {
        this.firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(R.string.default_web_client_id.getStr(this))
                .requestEmail().build()

        this.googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) {
                    if(!it.isSuccess) R.string.error_google_load_error.getStr(this).openAlert(R.string.error, this)
                }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    private val AUTH_GOOGLE_LOGIN = 0x2
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AUTH_GOOGLE_LOGIN && data != null) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.signInAccount != null) {
                firebaseAuthWithGoogle(result.signInAccount!!)
            } else {
                (R.string.error_google_failed.getStr(this@AuthActivity)).openAlert(R.string.error, this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
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
    }

    /**
     * 구글 로그인이 완료되어 파이어베이스에 등록 한다.
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    val user = FirebaseAuth.getInstance().currentUser

                    // 게임 번역기 서버에 데이터를 전송 할 준비를 한다.
                    if(it.isSuccessful && user != null) {
                        val email = (user.email ?: EMAIL_DEFAULT).aesEncode(this)
                        val photoUrl = (if (user.photoUrl.toString() == "null") PHOTO_DEFAULT else account.photoUrl.toString()).aesEncode(this)
                        val uuid = user.uid?.aesEncode(this)
                        val nickname = user.displayName?.aesEncode(this)

                        googleApiClient.disconnect()

                        if ((arrayOf(email, photoUrl, uuid, nickname).filter { it == null }).count() == 0) {
                            // null 이 전혀 없다면
                            authForGameTranslate(uuid!!, nickname!!, email!!, photoUrl!!, 1)
                        }
                    } else {
                        R.string.error_firebase_failed.getStr(this)
                                .openAlert(R.string.error, this@AuthActivity)
                    }
                }
    }

    /**
     * 소셜 로그인을 마치고 게임 번역기 서버에 계정을 등록합니다.
     */
    private fun authForGameTranslate(authKey: String, nickname: String, email: String = EMAIL_DEFAULT, photoUrl: String = PHOTO_DEFAULT, type: Int = 0) {
        val dialog = R.string.authing.getStr(this).openProgress(this@AuthActivity)

        fun onErrorOccurred(msg: String? = "") {
            (R.string.error_network_auth.getStr(this@AuthActivity) + msg)
                    .openAlert(R.string.error, this@AuthActivity)
        }

        fun createLoginSession(response: GameTranslateLoginResponse, type: String) {
            EnvProperties.set(AUTH_TYPE, type, this@AuthActivity)
            EnvProperties.set(AUTH_SECRET_KEY, response.secretKey, this@AuthActivity)

            // 씬 정산을 처리 한다.
            EnvProperties.set(PROPERTY_CHARGE_SCENE, response.chargeScene.toString(), this)
            EnvProperties.set(PROPERTY_CONSUME_SCENE, response.consumeScene.toString(), this)
            EnvProperties.set(PROPERTY_REMAIN_SCENE, response.remainScene.toString(), this)

            // 메인 엑티비티를 연다.
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        if(type == 0) {
            val kakaoAuthService = retrofit.create(KakaoAuthService::class.java)
            val call = kakaoAuthService.kakaoLoginAuth(authKey, nickname, email, photoUrl)
            call.enqueue(object : Callback<GameTranslateLoginResponse> {
                override fun onResponse(call: Call<GameTranslateLoginResponse>, response: Response<GameTranslateLoginResponse>) {
                    val loginResponse = response.body()
                    dialog.dismiss()

                    createLoginSession(loginResponse, "kakao")
                }

                override fun onFailure(call: Call<GameTranslateLoginResponse>?, t: Throwable?) = onErrorOccurred(t?.message)
            })
        } else if(type == 1){
            // 구글 로그인을 시도 한다.
            val googleAuthService = retrofit.create(GoogleAuthService::class.java)
            val call = googleAuthService.googleLoginAuth(authKey, nickname, email, photoUrl)
            call.enqueue(object : Callback<GameTranslateLoginResponse> {
                override fun onResponse(call: Call<GameTranslateLoginResponse>, response: Response<GameTranslateLoginResponse>) {
                    val loginResponse = response.body()
                    dialog.dismiss()

                    createLoginSession(loginResponse, "google")
                }

                override fun onFailure(call: Call<GameTranslateLoginResponse>?, t: Throwable?) = onErrorOccurred(t?.message)
            })
        }
    }
}