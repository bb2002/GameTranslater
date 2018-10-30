package kr.saintdev.gametrans

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.internal.GoogleApiManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import kotlinx.android.synthetic.main.activity_main.*
import kr.saintdev.gametrans.libs.network.apis.User
import kr.saintdev.gametrans.libs.network.models.ResponseProperty
import kr.saintdev.gametrans.libs.network.netlib.aesDecode
import kr.saintdev.gametrans.libs.permission.MediaProj.REQUEST_MEDIA_PROJECTION
import kr.saintdev.gametrans.libs.permission.MediaProj.getMediaProjectionIntent
import kr.saintdev.gametrans.libs.permission.MediaProj.openRequestMediaProjection
import kr.saintdev.gametrans.libs.permission.MediaProj.setMediaProjectionIntent
import kr.saintdev.gametrans.libs.properties.EnvProperties
import kr.saintdev.gametrans.libs.properties.EnvProperties.get
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID
import kr.saintdev.gametrans.libs.util.AuthManager
import kr.saintdev.gametrans.libs.util.getStr
import kr.saintdev.gametrans.libs.views.ItemButtonView
import kr.saintdev.gametrans.libs.window.openAlert
import kr.saintdev.gametrans.views.activitys.AuthActivity
import kr.saintdev.gametrans.views.activitys.GameRegisterActivity
import kr.saintdev.gametrans.views.activitys.MyGameActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 사용자의 로그인 여부를 확인 한다.
        if(AuthManager.isLoginned() == null || !AuthManager.isGameTranslateAuth(this)) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        val clickListener = OnItemClickHandler()
        menu_game_add.setOnClickListener(clickListener)
        menu_my_game.setOnClickListener(clickListener)
        menu_game_search.setOnClickListener(clickListener)
        menu_charge_coin.setOnClickListener(clickListener)
        menu_help.setOnClickListener(clickListener)
        menu_app_info.setOnClickListener(clickListener)

        updateRemainSceneView(null)     // 현재 프로퍼티 상태 업데이트
        updateProperty()                        // 프로퍼티 상태 업데이트
    }

    override fun onResume() {
        super.onResume()

        // MediaProjection 이 없다면 실행 한다.
        if(getMediaProjectionIntent() == null) {
            openRequestMediaProjection(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_MEDIA_PROJECTION && data != null) {
            setMediaProjectionIntent(data)
        }
    }

    /**
     * 남은 씬을 업데이트 합니다.
     * @param scene? null 을 입력하면 캐시에서 불러옵니다.
     */
    private fun updateRemainSceneView(scene: Int?) {
        val rem = (scene ?: (get(EnvPropertiesID.PROPERTY_REMAIN_SCENE, this) ?: 0)).toString()
        main_remain_size.text = rem
    }

    private fun updateProperty() {
        User.requestProperty(this, object : Callback<ResponseProperty> {
            override fun onResponse(call: Call<ResponseProperty>?, response: Response<ResponseProperty>?) {
                val body = response?.body()
                if(body == null || body.code != 200) {
                    reqFailed()
                } else {
                    // Property update.
                    val data = arrayOf(
                            body.remainScene.aesDecode(this@MainActivity),
                            body.consumeScene.aesDecode(this@MainActivity),
                            body.chargeScene.aesDecode(this@MainActivity))
                    val keys = arrayOf(
                            EnvPropertiesID.PROPERTY_REMAIN_SCENE,
                            EnvPropertiesID.PROPERTY_CONSUME_SCENE,
                            EnvPropertiesID.PROPERTY_CHARGE_SCENE)

                    for(i in 0 until 3) {
                        if(data[i] != null) {
                            EnvProperties.set(keys[i], data[i]!!, this@MainActivity)
                        }
                    }
                    updateRemainSceneView((data[0]?:"").toInt())
                }
            }

            override fun onFailure(call: Call<ResponseProperty>?, t: Throwable?) = reqFailed()

            private fun reqFailed() {
                R.string.update_property_failed.getStr(this@MainActivity).openAlert(R.string.error, this@MainActivity)
            }
        })
    }

    inner class OnItemClickHandler : View.OnClickListener {
        override fun onClick(v: View) {
            val clazz = when (v.id) {
                R.id.menu_game_add -> GameRegisterActivity::class.java
                R.id.menu_my_game -> MyGameActivity::class.java
                else -> null
            }

            if (clazz != null) {
                val intent = Intent(this@MainActivity, clazz)
                startActivity(intent)
            }
        }
    }
}
