package ch.hevs.stockexchange.dbaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pierre-Alain Wyssen on 12.03.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.dbaccess
 * Description:
 * Class to manage the android database
 */
public class DatabaseUtility extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StockExchange.db";
    private static final String DB_PATH = "/mnt/sdcard/" ;
    private static final int DATABASE_VERSION = 1;

    public DatabaseUtility(Context context) {
        super(context, DB_PATH + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
