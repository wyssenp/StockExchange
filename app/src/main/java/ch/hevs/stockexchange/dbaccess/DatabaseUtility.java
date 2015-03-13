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
    private static final String TABLE_STOCK = "Stock";
    private static final String TABLE_BROKER = "Broker";
    private static final String TABLE_STOCKMARKET = "Stockmarket";
    private static final String TABLE_PORTFOLIO = "Portfolio";

    private static final String TABLE_STOCKMARKET_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_STOCKMARKET +
            "(stockMarketId INTEGER PRIMARY KEY NOT NULL,"+
            "Symbol TEXT NOT NULL,"+
            "Name TEXT NOT NULL,"+
            "Currency TEXT NOT NULL);";

    private static final String TABLE_STOCK_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_STOCK +
            "(stockId INTEGER PRIMARY KEY NOT NULL,"+
            "Symbol TEXT NOT NULL,"+
            "Name TEXT NOT NULL,"+
            "Sector TEXT NOT NULL,"+
            "StockMarket INTEGER NOT NULL,"+
            "StockValue DOUBLE NOT NULL,"+
            "FOREIGN KEY(StockMarket) REFERENCES " + TABLE_STOCKMARKET + "(stockMarketId));";

    private static final String TABLE_BROKER_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_BROKER +
            "(brokerId INTEGER PRIMARY KEY NOT NULL,"+
            "Name TEXT NOT NULL,"+
            "BankType TEXT NOT NULL,"+
            "SecuritiesDealerType TEXT NOT NULL);";

    private static final String TABLE_PORTFOLIO_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_PORTFOLIO +
            "(portfolioId INTEGER PRIMARY KEY NOT NULL,"+
            "Stock INTEGER NOT NULL,"+
            "Broker INTEGER NOT NULL,"+
            "Market INTEGER NOT NULL,"+
            "FOREIGN KEY(Stock) REFERENCES " + TABLE_STOCK + "(stockId),"+
            "FOREIGN KEY(Broker) REFERENCES " + TABLE_BROKER + "(brokerId),"+
            "FOREIGN KEY(Market) REFERENCES " + TABLE_STOCKMARKET + "(stockMarketId));";

    private static final String TABLE_STOCKMARKET_INSERT = "INSERT INTO " + TABLE_STOCKMARKET  + "(Symbol, Name, Currency) "+
            "VALUES('SIX','SIX Swiss Exchange','CHF');"+
            "INSERT INTO " + TABLE_STOCKMARKET  + "(Symbol, Name, Currency) "+
            "VALUES('DBAG','Deutsche Börse AG','EUR');";


    public DatabaseUtility(Context context) {
        super(context, DB_PATH + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_STOCKMARKET_CREATE);
        db.execSQL(TABLE_STOCK_CREATE);
        db.execSQL(TABLE_BROKER_CREATE);
        db.execSQL(TABLE_PORTFOLIO_CREATE);
        db.execSQL(TABLE_STOCKMARKET_INSERT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BROKER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKMARKET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PORTFOLIO);
        onCreate(db);
    }
}
