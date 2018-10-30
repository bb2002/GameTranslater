package kr.saintdev.gametrans.libs.network.interfaces

import kr.saintdev.gametrans.libs.network.models.ResponseProperty
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    /**
     * 이 사용자의 프로퍼티 값을 요청 한다.
     */
    @GET("user/property/{secretKey}/{authKey}")
    fun requestProperty(
            @Path("secretKey") secretKey: String,
            @Path("authKey") authKey: String) : Call<ResponseProperty>
}