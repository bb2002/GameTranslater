package kr.saintdev.gametrans.libs.network.interfaces

import kr.saintdev.gametrans.libs.network.models.KakaoLoginResponse
import kr.saintdev.gametrans.libs.network.netlib.NetContant
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Path

interface KakaoAuthService {
    @POST("auth/kakaologin/{authKey}/{userName}/{userEmail}/{photoUrl}")
    fun kakaoLoginAuth (
            @Path("authKey") authKey: String,
            @Path("userName") userName: String,
            @Path("userEmail") userEmail: String,
            @Path("photoUrl") photoUrl: String) : Call<KakaoLoginResponse>
}