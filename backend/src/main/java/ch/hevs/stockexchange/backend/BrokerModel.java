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
public class BrokerModel {

    @Id
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
}
