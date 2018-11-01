package kr.saintdev.gametrans.libs.network.models

import com.google.gson.annotations.SerializedName

data class GameObject(
        @SerializedName("package_name") val pkgName: String,
        @SerializedName("app_name") val appName: String,
        @SerializedName("app_icon") val appIcon: String,
        @SerializedName("app_developer") val appDeveloper: String,
        @SerializedName("is_game") val isGame: String)