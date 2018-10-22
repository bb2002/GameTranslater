package kr.saintdev.pst.libs.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import java.util.ArrayList

object InstalledPackageManager {
    /**
     * 비동기 처리 권장, 설치된 앱 목록 가져옴.
     */
    fun requestInstalledPkgs(context: Context, listener: OnInstallPkgListener) {
        val task = BackgroundTask(context, listener)
        task.execute()
    }

    fun getApplicationName(context: Context, pkgName: String) : String? {
        return try {
            context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES)).toString()
        } catch (nex: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getApplicationIcon(context: Context, pkgName: String) : Drawable? {
        return try {
            context.packageManager.getApplicationIcon(pkgName)
        } catch (nex: PackageManager.NameNotFoundException) {
            nex.printStackTrace()
            null
        }
    }

    data class ApplicationObject(val appName: String, val pkgName: String, val appIcon: Drawable)

    private class BackgroundTask(val context: Context, val listener: OnInstallPkgListener) : AsyncTask<Void, Void, ArrayList<ApplicationObject>>() {
        override fun doInBackground(vararg params: Void): ArrayList<ApplicationObject> {
            val installedPackages = context.packageManager.getInstalledPackages(0)
            val installedApps = ArrayList<ApplicationObject>()

            for (pkg in installedPackages) {
                val pkgName = pkg.packageName
                val appName = getApplicationName(context, pkgName)
                val appIcon = getApplicationIcon(context, pkgName)
                if(appName != null && appIcon != null) {
                    val appObject = ApplicationObject(appName, pkgName, appIcon)
                    installedApps.add(appObject)
                }
            }

            return installedApps
        }

        override fun onPostExecute(result: ArrayList<ApplicationObject>) {
            super.onPostExecute(result)
            listener.onRequested(result)
        }
    }

    interface OnInstallPkgListener {
        fun onRequested(result: ArrayList<ApplicationObject>)
    }
}