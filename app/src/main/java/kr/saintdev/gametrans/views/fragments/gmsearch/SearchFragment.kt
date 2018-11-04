package kr.saintdev.gametrans.views.fragments.gmsearch

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragmn_search_myapps.*
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.network.interfaces.GameFilterService
import kr.saintdev.gametrans.libs.network.models.GameObject
import kr.saintdev.gametrans.libs.network.netlib.NetContant.retrofit
import kr.saintdev.gametrans.libs.util.InstalledPackageManager
import kr.saintdev.gametrans.libs.util.getStr
import kr.saintdev.gametrans.libs.util.sql.SQLManager
import kr.saintdev.gametrans.libs.window.openAlert
import kr.saintdev.gametrans.views.activitys.SearchGameActivity
import kr.saintdev.gametrans.views.adapters.DefaultAppAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class SearchFragment : Fragment() {
    private lateinit var nextButton: FloatingActionButton
    private var adapter: DefaultAppAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragmn_search_myapps, container, false)

        this.nextButton = v.findViewById(R.id.next_button)
        this.nextButton.setOnClickListener {
            if(this.adapter != null) {
                val items = this.adapter!!.items
                for(i in items) SQLManager.Game.add(i, context!!)
                (activity as SearchGameActivity).changeFragment(2)
            }
        }

        // 휴대폰에 설치된 게임을 가져온다.
        InstalledPackageManager.requestInstalledPkgs(context!!, OnPackageSearchedListener())
        return v
    }

    inner class OnPackageSearchedListener : InstalledPackageManager.OnInstallPkgListener {
        override fun onRequested(apps: ArrayList<InstalledPackageManager.ApplicationObject>) {
            if(apps.isEmpty()) {
                pkg_searching.text = R.string.game_search_empty.getStr(context!!)
            } else {
                // 설치된 게임을 가져왔습니다.
                // 서버에 요청하여 게임 목록을 가져옵니다.
                val checkedGamesArray = arrayListOf<InstalledPackageManager.ApplicationObject>()
                val filterService = retrofit.create(GameFilterService::class.java)
                val call = filterService.getAllGames()
                call.enqueue(object : Callback<List<GameObject>> {
                    override fun onFailure(call: Call<List<GameObject>>?, t: Throwable?) {
                        R.string.game_search_failed.getStr(context!!).openAlert(R.string.error, activity as AppCompatActivity)
                    }

                    override fun onResponse(call: Call<List<GameObject>>?, response: Response<List<GameObject>>?) {
                        // 게임만 필터링 합니다.
                        val result = response?.body()
                        if(result == null) {
                            onFailure(null, null)
                        } else {
                            result.forEach {
                                val gameObj = it
                                val list = apps.filter {
                                    it.pkgName == gameObj.pkgName
                                }

                                if(list.isNotEmpty()) {
                                    checkedGamesArray.add(list[0])
                                }
                            }
                        }

                        if(checkedGamesArray.isNotEmpty()) {
                            adapter = DefaultAppAdapter(checkedGamesArray)
                            pkg_listview.adapter = adapter
                            pkg_searching.visibility = View.GONE
                            next_button.visibility = View.VISIBLE
                        } else {
                            pkg_searching.visibility = View.VISIBLE
                            pkg_searching.text = R.string.game_search_empty.getStr(context!!)
                        }
                    }
                })
            }
        }
    }
}