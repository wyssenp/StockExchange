package ch.hevs.stockexchange.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Pierre-Alain Wyssen on 23.04.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.backend
 * Description:
 */
@Entity
public class CurrencyModel {

    @Id
    private long id;
    private String symbol;
    private String name;

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
}
