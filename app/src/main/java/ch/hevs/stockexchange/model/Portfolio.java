package ch.hevs.stockexchange.model;

/**
 * Created by Stefan Eggenschwiler on 10.04.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.model
 * Description:
 * Model for Portfolio
 */
public class Portfolio {

    private long id;
    private Stock stock;
    private Broker broker;
    private double value;
    private int amount;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return amount + "x" + stock + " - " + broker.getName();
    }
}
