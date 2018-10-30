package kr.saintdev.gametrans.libs.network.netlib

import android.content.Context
import android.net.ConnectivityManager

object Netstat {
    fun isNetworkConnecting(context: Context) : Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val netInfo = connManager.activeNetworkInfo
        return netInfo.isConnected
    }
}