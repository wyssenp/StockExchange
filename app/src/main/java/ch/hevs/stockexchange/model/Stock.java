package ch.hevs.stockexchange.model;

/**
 * Created by Pierre-Alain Wyssen on 04.03.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.model
 * Description:
 * Model for Stock
 */
public class Stock {

    private long id;
    private String symbol;
    private String name;
    private String sector;
    private Market market;
    private Currency currency;
    private double value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return name + " (" + value + currency +")";
    }
}
