package ch.hevs.stockexchange.dbaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pierre-Alain Wyssen on 12.03.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.dbaccess
 * Description:
 * Class to access the android database
 */
public abstract class DatabaseAccessObject {

    private static SQLiteDatabase database;
    private static SQLiteOpenHelper helper;

    /**
     * Open the database
     * @param context
     * The context in which running
     */
    public static void open(Context context)
    {
        helper = new DatabaseUtility(context);
        database = helper.getWritableDatabase();
    }

    /**
     * Close the database
     */
    public static void close()
    {
        helper.close();
    }

    public static void writeStock(String symbol, String name, String sector, double value, int market) {
        ContentValues values = new ContentValues();
        values.put("Symbol",symbol);
        values.put("Name",name);
        values.put("Sector",sector);
        values.put("StockValue",value);
        values.put("StockMarket",market);
        database.insert("Stock", null, values);
    }
}
