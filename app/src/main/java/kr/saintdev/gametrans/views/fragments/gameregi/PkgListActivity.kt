package kr.saintdev.gametrans.views.fragments.gameregi

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragmn_pkg_list.*
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.util.InstalledPackageManager
import kr.saintdev.gametrans.libs.util.InstalledPackageManager.requestInstalledPkgs
import kr.saintdev.gametrans.libs.util.sql.SQLManager
import kr.saintdev.gametrans.views.activitys.GameRegisterActivity
import kr.saintdev.gametrans.views.adapters.CheckboxAppAdapter
import java.util.ArrayList

class PkgListActivity : Fragment() {
    private lateinit var v: View
    private var adapter: CheckboxAppAdapter? = null
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
        // 이미 등록된 패키지는 목록에서 제외합니다.
        val filteredApps = apps.filter {
            SQLManager.Game.get(it.pkgName, context!!) == null
        }
        this.adapter = CheckboxAppAdapter(filteredApps)
        pkg_listview.adapter = this.adapter

        if(filteredApps.isEmpty()) {
            pkg_searching.setText(R.string.game_regi_empty)
        } else {
            pkg_searching.visibility = View.INVISIBLE
        }
    }

    private fun clickNextButton() {
        // 선택된 앱들을 불러온다.
        val checkedItems = this.adapter?.getCheckedItems()
        val appList = this.adapter?.items

        if(checkedItems != null && appList != null) {
            if(checkedItems.size == 0) {
                // 선택해주세요.
                Toast.makeText(activity, R.string.game_list_error1, Toast.LENGTH_SHORT).show()
            } else {
                // 선택된 아이템을 ApplicationObject 로 받아오고
                // 부모 엑티비티에 적용한다.
                val appObjects = arrayListOf<InstalledPackageManager.ApplicationObject>()
                for(i in checkedItems) {
                    appObjects.add(appList[i])
                }
                val act = activity as GameRegisterActivity
                act.selectedItems = appObjects
                act.changeFragment(2)
            }
        }
    }
}