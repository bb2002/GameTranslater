package kr.saintdev.gametrans.views.activitys

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.util.InstalledPackageManager
import kr.saintdev.gametrans.views.fragments.gameregi.CompleteFragmnt
import kr.saintdev.gametrans.views.fragments.gameregi.IntroFragment
import kr.saintdev.gametrans.views.fragments.gameregi.PkgListActivity

class GameRegisterActivity : AppCompatActivity() {
    private val processFragments = arrayOf(
            IntroFragment(),
            PkgListActivity(),
            CompleteFragmnt()
    )
    private var indexPointer = 0
    var selectedItems: ArrayList<InstalledPackageManager.ApplicationObject> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_register)

        changeFragment(0)
    }

    /**
     * Fragment index 를 교체 한다.
     */
    fun changeFragment(i: Int?) {
        val index = i ?: indexPointer ++
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.game_register_container, processFragments[index]).commit()
        this.indexPointer = index
    }
}