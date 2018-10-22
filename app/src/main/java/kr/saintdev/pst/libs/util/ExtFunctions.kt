package kr.saintdev.pst.libs.util

import android.content.Context

fun Int.getStr(context: Context) = context.getString(this)

fun Int.getDrawable(context: Context) = context.getDrawable(this)
