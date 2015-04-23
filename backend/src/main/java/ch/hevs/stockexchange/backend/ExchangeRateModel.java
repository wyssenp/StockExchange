package ch.hevs.stockexchange.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Created by Pierre-Alain Wyssen on 23.04.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.backend
 * Description:
 */
@Entity
public class ExchangeRateModel {

    @Id
    private Long id;
    private CurrencyModel from;
    private CurrencyModel to;
    private double rate;
    private String date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CurrencyModel getFrom() {
        return from;
    }

    public void setFrom(CurrencyModel from) {
        this.from = from;
    }

    public CurrencyModel getTo() {
        return to;
    }

    public void setTo(CurrencyModel to) {
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
