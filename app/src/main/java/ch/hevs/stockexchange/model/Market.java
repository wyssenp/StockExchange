package ch.hevs.stockexchange.model;

/**
 * Created by Pierre-Alain Wyssen on 04.03.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.model
 * Description:
 * Model for Market
 */
public class Market {

    private long id;
    private String symbol;
    private String name;
    private String currency;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Market{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
