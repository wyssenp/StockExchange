package ch.hevs.stockexchange.dbaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.stockexchange.model.Broker;
import ch.hevs.stockexchange.model.Currency;
import ch.hevs.stockexchange.model.ExchangeRate;
import ch.hevs.stockexchange.model.Market;
import ch.hevs.stockexchange.model.Portfolio;
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
    private String[] exchangeRate_exchangeRateColumn = {"ExchangeRate"};
    private String[] currency_allColumns = {"currencyId","Symbol","Name"};
    private String[] broker_allColumns = {"brokerId", "Name", "BankType", "SecuritiesDealerType"};
    private String[] portfolio_allColumns = {"portfolioId", "Stock", "Broker", "Value", "Amount"};

    public DatabaseAccessObject(Context context) {
        helper = new DatabaseUtility(context);
    }

    /**
     * Open the database
     */
    public void open()
    {
        database = helper.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
    }

    /**
     * Close the database
     */
    public void close()
    {
        helper.close();
    }

    public boolean hasDataToUpload() {
        int nb = selectAllToUpload().getCount();

        return nb != 0;
    }

    private Cursor selectAllToUpload() {
        String sql = "SELECT * FROM " + DatabaseUtility.TABLE_BROKER + "WHERE Uploaded = 'FALSE'";

        return database.rawQuery(sql, null);
    }

    public void setUploadedToTrue(Long id) {
        String sql = "UPDATE " + DatabaseUtility.TABLE_BROKER + " SET Uploaded = 'true' WHERE brokerId = " + id;

        Cursor c = database.rawQuery(sql, null);
        c.moveToFirst();
        c.close();
    }

    /**
     * Method used to display all the exchange rates
     * @return A generic list of all exchange rates in the database
     */
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
        long from = Long.parseLong(cursor.getString(1));
        er.setFrom(getCurrencyById(from).getSymbol());
        long to = Long.parseLong(cursor.getString(2));
        er.setTo(getCurrencyById(to).getSymbol());
        er.setRate(cursor.getDouble(3));
        er.setDate(cursor.getString(4));
        return er;
    }

    /**
     * Method used to display the correct currency in the exchange rates activity
     * @param id The currency identifier
     * @return A Currency object
     */
    public Currency getCurrencyById(long id) {
        Currency result;

        Cursor cursor = database.query(DatabaseUtility.TABLE_CURRENCY, currency_allColumns, "currencyId = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        result = cursorToCurrency(cursor);
        cursor.close();
        return result;
    }

    /**
     * Method used to convert a cursor object into an Currency object
     * @param cursor The cursor
     * @return A Currency object
     */
    private Currency cursorToCurrency(Cursor cursor) {
        Currency c = new Currency();
        c.setId(cursor.getLong(0));
        c.setSymbol(cursor.getString(1));
        c.setName(cursor.getString(2));
        return c;
    }

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
        values.put("Name", name);
        values.put("Sector",sector);
        values.put("StockValue", value);
        values.put("StockMarket",market);

        String selection = "stockId LIKE ?";
        String[] selectionArgs = { String.valueOf(stockId) };

        database.update(DatabaseUtility.TABLE_STOCK,values,selection,selectionArgs);
    }

    /**
     * Method used to display the correct stock when the users wishes to see the details
     * @param id The stock identifier
     * @param currency The current currency selected in the app
     * @return A stock object
     */
    public Stock getStockById(long id, Currency currency) {
        Stock result;

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCK,stock_allColumns,"stockId = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        result = cursorToStock(cursor, currency);
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
     * @param currency The current currency selected in the app
     * @return A generic list of all stocks in the database
     */
    public List<Stock> getStocks(Currency currency) {
        List<Stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCK, stock_allColumns, null, null, null, null, "stockId");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Stock stock = cursorToStock(cursor, currency);
            stocks.add(stock);
            cursor.moveToNext();
        }
        cursor.close();

        return stocks;
    }

    /**
     * Method used in the stock market activity, so that the user can filter by market
     * @param marketId The stock market identifier
     * @param currency The current currency selected in the app
     * @return A generic list of all stocks in the database belonging to a certain market
     */
    public List<Stock> getStocksWithMarket(int marketId, Currency currency) {
        List<Stock> stocks = new ArrayList<>();

        String selection = "StockMarket LIKE ?";
        String[] selectionArgs = { String.valueOf(marketId) };

        Cursor cursor = database.query(DatabaseUtility.TABLE_STOCK,stock_allColumns,selection,selectionArgs,null,null,"stockId");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Stock stock = cursorToStock(cursor, currency);
            stocks.add(stock);
            cursor.moveToNext();
        }
        cursor.close();

        return stocks;
    }

    /**
     * Method used to convert a cursor object into a stock object
     * @param cursor The cursor
     * @param currency The current currency selected in the app
     * @return A stock object
     */
    private Stock cursorToStock(Cursor cursor, Currency currency) {
        Stock stock = new Stock();
        stock.setId(cursor.getLong(0));
        stock.setSymbol(cursor.getString(1));
        stock.setName(cursor.getString(2));
        stock.setSector(cursor.getString(3));
        stock.setMarket(getMarketById(cursor.getLong(4)));
        stock.setValue(cursor.getDouble(5));
        stock.setCurrency(currency);
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
     * Method used to convert a cursor object into a broker object
     * @param cursor The cursor
     * @return A broker object
     */
    private Broker cursorToBroker(Cursor cursor) {
        Broker broker = new Broker();
        broker.setId(cursor.getLong(0));
        broker.setName(cursor.getString(1));
        broker.setBankType(cursor.getString(2));
        broker.setSecuritiesDealerType(cursor.getString(3));
        return broker;
    }

    /**
     * Method used to convert a cursort object into a portfolio object
     * @param cursor The cursor
     * @param currency The current currency selected in the app
     * @return A portfolio object
     */
    private Portfolio cursortToPortfolio(Cursor cursor, Currency currency) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(cursor.getLong(0));
        portfolio.setStock(getStockById(cursor.getLong(1), currency));
        portfolio.setBroker(getBrokerById(cursor.getLong(2)));
        portfolio.setValue(cursor.getDouble(3));
        portfolio.setAmount(cursor.getInt(4));

        return portfolio;
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
        values.put("Symbol", symbol);
        values.put("Name", name);
        values.put("Sector", sector);
        values.put("StockValue", value);
        values.put("StockMarket", market);
        database.insert(DatabaseUtility.TABLE_STOCK, null, values);
    }

    /**
     * Method used in the stock details activity in order to show the brokers
     * @return A generic list of all brokers in the database
     */
    public List<Broker> getBrokers() {
        List<Broker> brokers = new ArrayList<>();

        Cursor cursor = database.query(DatabaseUtility.TABLE_BROKER,broker_allColumns,null,null,null,null,"brokerId");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            brokers.add(cursorToBroker(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return brokers;
    }

    /**
     * Method used to select the correct broker for the user portfolio
     * @param brokerId The stock identifier
     * @return A broker object
     */
    public Broker getBrokerById(long brokerId) {
        Broker broker;

        Cursor cursor = database.query(DatabaseUtility.TABLE_BROKER,broker_allColumns,
                "brokerId = " + brokerId,null,null,null,null);

        cursor.moveToFirst();
        broker = cursorToBroker(cursor);
        cursor.close();

        return broker;
    }

    /**
     * Method used to insert a broker into the database
     * @param brokerName Name of the broker
     * @param bankType Bank type of the broaker
     * @param securitiesDealerType securities dealer type of the broker
     */
    public void createBroker(String brokerName, String bankType, String securitiesDealerType) {
        ContentValues values = new ContentValues();
        values.put("Name", brokerName);
        values.put("BankType", bankType);
        values.put("SecuritiesDealerType", securitiesDealerType);
        database.insert(DatabaseUtility.TABLE_BROKER, null, values);
    }

    /**
     * Method used to update a broker in the database
     * @param brokerId Id of the broker
     * @param brokerName Name of the broker
     * @param bankType Bank type of the broaker
     * @param securitiesDealerType securities dealer type of the broker
     */
    public void updateBroker(int brokerId, String brokerName, String bankType, String securitiesDealerType) {
        ContentValues values = new ContentValues();
        values.put("Name", brokerName);
        values.put("BankType", bankType);
        values.put("SecuritiesDealerType", securitiesDealerType);

        String selection = "brokerId LIKE ?";
        String[] selectionArgs = { String.valueOf(brokerId) };

        database.update(DatabaseUtility.TABLE_BROKER,values,selection,selectionArgs);
    }

    /**
     * Method used to delete a broker from the database
     * @param brokerId Id of the broker
     */
    public void deleteBroker(int brokerId) {
        String query = "DELETE FROM " + DatabaseUtility.TABLE_BROKER + " WHERE brokerId = " + brokerId;

        database.execSQL(query);
    }

    /**
     * Method used to add a specific stock to the user's portfolio
     * @param stockId Id of the stock
     * @param brokerId Id of the broker
     * @param value Current stock value
     * @param amount Amount of stock the user bought
     */
    public void addStockToPortfolio(long stockId, long brokerId, double value, int amount) {
        ContentValues values = new ContentValues();
        values.put("Stock", stockId);
        values.put("Broker", brokerId);
        values.put("Value", value);
        values.put("Amount", amount);
        database.insert(DatabaseUtility.TABLE_PORTFOLIO, null, values);
    }

    /**
     * Method used in the myPortfolio activity in order to show the portfolio
     * @param currency The current currency selected in the app
     * @return A generic list of the portfolio in the database
     */
    public List<Portfolio> getPortfolio(Currency currency) {
        List<Portfolio> portfolio = new ArrayList<>();

        Cursor cursor = database.query(DatabaseUtility.TABLE_PORTFOLIO,portfolio_allColumns,null,null,null,null,"portfolioId");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            portfolio.add(cursortToPortfolio(cursor, currency));
            cursor.moveToNext();
        }
        cursor.close();

        return portfolio;
    }

    /**
     * Method used to "sell" stocks from the portfolio. It updates the database with the new amount.
     * @param portfolioId Id of the portfolio
     * @param amount New stock amount
     */
    public void updatePortfolio(long portfolioId, int amount) {
        ContentValues values = new ContentValues();
        values.put("Amount",amount);

        String selection = "portfolioId LIKE ?";
        String[] selectionArgs = { String.valueOf(portfolioId) };

        database.update(DatabaseUtility.TABLE_PORTFOLIO,values,selection,selectionArgs);
    }

    /**
     * Method used to "sell" stocks from the portfolio. Used if sell amount == stock amount.
     * @param portfolioId The portfolio Id
     */
    public void deletePortfolio(long portfolioId) {
        String query = "DELETE FROM " + DatabaseUtility.TABLE_PORTFOLIO + " WHERE portfolioId = " + portfolioId;

        database.execSQL(query);
    }

    /**
     * Method used to get the exchange rate from one currency to another
     * @param currencyFrom From Currency
     * @param currencyTo To Currency
     * @return A double that represents the exchange rate
     */
    public double getExchangeRateByCurrencies(int currencyFrom, int currencyTo) {
        Cursor cursor = database.query(DatabaseUtility.TABLE_EXCHANGERATE,exchangeRate_exchangeRateColumn,
                "CurrencyFrom = ? AND CurrencyTo = ?",new String[] {String.valueOf(currencyFrom), String.valueOf(currencyTo)},null,null,null);

        cursor.moveToFirst();
        Double rate = cursor.getDouble(0);
        cursor.close();
        return rate;
    }
}
