package ch.hevs.stockexchange;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Stock;


public class StockExchangeActivity extends ActionBarActivity {

    private Spinner spinner_markets;
    private ListView list_stocks;
    private DatabaseAccessObject datasource;
    private ArrayAdapter<Stock> adapter;
    private int stockId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_exchange);

        /*
         * Options for the Spinner:
         * 0 = All
         * 1 = Swiss market
         * 2 = German market
         */
        spinner_markets = (Spinner) findViewById(R.id.spinner_markets);

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this,
                R.array.array_markets, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_markets.setAdapter(adapter_spinner);

        spinner_markets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("position; id",position+"; "+id);
                switch(position) {
                    case 0: //All
                        initializeList(0);
                        break;
                    case 1: //Swiss market
                    case 2: //German market
                        initializeList(position);
                        break;
                }
            }

            //Not needed
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeList(0);
    }

    private class MyListViewOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Stock s = adapter.getItem(position);
            stockId = (int) s.getId();
            parent.getItemAtPosition(position);
            //Log.d("Market",s.getMarket().toString());

            Intent i = new Intent(StockExchangeActivity.this, StockDetailsActivity.class);
            i.putExtra("stockId",stockId);
            startActivity(i);
        }
    }

    private void initializeList(int marketId) {
        datasource = new DatabaseAccessObject(this);
        datasource.open();

        list_stocks = (ListView) findViewById(R.id.list_stocks);

        List<Stock> stocks;
        if(marketId == 0) {
            stocks = datasource.getStocks();
        } else {
            stocks = datasource.getStocksWithMarket(marketId);
        }

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,stocks);
        list_stocks.setAdapter(adapter);

        list_stocks.setOnItemClickListener(new MyListViewOnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_exchange, menu);
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
