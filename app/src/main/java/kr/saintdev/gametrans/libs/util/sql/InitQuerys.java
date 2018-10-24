package kr.saintdev.gametrans.libs.util.sql;

/**
 * Created by 5252b on 2018-01-25.
 */

public interface InitQuerys {
    /*
        Game Protector 가 보호해야할 앱의 정보
     */
    String TRANSLATE_GAME_QUERY =
            "CREATE TABLE `tb_translate_games` (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "package_name TEXT NOT NULL," +             // 패키지 명
            "app_name TEXT NOT NULL," +                 // 앱 이름
            "created DATETIME);";
}
