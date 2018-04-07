package example.com.daliynews.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String dbname="img.db";
    private static final int version=1;
    private static SQLiteHelper dbHelper;


    private final String createTb="CREATE TABLE Img (id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar(255),avatar BLOB)";

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteHelper(Context context){
        super(context, dbname, null, version);
    }

    public static SQLiteHelper getInstance(Context context) {

        if (dbHelper == null) { //单例模式
            dbHelper = new SQLiteHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create a table   ，字段：_id、name、avatar。
        db.execSQL(createTb);
        Log.d("tag","创建数据库成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



}

