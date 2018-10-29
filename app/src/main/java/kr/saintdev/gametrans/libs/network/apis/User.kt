package kr.saintdev.gametrans.libs.network.apis

import android.content.Context
import kr.saintdev.gametrans.libs.network.interfaces.UserService
import kr.saintdev.gametrans.libs.network.models.ResponseProperty
import kr.saintdev.gametrans.libs.network.netlib.NetContant.retrofit
import kr.saintdev.gametrans.libs.network.netlib.aesEncode
import kr.saintdev.gametrans.libs.properties.EnvProperties
import kr.saintdev.gametrans.libs.properties.EnvPropertiesID
import kr.saintdev.gametrans.libs.util.AuthManager
import retrofit2.Callback

object User {
    fun requestProperty(context: Context, callback: Callback<ResponseProperty>) {
        val secKey = AuthManager.getSecretKey(context)?.aesEncode(context)
        if(secKey != null) {
            AuthManager.requestAuthKey(object : AuthManager.OnResponseAuthKey {
                override fun onResponse(key: String?) {
                    val encKey = key?.aesEncode(context)
                    if (encKey != null) {
                        val userService = retrofit.create(UserService::class.java)
                        val call = userService.requestProperty(
                            secKey, encKey)
                        call.enqueue(callback)
                    }
                }
            })
        }
    }
}