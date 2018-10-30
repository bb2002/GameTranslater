package kr.saintdev.gametrans.libs.network.netlib

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetContant {
    val END_POINT = "http://api.saintdev.kr/~gmt/api.php/"
    val TIME_OUT = 5000
    val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(NetContant.END_POINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

object DefaultValue {
    val EMAIL_DEFAULT = "unknown@unknown.com"
    val PHOTO_DEFAULT = "http://api.saintdev.kr/~gmt/default_icon.png"
}