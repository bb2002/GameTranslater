package kr.saintdev.pst.views.fragments.gameregi

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.saintdev.pst.R
import kr.saintdev.pst.views.activitys.GameRegisterActivity

class EmptyFragment : Fragment() {
    private lateinit var v: View

    private lateinit var nextButton: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        v = inflater.inflate(R.layout.fragmn_empty, container, false)
        return v
    }


}