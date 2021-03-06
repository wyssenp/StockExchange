package ch.hevs.stockexchange.model;

/**
 * Created by Pierre-Alain Wyssen on 04.03.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange.model
 * Description:
 * Model for Broker
 */
public class Broker {

    private long id;
    private String name;
    private String bankType;
    private String securitesDealerType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public void setSecuritesDealerType(String securitesDealerType) {
        this.securitesDealerType = securitesDealerType;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
