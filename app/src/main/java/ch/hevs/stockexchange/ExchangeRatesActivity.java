package ch.hevs.stockexchange;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.ExchangeRate;


public class ExchangeRatesActivity extends ActionBarActivity {

    private DatabaseAccessObject datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates);

        TableLayout table = (TableLayout) findViewById(R.id.tableExchangeRates);


        datasource = new DatabaseAccessObject(this);
        datasource.open();

        List<ExchangeRate> rates = datasource.getExchangeRates();
        int rows = rates.size();
        //Log.d("Number of rows", String.valueOf(rows));

        for(int i = 0; i < rows; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView tv_from = new TextView(this);
            tv_from.setPadding(0,5,0,5);
            tv_from.setText(rates.get(i).getFrom());
            row.addView(tv_from);

            TextView tv_to = new TextView(this);
            tv_to.setPadding(0,5,0,5);
            tv_to.setText(rates.get(i).getTo());
            row.addView(tv_to);

            TextView tv_rate = new TextView(this);
            tv_rate.setPadding(0,5,0,5);
            DecimalFormat df = new DecimalFormat("0.00");
            tv_rate.setText(df.format(rates.get(i).getRate()));
            row.addView(tv_rate);

            TextView tv_date = new TextView(this);
            tv_date.setPadding(0,5,0,5);
            //TODO Make a check on the language of the phone and set the appropriate formatter
            //SimpleDateFormat formatterGerman = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            SimpleDateFormat formatterDefault = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateInString = rates.get(i).getDate();
            try {
                Date date = input.parse(dateInString);
                tv_date.setText(formatterDefault.format(date));
            } catch(ParseException e) {
                e.printStackTrace();
            }
            row.addView(tv_date);

            table.addView(row);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exchange_rates, menu);
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
