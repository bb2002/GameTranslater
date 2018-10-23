package kr.saintdev.pst.views.fragments.gameregi

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.saintdev.pst.R
import kr.saintdev.pst.libs.util.sql.SQLManager
import kr.saintdev.pst.views.activitys.GameRegisterActivity

class CompleteFragmnt : Fragment() {
    private lateinit var nextButton: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragmn_game_regist_comp, container, false)
        val acti = (activity as GameRegisterActivity)

        this.nextButton = v.findViewById(R.id.next_button)
        this.nextButton.setOnClickListener {
            acti.finish()
        }


        // 목록에 있는 게임들을 등록한다.
        val games = acti.selectedItems
        val ctx = context
        if(ctx != null) {
            for (g in games) {
                SQLManager.Game.add(g, ctx)
            }
        }

        // DB 작업을 마치고 닫는다.
        SQLManager.closeDB()

        return v
    }
}