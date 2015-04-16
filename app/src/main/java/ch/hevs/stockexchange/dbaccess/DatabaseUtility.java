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

    //To insert the current time in CE(S)T instead of GMT, use (DATETIME(CURRENT_TIMESTAMP, 'localtime'))
    //Source: http://stackoverflow.com/questions/14814433/how-to-change-timestamp-of-sqlite-db-to-local-timestamp
    private static final String TABLE_EXCHANGERATE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_EXCHANGERATE +
            "(exchangeRateId INTEGER PRIMARY KEY NOT NULL,"+
            "CurrencyFrom INTEGER NOT NULL,"+
            "CurrencyTo INTEGER NOT NULL,"+
            "ExchangeRate DOUBLE NOT NULL,"+
            "ExchangeDate DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'localtime')),"+
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
            "Value INTEGER NOT NULL,"+
            "Amount INTEGER NOT NULL,"+
            "FOREIGN KEY(Stock) REFERENCES " + TABLE_STOCK + "(stockId)" +
                " ON DELETE CASCADE" +
                " ON UPDATE CASCADE," +
            "FOREIGN KEY(Broker) REFERENCES " + TABLE_BROKER + "(brokerId));";

    private static final String TABLE_STOCKMARKET_INSERT_1 = "INSERT INTO " + TABLE_STOCKMARKET  + "(Symbol, Name, Currency) "+
            "VALUES('SIX','SIX Swiss Exchange',1);";

    private static final String TABLE_STOCKMARKET_INSERT_2 = "INSERT INTO " + TABLE_STOCKMARKET  + "(Symbol, Name, Currency) "+
            "VALUES('DBAG','Deutsche Börse AG',3);";

    private static final String TABLE_CURRENCY_INSERT_CHF = "INSERT INTO " + TABLE_CURRENCY + "(Symbol, Name) VALUES ('CHF','Swiss Francs');";
    private static final String TABLE_CURRENCY_INSERT_USD = "INSERT INTO " + TABLE_CURRENCY + "(Symbol, Name) VALUES ('USD','US Dollar');";
    private static final String TABLE_CURRENCY_INSERT_EUR = "INSERT INTO " + TABLE_CURRENCY + "(Symbol, Name) VALUES ('EUR','Euro');";

    private static final String TABLE_BROKER_INSERT_1 = "INSERT INTO " + TABLE_BROKER  + "(Name, BankType, SecuritiesDealerType) "+
            "VALUES('Aquila Co. AG','Andere Bank', 'Swiss securities dealer');";
    private static final String TABLE_BROKER_INSERT_2 = "INSERT INTO " + TABLE_BROKER  + "(Name, BankType, SecuritiesDealerType) "+
            "VALUES('BANCA CREDINVEST SA','Ausländische Bank', 'Foreign-controlled securities dealer');";
    private static final String TABLE_BROKER_INSERT_3 = "INSERT INTO " + TABLE_BROKER  + "(Name, BankType, SecuritiesDealerType) "+
            "VALUES('SB Saanen Bank AG','Regionale Bank', 'Swiss securities dealer');";
    private static final String TABLE_BROKER_INSERT_4 = "INSERT INTO " + TABLE_BROKER  + "(Name, BankType, SecuritiesDealerType) "+
            "VALUES('Zevener Volksbank','Regionale Bank', 'German securities deal');";
    private static final String TABLE_BROKER_INSERT_5 = "INSERT INTO " + TABLE_BROKER  + "(Name, BankType, SecuritiesDealerType) "+
            "VALUES('Ostsee Sparkasse Rostock','Regionale Bank', 'German securities deal');";
    private static final String TABLE_BROKER_INSERT_6 = "INSERT INTO " + TABLE_BROKER  + "(Name, BankType, SecuritiesDealerType) "+
            "VALUES('SHINHAN BANK EUROPE GmbH','Ausländische Bank', 'Foreign-controlled securities deal');";

    private static final String TABLE_STOCK_INSERT_1 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('BAER','Julius Baer Group Ltd', 'Financials', 1, 52.80);";
    private static final String TABLE_STOCK_INSERT_2 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('CSGN','Credit Suisse Group AG', 'Financials', 1, 27.46);";
    private static final String TABLE_STOCK_INSERT_3 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('NOVN','Novartis AG', 'Health Care', 1, 101.20);";
    private static final String TABLE_STOCK_INSERT_4 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('NESN','Nestle SA', 'Consumer Staples', 1, 75.75);";
    private static final String TABLE_STOCK_INSERT_5 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('HEN3','Henkel AG & Co KGaA', 'Consumer Staples', 2, 115.00);";
    private static final String TABLE_STOCK_INSERT_6 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('ALV','Allianz SE', 'Financials', 2, 169.45);";
    private static final String TABLE_STOCK_INSERT_7 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('TKA','ThyssenKrupp AG', 'Materials', 2, 25.45);";
    private static final String TABLE_STOCK_INSERT_8 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('BAYN','Bayer AG', 'Health Care', 2, 144.75);";
    private static final String TABLE_STOCK_INSERT_9 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('BMW','Bayerische Motoren Werke AG', 'Consumer Discretionary', 2, 117.65);";
    private static final String TABLE_STOCK_INSERT_10 = "INSERT INTO " + TABLE_STOCK  + "(Symbol, Name, Sector, StockMarket, StockValue) "+
            "VALUES('HEI','HeidelbergCement AG', 'Materials', 2, 76.42);";

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
        db.execSQL(TABLE_STOCKMARKET_INSERT_1);
        db.execSQL(TABLE_STOCKMARKET_INSERT_2);
        db.execSQL(TABLE_BROKER_INSERT_1);
        db.execSQL(TABLE_BROKER_INSERT_2);
        db.execSQL(TABLE_BROKER_INSERT_3);
        db.execSQL(TABLE_BROKER_INSERT_4);
        db.execSQL(TABLE_BROKER_INSERT_5);
        db.execSQL(TABLE_BROKER_INSERT_6);
        db.execSQL(TABLE_STOCK_INSERT_1);
        db.execSQL(TABLE_STOCK_INSERT_2);
        db.execSQL(TABLE_STOCK_INSERT_3);
        db.execSQL(TABLE_STOCK_INSERT_4);
        db.execSQL(TABLE_STOCK_INSERT_5);
        db.execSQL(TABLE_STOCK_INSERT_6);
        db.execSQL(TABLE_STOCK_INSERT_7);
        db.execSQL(TABLE_STOCK_INSERT_8);
        db.execSQL(TABLE_STOCK_INSERT_9);
        db.execSQL(TABLE_STOCK_INSERT_10);
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
