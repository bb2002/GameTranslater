package kr.saintdev.gametrans.libs.window

import android.app.ProgressDialog
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kr.saintdev.gametrans.libs.util.getStr

/**
 * 단순한 경고창을 발생시킵니다.
 * @param title 다이얼로그 타이틀
 * @param activity 타겟 엑티비티
 */
fun String.openAlert(title: Int, activity: AppCompatActivity) {
    val builder = AlertDialog.Builder(activity)
    builder.setTitle(title).setMessage(this).setPositiveButton("OK") {
        dialog, _ -> dialog.dismiss()
    }
    builder.create().show()
}

/**
 * Confirm 경고창을 발생시킵니다.
 */
fun String.openConfirm(title: String, activity: AppCompatActivity, listener: DialogInterface.OnClickListener, yesText: String = "OK", noText: String = "Deny") {
    val builder = AlertDialog.Builder(activity)
    builder.setTitle(title).setMessage(this)
            .setPositiveButton(yesText, listener)
            .setNegativeButton(noText, listener)
    builder.create().show()
}

/**
 * 로딩중 다이얼로그를 발생 시킵니다.
 */
fun String.openProgress(title: Int, activity: AppCompatActivity) : ProgressDialog {
    val dialog = ProgressDialog(activity)
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    dialog.setMessage(title.getStr(activity))
    dialog.setCancelable(false)
    dialog.show()

    return dialog
}