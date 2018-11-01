package kr.saintdev.gametrans.views.fragments.gmsearch

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.views.activitys.SearchGameActivity

class IntroFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragmn_search_intro, container, false)
        v.findViewById<FloatingActionButton>(R.id.next_button).setOnClickListener {
            (activity as SearchGameActivity).changeFragment(1)
        }

        return v
    }
}