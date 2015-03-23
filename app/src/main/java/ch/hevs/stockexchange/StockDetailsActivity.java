package ch.hevs.stockexchange;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.TextView;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.dbaccess.DatabaseUtility;
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
        Stock s = datasource.getStockById(stockId);

        textViewName.setText(s.getName());
        textViewSymbol.setText(s.getSymbol());
        textViewSector.setText(s.getSector());
        textViewValue.setText(Double.toString(s.getValue()));

        Resources res = getResources();
        String text = String.format(res.getString(R.string.sd_title_buy,s.getName()));
        textViewMediumBuy.setText(text);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
