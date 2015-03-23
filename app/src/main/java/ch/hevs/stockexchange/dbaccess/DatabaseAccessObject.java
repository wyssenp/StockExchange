package ch.hevs.stockexchange.dbaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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

    public Stock getStockById(long id) {
        Stock result;

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCK,stock_allColumns,"stockId = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        result = cursorToStock(cursor);
        cursor.close();
        return result;
    }

    public Market getMarketById(long id) {
        Market result;

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCKMARKET,market_allColumns,"stockMarketId = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        result = cursorToMarket(cursor);
        cursor.close();
        return result;
    }

    public void deleteStock(int stockId) {
        String query = "DELETE FROM " + DatabaseUtility.TABLE_STOCK + " WHERE stockId = " + stockId;

        database.execSQL(query);
    }

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

    private Market cursorToMarket(Cursor cursor) {
        Market market = new Market();
        market.setId(cursor.getLong(0));
        market.setSymbol(cursor.getString(1));
        market.setName(cursor.getString(2));
        market.setCurrency(cursor.getString(3));
        return market;
    }

    public void writeStock(String symbol, String name, String sector, double value, int market) {
        ContentValues values = new ContentValues();
        values.put("Symbol",symbol);
        values.put("Name",name);
        values.put("Sector",sector);
        values.put("StockValue",value);
        values.put("StockMarket",market);
        database.insert(DatabaseUtility.TABLE_STOCK, null, values);
    }

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
}
