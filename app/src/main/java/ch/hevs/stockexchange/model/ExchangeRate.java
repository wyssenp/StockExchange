package ch.hevs.stockexchange.model;

/**
 * Created by Pierre-Alain Wyssen on 31.03.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.model
 * Description:
 */
public class ExchangeRate {

    private long id;
    private String from;
    private String to;
    private double rate;
    private String date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
