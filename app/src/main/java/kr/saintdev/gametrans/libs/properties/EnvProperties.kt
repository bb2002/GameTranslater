package kr.saintdev.gametrans.libs.properties

import android.content.Context
import android.content.SharedPreferences

object EnvProperties {
    var pref: SharedPreferences? = null
    val PREFERENCE_ID = "gametranslate.pref"

    fun get(property: String, context: Context) : String? {
        if(pref == null)
            openPreference(context)
        return this.pref!!.getString(property, null)
    }

    fun set(property: String, data: String, context: Context) {
        if(pref == null)
            openPreference(context)
        val edit = this.pref!!.edit()
        edit.putString(property, data)
        edit.apply()
    }

    fun reset(context: Context) {
        if(pref == null)
            openPreference(context)

        // 모든 값을 초기화 시킵니다.
        pref!!.edit().clear().apply()
    }

    fun openPreference(context: Context) {
        this.pref = context.getSharedPreferences(PREFERENCE_ID, Context.MODE_PRIVATE)
    }
}