package garwalle.legendspotter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private final static int dbVersion = 1;
    private final static String dbName="legendSpotter";

    public DbHelper(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE favoriteChamp (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", name TEXT" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favoriteChamp");
        this.onCreate(db);
    }
}