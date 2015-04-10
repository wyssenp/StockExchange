package ch.hevs.stockexchange;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Broker;
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
    private List<Broker> brokers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);


        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewSymbol = (TextView) findViewById(R.id.textViewSymbol);
        textViewSector = (TextView) findViewById(R.id.textViewSector);
        textViewValue = (TextView) findViewById(R.id.textViewValue);
        textViewMediumBuy = (TextView) findViewById(R.id.textView5);

        datasource = new DatabaseAccessObject(this);
        datasource.open();

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(100);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        stockId = b.getInt("stockId");
        s = datasource.getStockById(stockId);

        textViewName.setText(s.getName());
        textViewSymbol.setText(s.getSymbol());
        textViewSector.setText(s.getSector());
        textViewValue.setText(Double.toString(s.getValue()));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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
}
