package ch.hevs.stockexchange;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Currency;
import ch.hevs.stockexchange.model.Stock;


public class ManageStockActivity extends ActionBarActivity {

    private Spinner spinner_markets;
    private EditText editTextSymbol;
    private EditText editTextName;
    private EditText editTextSector;
    private EditText editTextValue;
    private Button addStock;
    private DatabaseAccessObject datasource;
    private int stockId;
    private Currency c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguage();
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_manage_stock);
        setContentView(R.layout.activity_manage_stock);

        datasource = new DatabaseAccessObject(this);
        datasource.open();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String currency = sharedPref.getString("current_currency", "");
        c = datasource.getCurrencyById(Long.parseLong(currency));

        spinner_markets = (Spinner) findViewById(R.id.spinner_markets);

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this,
                R.array.sm_array_markets, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_markets.setAdapter(adapter_spinner);

        editTextSymbol = (EditText) findViewById(R.id.editTextSymbol);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextSector = (EditText) findViewById(R.id.editTextSector);
        editTextValue = (EditText) findViewById(R.id.editTextValue);

        addStock = (Button) findViewById(R.id.btn_addStock);

        Bundle b = getIntent().getExtras();
        Resources res = getResources();

        /*
        Depending on whether the user creates a new stock or edits an existing stock, the appropriate listener is applied to the button
         */
        if(b != null) {
            stockId = b.getInt("stockId");
            Stock s = datasource.getStockById(stockId, null);

            //Fill the EditText's with the correct data
            editTextSymbol.setText(s.getSymbol());
            editTextName.setText(s.getName());
            editTextSector.setText(s.getSector());
            editTextValue.setText(Double.toString(convertMarketDefaultToUserValue(s)));
            spinner_markets.setSelection((int) (s.getMarket().getId()-1));

            //Change the title of the activity
            setTitle(String.format(res.getString(R.string.title_activity_manage_stock_update), c.getSymbol()));

            addStock.setText(R.string.sm_update_stock);
            addStock.setOnClickListener(new UpdateStockListener());
        } else {
            setTitle(String.format(res.getString(R.string.title_activity_manage_stock), c.getSymbol()));
            addStock.setOnClickListener(new NewStockListener());
        }
    }

    /**
     * This inner class is used when the user creates a new stock
     * @author Pierre-Alain Wyssen
     */
    private class NewStockListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            checkFields();
            double updatedValue = convertUserValueToMarketDefault(Double.parseDouble(editTextValue.getText().toString()));

            datasource.writeStock(editTextSymbol.getText().toString(),
                    editTextName.getText().toString(),
                    editTextSector.getText().toString(),
                    updatedValue,
                    spinner_markets.getSelectedItemPosition()+1); //Swiss market = 0, German market = 1
            datasource.close();

            Toast.makeText(ManageStockActivity.this,R.string.toast_stockCreated,Toast.LENGTH_SHORT).show();

            //Return to the calling activity
            finish();
        }
    }

    /**
     * This inner class is used when the user updates a stock
     * @author Pierre-Alain Wyssen
     */
    private class UpdateStockListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            checkFields();
            double updatedValue = convertUserValueToMarketDefault(Double.parseDouble(editTextValue.getText().toString()));

            //Update database
            datasource.updateStock(stockId,
                    editTextSymbol.getText().toString(),
                    editTextName.getText().toString(),
                    editTextSector.getText().toString(),
                    updatedValue,
                    spinner_markets.getSelectedItemPosition()+1); //Swiss market = 0, German market = 1

            datasource.close();

            Toast.makeText(ManageStockActivity.this,R.string.toast_stockUpdated,Toast.LENGTH_SHORT).show();

            //Return to the calling activity
            finish();

        }
    }

    /**
     * Helper method that makes sure that all fields (EditText) are filled
     */
    private void checkFields() {
        //If any of the text fields are empty, the user is informed
        if(isEmpty(editTextSymbol)||isEmpty(editTextName)||
                isEmpty(editTextSector)||isEmpty(editTextValue)) {
            Toast.makeText(ManageStockActivity.this,R.string.toast_fieldsEmpty,Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     *  Converts the value the user has set to the value of the stockmarket
     *  Swiss market Id = 1 (default CHF), German market Id = 2 (default EUR)
     * @param value Double of the user set value in the userspecific currency
     * @return Double of the converted value in the default market currency
     */
    private double convertUserValueToMarketDefault(double value) {
        double exchangerate;
        if(spinner_markets.getSelectedItemPosition()+1 == 1 && !c.getSymbol().equals("CHF")) {
            exchangerate = datasource.getExchangeRateByCurrencies((int)c.getId(), 1);
            value = Math.round((value * exchangerate) * 100.0) / 100.0;
        } else if(spinner_markets.getSelectedItemPosition()+1 == 2 && !c.getSymbol().equals("EUR")) {
            exchangerate = datasource.getExchangeRateByCurrencies((int) c.getId(), 3);
            value = Math.round((value * exchangerate) * 100.0) / 100.0;
        }
        return value;
    }

    /**
     *  Converts the value of the stock to the user specified currency
     *  Swiss market Id = 1 (default CHF), German market Id = 2 (default EUR)
     * @param s Stock which value should be converted
     * @return Double of the converted value in the user specified currency
     */
    private double convertMarketDefaultToUserValue(Stock s) {
        double exchangerate;
        if(s.getMarket().getSymbol().equals("SIX") && !c.getSymbol().equals("CHF")) {
            exchangerate = datasource.getExchangeRateByCurrencies(1, (int)c.getId());
            s.setValue(Math.round((s.getValue() * exchangerate) * 100.0) / 100.0);
        } else if(s.getMarket().getSymbol().equals("DBAG") && !c.getSymbol().equals("EUR")) {
            exchangerate = datasource.getExchangeRateByCurrencies(3, (int) c.getId());
            s.setValue(Math.round((s.getValue() * exchangerate) * 100.0) / 100.0);
        }
        return s.getValue();
    }

    /**
     * Check if a text field (EditText) is empty
     * @param editText The EditText object
     * @return false if it's not empty, true if it's empty
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_stock, menu);
        return true;
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
