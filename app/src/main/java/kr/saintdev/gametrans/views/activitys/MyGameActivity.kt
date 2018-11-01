package kr.saintdev.gametrans.views.activitys

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_my_games.*
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.util.InstalledPackageManager
import kr.saintdev.gametrans.libs.util.getStr
import kr.saintdev.gametrans.libs.util.sql.SQLManager
import kr.saintdev.gametrans.libs.window.openProgress
import kr.saintdev.gametrans.views.adapters.DeleteAppAdapter
import java.util.ArrayList

class MyGameActivity : AppCompatActivity() {
    private lateinit var adapter: DeleteAppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_games)

        val pgDialog = R.string.searching.getStr(this).openProgress(this)
        InstalledPackageManager.requestInstalledPkgs(this, OnResponsePackages(pgDialog))
    }

    inner class OnResponsePackages(val dialog: ProgressDialog) : InstalledPackageManager.OnInstallPkgListener {
        override fun onRequested(result: ArrayList<InstalledPackageManager.ApplicationObject>) {
            dialog.dismiss()

            // Database 에서 등록된 Application 을 호출한다.
            val registedGames = SQLManager.Game.getAll(this@MyGameActivity)
            // result 에 휴대폰에 설치된 게임들이 있다.

            val resultGames = arrayListOf<InstalledPackageManager.ApplicationObject>()
            // 목록을 표시 한다.
            // 휴대폰에 설치되어있으며, DB 에 등록된 게임을 찾는다.
            for(regGame in registedGames) {
                for(insGame in result) {
                    if(regGame.pkgName == insGame.pkgName) {
                        resultGames.add(insGame)
                    }
                }
            }

            if(resultGames.isEmpty()) {
                // 비어있다.
                mygames_list.visibility = View.GONE
                mygames_empty_set.visibility = View.VISIBLE
            } else {
                adapter = DeleteAppAdapter(resultGames, OnDeleteClicked())
                mygames_list.adapter = adapter
            }
        }
    }

    inner class OnDeleteClicked : DeleteAppAdapter.OnDeleteClickListener {
        override fun onClick(view: View, idx: Int) {
            val item = adapter.getItem(idx)
            SQLManager.Game.remove(item.pkgName, this@MyGameActivity)
            adapter.remove(idx)

            Toast.makeText(this@MyGameActivity, R.string.mygames_deleted, Toast.LENGTH_SHORT).show();
        }
    }
}