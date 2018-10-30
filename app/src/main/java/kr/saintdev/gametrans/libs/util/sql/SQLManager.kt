package kr.saintdev.gametrans.libs.util.sql

import android.content.Context
import kr.saintdev.gametrans.R
import kr.saintdev.gametrans.libs.util.InstalledPackageManager
import kr.saintdev.gametrans.libs.util.getDrawable

object SQLManager {
    var dbm: DatabaseManager? = null

    object Game {
        fun add(app: InstalledPackageManager.ApplicationObject, context: Context) {
            connectDB(context)

            if(get(app.pkgName, context) == null) {
                // 없을 경우 새로 추가 한다.
                dbm?.query("INSERT INTO `tb_translate_games`" +
                        "(package_name, app_name, created)" +
                        "VALUES (?,?,datetime('now'))", arrayOf(app.pkgName, app.appName))
                dbm?.close()
            }
        }

        fun get(pkgName: String, context: Context) : InstalledPackageManager.ApplicationObject? {
            connectDB(context)

            val cursor = dbm?.resultQuery("SELECT * FROM `tb_translate_games` WHERE package_name = '$pkgName'")

            return if(cursor != null && cursor.moveToNext()) {
                val pkgName = cursor.getString(1)
                val appName = cursor.getString(2)

                // 여기를 통해 얻은 값에는 아이콘이 없습니다.
                InstalledPackageManager.ApplicationObject(pkgName, appName, R.drawable.app_icon.getDrawable(context))
            } else {
                null
            }
        }

        fun getAll(context: Context) : ArrayList<InstalledPackageManager.ApplicationObject> {
            connectDB(context)

            val cs = dbm?.resultQuery("SELECT * FROM `tb_translate_games`")
            val pkgs = arrayListOf<InstalledPackageManager.ApplicationObject>()
            if(cs != null) {
                while (cs.moveToNext()) {
                    pkgs.add(
                            InstalledPackageManager.ApplicationObject(cs.getString(2),
                            cs.getString(1),
                            R.drawable.app_icon.getDrawable(context))
                    )
                }
            }

            return pkgs
        }

        fun remove(pkgName: String, context: Context) {
            connectDB(context)

            dbm?.query("DELETE FROM `tb_protector_repo` WHERE package_name = ?", arrayOf(pkgName))
        }
    }

    fun connectDB(context: Context) {
        if(dbm == null) {
            dbm = DatabaseManager(context)
        }
    }

    fun closeDB() {
        dbm?.close()
        dbm = null
    }
}