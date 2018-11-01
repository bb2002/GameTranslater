package kr.saintdev.gametrans.libs.network.interfaces

import kr.saintdev.gametrans.libs.network.models.GameObject
import retrofit2.Call
import retrofit2.http.GET

interface GameFilterService {
    @GET("api/gameall/")
    fun getAllGames() : Call<List<GameObject>>
}