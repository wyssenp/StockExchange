package ch.hevs.stockexchange.dbaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.stockexchange.model.ExchangeRate;
import ch.hevs.stockexchange.model.Market;
import ch.hevs.stockexchange.model.Stock;

/**
 * Created by Pierre-Alain Wyssen on 12.03.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.dbaccess
 * Description:
 * Class to access the android database
 */
public class DatabaseAccessObject {

    private SQLiteDatabase database;
    private SQLiteOpenHelper helper;
    private String[] stock_allColumns = {"stockId","Symbol","Name","Sector","StockMarket","StockValue"};
    private String[] market_allColumns = {"stockMarketId","Symbol","Name","Currency"};
    private String[] exchangeRate_allColumns = {"exchangeRateId","CurrencyFrom","CurrencyTo","ExchangeRate","ExchangeDate"};

    public DatabaseAccessObject(Context context) {
        helper = new DatabaseUtility(context);
    }

    /**
     * Open the database
     */
    public void open()
    {
        database = helper.getWritableDatabase();
    }

    /**
     * Close the database
     */
    public void close()
    {
        helper.close();
    }

    public List<ExchangeRate> getExchangeRates() {
        List<ExchangeRate> rates = new ArrayList<>();

        Cursor cursor = database.query(DatabaseUtility.TABLE_EXCHANGERATE, exchangeRate_allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            ExchangeRate er = cursorToExchangeRate(cursor);
            rates.add(er);
            cursor.moveToNext();
        }
        cursor.close();
        return rates;
    }

    /**
     * Method used to convert a cursor object into an ExchangeRate object
     * @param cursor The cursor
     * @return A ExchangeRate object
     */
    private ExchangeRate cursorToExchangeRate(Cursor cursor) {
        ExchangeRate er = new ExchangeRate();
        er.setId(cursor.getLong(0));
        er.setFrom(cursor.getString(1)); //TODO change to getCurrencyById(cursor.getString(1));
        er.setTo(cursor.getString(2)); //TODO changge to getCurrencyById(cursor.getString(2));
        er.setRate(cursor.getDouble(3));
        er.setDate(cursor.getString(4));
        return er;
    }

    //TODO implement Currency class
    //TODO implement method getCurrencyById

    /**
     * Method used to update the database with the most recent exchange rates
     * @param chf_eur Current exchange rate from CHF to EUR
     * @param chf_usd Current exchange rate from CHF to USD
     * @param eur_chf Current exchange rate from EUR to CHF
     * @param eur_usd Current exchange rate from EUR to USD
     * @param usd_chf Current exchange rate from USD to CHF
     * @param usd_eur Current exchange rate from USD to EUR
     */
    public void updateExchangeRates(double chf_eur, double chf_usd, double eur_chf, double eur_usd, double usd_chf, double usd_eur) {
        //Link: http://download.finance.yahoo.com/d/quotes.csv?s=USDEUR=x,CHFUSD=x,CHFEUR=x&f=l1
        /*
        Table ExchangeRate
        CurrencyFrom
        CurrencyTo
        ExchangeRate

        Table Currency
        ID:Currency
        1:CHF
        2:USD
        3:EUR
         */

        //Delete all the rows from the table
        database.delete(DatabaseUtility.TABLE_EXCHANGERATE, null, null);
        ContentValues values_chf_eur = new ContentValues();
        values_chf_eur.put("CurrencyFrom",1);
        values_chf_eur.put("CurrencyTo",3);
        values_chf_eur.put("ExchangeRate",chf_eur);

        ContentValues values_chf_usd = new ContentValues();
        values_chf_usd.put("CurrencyFrom",1);
        values_chf_usd.put("CurrencyTo",2);
        values_chf_usd.put("ExchangeRate",chf_usd);

        ContentValues values_eur_chf = new ContentValues();
        values_eur_chf.put("CurrencyFrom",3);
        values_eur_chf.put("CurrencyTo",1);
        values_eur_chf.put("ExchangeRate",eur_chf);

        ContentValues values_eur_usd = new ContentValues();
        values_eur_usd.put("CurrencyFrom",3);
        values_eur_usd.put("CurrencyTo",2);
        values_eur_usd.put("ExchangeRate",eur_usd);

        ContentValues values_usd_chf = new ContentValues();
        values_usd_chf.put("CurrencyFrom",2);
        values_usd_chf.put("CurrencyTo",1);
        values_usd_chf.put("ExchangeRate",usd_chf);

        ContentValues values_usd_eur = new ContentValues();
        values_usd_eur.put("CurrencyFrom",2);
        values_usd_eur.put("CurrencyTo",3);
        values_usd_eur.put("ExchangeRate",usd_eur);

        database.insert(DatabaseUtility.TABLE_EXCHANGERATE, null, values_chf_eur);
        database.insert(DatabaseUtility.TABLE_EXCHANGERATE, null, values_chf_usd);
        database.insert(DatabaseUtility.TABLE_EXCHANGERATE, null, values_eur_chf);
        database.insert(DatabaseUtility.TABLE_EXCHANGERATE, null, values_eur_usd);
        database.insert(DatabaseUtility.TABLE_EXCHANGERATE, null, values_usd_chf);
        database.insert(DatabaseUtility.TABLE_EXCHANGERATE, null, values_usd_eur);

    }

    /**
     * Method used when the user changes the values of a stock
     * @param stockId Needed to identify the stock
     * @param symbol The symbol
     * @param name The name
     * @param sector Corresponding sector
     * @param value The value
     * @param market The market
     */
    public void updateStock(int stockId, String symbol, String name, String sector, double value, int market) {
        ContentValues values = new ContentValues();
        values.put("Symbol",symbol);
        values.put("Name",name);
        values.put("Sector",sector);
        values.put("StockValue",value);
        values.put("StockMarket",market);

        String selection = "stockId LIKE ?";
        String[] selectionArgs = { String.valueOf(stockId) };

        database.update(DatabaseUtility.TABLE_STOCK,values,selection,selectionArgs);
    }

    /**
     * Method used to display the correct stock when the users wishes to see the details
     * @param id The stock identifier
     * @return A stock object
     */
    public Stock getStockById(long id) {
        Stock result;

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCK,stock_allColumns,"stockId = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        result = cursorToStock(cursor);
        cursor.close();
        return result;
    }

    /**
     * Method used in the cursorToStock() method, because the foreign key in the stock table needs to be an integer and not a Market object
     * @param id The market identifier
     * @return A market object
     */
    public Market getMarketById(long id) {
        Market result;

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCKMARKET,market_allColumns,"stockMarketId = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        result = cursorToMarket(cursor);
        cursor.close();
        return result;
    }

    /**
     * Method used in the stock management activity, to delete a stock
     * @param stockId The stock identifier
     */
    public void deleteStock(int stockId) {
        String query = "DELETE FROM " + DatabaseUtility.TABLE_STOCK + " WHERE stockId = " + stockId;

        database.execSQL(query);
    }

    /**
     * Method used to display all the stocks
     * @return A generic list of all stocks in the database
     */
    public List<Stock> getStocks() {
        List<Stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCK, stock_allColumns, null, null, null, null, "stockId");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Stock stock = cursorToStock(cursor);
            stocks.add(stock);
            cursor.moveToNext();
        }
        cursor.close();

        return stocks;
    }

    /**
     * Method used in the stock market activity, so that the user can filter by market
     * @param marketId The stock market identifier
     * @return A generic list of all stocks in the database belonging to a certain market
     */
    public List<Stock> getStocksWithMarket(int marketId) {
        List<Stock> stocks = new ArrayList<>();

        String selection = "StockMarket LIKE ?";
        String[] selectionArgs = { String.valueOf(marketId) };

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCK,stock_allColumns,selection,selectionArgs,null,null,"stockId");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Stock stock = cursorToStock(cursor);
            stocks.add(stock);
            cursor.moveToNext();
        }
        cursor.close();

        return stocks;
    }

    /**
     * Method used to convert a cursor object into a stock object
     * @param cursor The cursor
     * @return A stock object
     */
    private Stock cursorToStock(Cursor cursor) {
        Stock stock = new Stock();
        stock.setId(cursor.getLong(0));
        stock.setSymbol(cursor.getString(1));
        stock.setName(cursor.getString(2));
        stock.setSector(cursor.getString(3));
        stock.setMarket(getMarketById(cursor.getLong(4)));
        stock.setValue(cursor.getDouble(5));
        return stock;
    }

    /**
     * Method used to convert a cursor object into a market object
     * @param cursor The cursor
     * @return A market object
     */
    private Market cursorToMarket(Cursor cursor) {
        Market market = new Market();
        market.setId(cursor.getLong(0));
        market.setSymbol(cursor.getString(1));
        market.setName(cursor.getString(2));
        market.setCurrency(cursor.getString(3));
        return market;
    }

    /**
     * Method used to insert a stock into the database
     * @param symbol The symbol
     * @param name The name
     * @param sector Corresponding sector
     * @param value The value
     * @param market The market
     */
    public void writeStock(String symbol, String name, String sector, double value, int market) {
        ContentValues values = new ContentValues();
        values.put("Symbol",symbol);
        values.put("Name",name);
        values.put("Sector",sector);
        values.put("StockValue",value);
        values.put("StockMarket",market);
        database.insert(DatabaseUtility.TABLE_STOCK, null, values);
    }

}
