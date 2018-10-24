package kr.saintdev.gametrans.libs.util.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 5252b on 2018-01-25.
 */

public class DatabaseManager extends SQLiteOpenHelper {
    SQLiteDatabase db = null;

    public DatabaseManager(Context context) {
        super(context, "gametrans.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(InitQuerys.TRANSLATE_GAME_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean query(String query, Object[] args) {
        try {
            this.db = getWritableDatabase();
            this.db.execSQL(query, args);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    public Cursor resultQuery(String query) {
        try {
            this.db = getReadableDatabase();
            Cursor c = this.db.rawQuery(query, null);

            return c;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void close() {
        if(this.db != null)  this.db.close();
    }
}
