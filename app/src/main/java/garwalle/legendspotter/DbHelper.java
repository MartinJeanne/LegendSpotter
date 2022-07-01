package garwalle.legendspotter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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

    public List<Champ> getFavoritesChamp() {
        List<Champ> champs = new ArrayList<Champ>();

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM favoriteChamp", null);
        if (cursor.getCount() > 0) {
            final int nameIndex = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                champs.add(new Champ(cursor.getString(nameIndex)));
            }
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return champs;
    }

    public boolean isChampFavorite(String champName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM favoriteChamp WHERE name = ?", new String[] {champName});
        boolean b = cursor.getCount() > 0;
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return b;
    }

    public boolean switchFavorite(String champName) {
        boolean isFavorite = isChampFavorite(champName);
        boolean result;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        if (isFavorite) {
            db.execSQL("DELETE FROM favoriteChamp WHERE name = '" + champName + "';");
            result = false;
        }
        else {
            db.execSQL("INSERT INTO favoriteChamp (name) VALUES ('" + champName + "');");
            result = true;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return result;
    }
}