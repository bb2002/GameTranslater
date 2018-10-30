package kr.saintdev.gametrans.libs.network.models

import com.google.gson.annotations.SerializedName

data class ResponseProperty(
        @SerializedName("Property.remain.scene") val remainScene: String,
        @SerializedName("Property.consume.scene") val consumeScene: String,
        @SerializedName("Property.charge.scene") val chargeScene: String,
        @SerializedName("code") val code: Int,
        @SerializedName("msg") val msg: String)