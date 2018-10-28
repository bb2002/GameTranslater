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
import kr.saintdev.gametrans.libs.properties.EnvProperties
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID
import kr.saintdev.gametrans.libs.util.AuthManager
import kr.saintdev.gametrans.libs.views.ItemButtonView
import kr.saintdev.gametrans.views.activitys.AuthActivity
import kr.saintdev.gametrans.views.activitys.GameRegisterActivity

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
    }

    inner class OnItemClickHandler : View.OnClickListener {
        override fun onClick(v: View) {
            val clazz = when (v.id) {
                R.id.menu_game_add -> GameRegisterActivity::class.java
                else -> null
            }

            if (clazz != null) {
                val intent = Intent(this@MainActivity, clazz)
                startActivity(intent)
            }
        }
    }
}
