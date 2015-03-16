package ch.hevs.stockexchange;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Stock;


public class StockManagementActivity extends ActionBarActivity {

    private ListView listViewStocks;
    private DatabaseAccessObject datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_management);

        datasource = new DatabaseAccessObject(this);
        datasource.open();

        listViewStocks = (ListView) findViewById(R.id.listView);

        List<Stock> stocks = datasource.getStocks();

        ArrayAdapter<Stock> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,stocks);
        listViewStocks.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_management, menu);
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

        if(id == R.id.action_add_stock) {
            Intent i = new Intent(this, ManageStockActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
