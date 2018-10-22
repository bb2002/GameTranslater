package kr.saintdev.pst

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import kr.saintdev.pst.libs.views.ItemButtonView
import kr.saintdev.pst.views.activitys.GameRegisterActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
