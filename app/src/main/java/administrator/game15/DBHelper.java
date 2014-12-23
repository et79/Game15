package administrator.game15;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by administrator on 14/12/22.
 */

public class DBHelper extends SQLiteOpenHelper {
    static final String TABLENAME = "15GameData";

    public DBHelper(Context c){
        super(c, "score.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String s = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (id INT PROMARY KEY, data TEXT)";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldver, int newver){
        String s = "DROP TABLE IF EXISTS " + TABLENAME;
        db.execSQL(s);
    }

}
