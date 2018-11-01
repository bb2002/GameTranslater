package kr.saintdev.gametrans.views.activitys

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.views.fragments.gmsearch.IntroFragment
import kr.saintdev.gametrans.views.fragments.gmsearch.SearchFragment

class SearchGameActivity : AppCompatActivity() {
    val FRAGMENTS = arrayOf(
        IntroFragment(), SearchFragment()
    )
    private var indexPointer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_game)

        changeFragment(0)
    }

    /**
     * Fragment index 를 교체 한다.
     */
    fun changeFragment(i: Int?) {
        val index = i ?: indexPointer ++
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.search_game_container, FRAGMENTS[index]).commit()
        this.indexPointer = index
    }
}