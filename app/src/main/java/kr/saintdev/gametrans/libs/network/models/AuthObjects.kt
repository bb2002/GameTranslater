package kr.saintdev.gametrans.libs.network.models

import com.google.gson.annotations.SerializedName

data class GameTranslateLoginResponse(
        @SerializedName("isNew") val isNew: Boolean,     // 새로 생성된 계정인가?
        @SerializedName("secretKey") val secretKey: String,  // 인증 후 키 값
        @SerializedName("remainScene") val remainScene: Int,   // 현재 남은 씬
        @SerializedName("consumeScene") val consumeScene: Int,  // 지금까지 사용한 씬
        @SerializedName("chargeScene") val chargeScene: Int    // 지금까지 충전한 씬
)