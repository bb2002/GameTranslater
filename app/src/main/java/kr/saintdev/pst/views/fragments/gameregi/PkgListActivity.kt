package kr.saintdev.pst.views.fragments.gameregi

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.fragmn_pkg_list.*
import kotlinx.android.synthetic.main.item_button.*
import kr.saintdev.pst.R
import kr.saintdev.pst.libs.util.InstalledPackageManager
import kr.saintdev.pst.libs.util.InstalledPackageManager.requestInstalledPkgs
import kr.saintdev.pst.libs.util.getStr
import kr.saintdev.pst.views.activitys.GameRegisterActivity
import kr.saintdev.pst.views.adapters.PkgListAdapter
import java.util.ArrayList

class PkgListActivity : Fragment() {
    private lateinit var v: View
    private var adapter: PkgListAdapter? = null
    private lateinit var nextButton: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragmn_pkg_list, container, false)
        this.nextButton = v.findViewById(R.id.next_button)
        this.nextButton.setOnClickListener {
            clickNextButton()
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        startSearchInstalledPkgs()
    }

    /**
     * 설치된 앱 들을 찾습니다.
     */
    private fun startSearchInstalledPkgs() {
        pkg_searching.visibility = View.VISIBLE
        requestInstalledPkgs(context!!, object : InstalledPackageManager.OnInstallPkgListener {
            override fun onRequested(result: ArrayList<InstalledPackageManager.ApplicationObject>) {
                resetInstalledItem(result)
            }
        })
    }

    /**
     * 설치된 앱들을 찾았습니다.
     */
    private fun resetInstalledItem(apps: ArrayList<InstalledPackageManager.ApplicationObject>) {
        pkg_searching.visibility = View.INVISIBLE
        this.adapter = PkgListAdapter(apps)
        pkg_listview.adapter = this.adapter
    }

    private fun clickNextButton() {
        // 선택된 앱들을 불러온다.
        val checkedApps = this.adapter!!.getCheckedItems()

        if(checkedApps.size == 0) {
            // 선택해주세요.
            Toast.makeText(activity, R.string.game_list_error1, Toast.LENGTH_SHORT).show()
        } else {
            // 등록 화면으로 이동 한다.
            val act = activity as GameRegisterActivity
            act.selectedItems = checkedApps
            act.changeFragment(2)
        }
    }
}