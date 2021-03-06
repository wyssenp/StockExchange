package ch.hevs.stockexchange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Broker;
import ch.hevs.stockexchange.model.Currency;
import ch.hevs.stockexchange.model.Stock;


public class StockDetailsActivity extends ActionBarActivity {

    private NumberPicker np;
    private int stockId;
    private DatabaseAccessObject datasource;

    private TextView textViewName;
    private TextView textViewSymbol;
    private TextView textViewSector;
    private TextView textViewValue;
    private TextView textViewMediumBuy;
    private Spinner spinner_broker;
    private Button buyStock;
    private Stock s;
    private Currency c;
    private List<Broker> brokers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguage();
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_stock_details);
        setContentView(R.layout.activity_stock_details);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewSymbol = (TextView) findViewById(R.id.textViewSymbol);
        textViewSector = (TextView) findViewById(R.id.textViewSector);
        textViewValue = (TextView) findViewById(R.id.textViewValue);
        textViewMediumBuy = (TextView) findViewById(R.id.textView5);

        datasource = new DatabaseAccessObject(this);
        datasource.open();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String currency = sharedPref.getString("current_currency", "");
        c = datasource.getCurrencyById(Long.parseLong(currency));

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(100);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        stockId = b.getInt("stockId");
        s = datasource.getStockById(stockId, c);

        // Converts the stock value to the current currency if needed
        double exchangerate;
        if(s.getMarket().getSymbol().equals("SIX") && !c.getSymbol().equals("CHF")) {
            exchangerate = datasource.getExchangeRateByCurrencies(1, (int)c.getId());
            s.setValue(Math.round((s.getValue() * exchangerate) * 100.0) / 100.0);
        } else if(s.getMarket().getSymbol().equals("DBAG") && !c.getSymbol().equals("EUR")) {
            exchangerate = datasource.getExchangeRateByCurrencies(3, (int) c.getId());
            s.setValue(Math.round((s.getValue() * exchangerate) * 100.0) / 100.0);
        }

        textViewName.setText(s.getName());
        textViewSymbol.setText(s.getSymbol());
        textViewSector.setText(s.getSector());
        textViewValue.setText(Double.toString(s.getValue())+s.getCurrency());

        Resources res = getResources();
        String text = String.format(res.getString(R.string.sd_title_buy),s.getName());
        textViewMediumBuy.setText(text);

        spinner_broker = (Spinner) findViewById(R.id.spinner_broker);
        loadSpinnerData();

        buyStock = (Button) findViewById(R.id.btn_buyStock);
        buyStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasource.addStockToPortfolio(s.getId(),
                        ((Broker)spinner_broker.getSelectedItem()).getId(), s.getValue(), np.getValue());
                Toast.makeText(StockDetailsActivity.this, R.string.toast_stockBought, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_details, menu);
        return true;
    }

    /**
     * Function to load the spinner data from SQLite database
     */
    private void loadSpinnerData() {
        // Spinner Drop down elements
        brokers = datasource.getBrokers();

        ArrayAdapter<Broker> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, brokers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_broker.setAdapter(dataAdapter);
    }

    /**
     * This method sets the current application language to the selected one.
     */
    public void setLanguage() {
        // Get the current language from shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = sharedPref.getString("current_language", "");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
    }
}
