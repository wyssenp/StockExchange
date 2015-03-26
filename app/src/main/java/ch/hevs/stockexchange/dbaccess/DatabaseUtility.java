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
    //private static final String DB_PATH = "/mnt/sdcard/";
    private static final int DATABASE_VERSION = 1;
    protected static final String TABLE_STOCK = "Stock";
    protected static final String TABLE_BROKER = "Broker";
    protected static final String TABLE_STOCKMARKET = "Stockmarket";
    protected static final String TABLE_PORTFOLIO = "Portfolio";
    protected static final String TABLE_CURRENCY = "Currency";
    protected static final String TABLE_EXCHANGERATE = "ExchangeRate";

    private static final String TABLE_CURRENCY_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_CURRENCY +
            "(currencyId INTEGER PRIMARY KEY NOT NULL,"+
            "Symbol TEXT NOT NULL,"+
            "Name TEXT NOT NULL);";

    private static final String TABLE_EXCHANGERATE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_EXCHANGERATE +
            "(exchangeRateId INTEGER PRIMARY KEY NOT NULL,"+
            "CurrencyFrom INTEGER NOT NULL,"+
            "CurrencyTo INTEGER NOT NULL,"+
            "ExchangeRate DOUBLE NOT NULL,"+
            "ExchangeDate DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "FOREIGN KEY(CurrencyFrom) REFERENCES " + TABLE_CURRENCY + "(currencyId)," +
            "FOREIGN KEY(CurrencyTo) REFERENCES " + TABLE_CURRENCY + "(currencyId));";

    private static final String TABLE_STOCKMARKET_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_STOCKMARKET +
            "(stockMarketId INTEGER PRIMARY KEY NOT NULL,"+
            "Symbol TEXT NOT NULL,"+
            "Name TEXT NOT NULL,"+
            "Currency INTEGER NOT NULL,"+
            "FOREIGN KEY (Currency) REFERENCES " + TABLE_CURRENCY + "(currencyId));";

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
            "FOREIGN KEY(Stock) REFERENCES " + TABLE_STOCK + "(stockId),"+
            "FOREIGN KEY(Broker) REFERENCES " + TABLE_BROKER + "(brokerId));";

    private static final String TABLE_STOCKMARKET_INSERT_1 = "INSERT INTO " + TABLE_STOCKMARKET  + "(Symbol, Name, Currency) "+
            "VALUES('SIX','SIX Swiss Exchange',1);";

    private static final String TABLE_STOCKMARKET_INSERT_2 = "INSERT INTO " + TABLE_STOCKMARKET  + "(Symbol, Name, Currency) "+
            "VALUES('DBAG','Deutsche Börse AG',3);";

    private static final String TABLE_CURRENCY_INSERT_CHF = "INSERT INTO " + TABLE_CURRENCY + "(Symbol, Name) VALUES ('CHF','Swiss Francs');";
    private static final String TABLE_CURRENCY_INSERT_USD = "INSERT INTO " + TABLE_CURRENCY + "(Symbol, Name) VALUES ('USD','US Dollar');";
    private static final String TABLE_CURRENCY_INSERT_EUR = "INSERT INTO " + TABLE_CURRENCY + "(Symbol, Name) VALUES ('EUR','Euro');";

    private static final String TABLE_EXCHANGERATE_INSERT_CHF_EUR = "INSERT INTO " + TABLE_EXCHANGERATE + "(CurrencyFrom, CurrencyTo, ExchangeRate) "+
            "VALUES(1,3,0.95);";
    private static final String TABLE_EXCHANGERATE_INSERT_CHF_USD = "INSERT INTO " + TABLE_EXCHANGERATE + "(CurrencyFrom, CurrencyTo, ExchangeRate) "+
            "VALUES(1,2,1.04);";
    private static final String TABLE_EXCHANGERATE_INSERT_EUR_CHF = "INSERT INTO " + TABLE_EXCHANGERATE + "(CurrencyFrom, CurrencyTo, ExchangeRate) "+
            "VALUES(3,1,1.05);";
    private static final String TABLE_EXCHANGERATE_INSERT_EUR_USD = "INSERT INTO " + TABLE_EXCHANGERATE + "(CurrencyFrom, CurrencyTo, ExchangeRate) "+
            "VALUES(3,2,1.10);";
    private static final String TABLE_EXCHANGERATE_INSERT_USD_CHF = "INSERT INTO " + TABLE_EXCHANGERATE + "(CurrencyFrom, CurrencyTo, ExchangeRate) "+
            "VALUES(2,1,0.96);";
    private static final String TABLE_EXCHANGERATE_INSERT_USD_EUR = "INSERT INTO " + TABLE_EXCHANGERATE + "(CurrencyFrom, CurrencyTo, ExchangeRate) "+
            "VALUES(2,3,0.91);";

    public DatabaseUtility(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CURRENCY_CREATE);
        db.execSQL(TABLE_EXCHANGERATE_CREATE);
        db.execSQL(TABLE_STOCKMARKET_CREATE);
        db.execSQL(TABLE_STOCK_CREATE);
        db.execSQL(TABLE_BROKER_CREATE);
        db.execSQL(TABLE_PORTFOLIO_CREATE);

        db.execSQL(TABLE_CURRENCY_INSERT_CHF);
        db.execSQL(TABLE_CURRENCY_INSERT_USD);
        db.execSQL(TABLE_CURRENCY_INSERT_EUR);
        db.execSQL(TABLE_EXCHANGERATE_INSERT_CHF_EUR);
        db.execSQL(TABLE_EXCHANGERATE_INSERT_CHF_USD);
        db.execSQL(TABLE_EXCHANGERATE_INSERT_EUR_CHF);
        db.execSQL(TABLE_EXCHANGERATE_INSERT_EUR_USD);
        db.execSQL(TABLE_EXCHANGERATE_INSERT_USD_CHF);
        db.execSQL(TABLE_EXCHANGERATE_INSERT_USD_EUR);
        db.execSQL(TABLE_STOCKMARKET_INSERT_1);
        db.execSQL(TABLE_STOCKMARKET_INSERT_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCHANGERATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BROKER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKMARKET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PORTFOLIO);
        onCreate(db);
    }
}
