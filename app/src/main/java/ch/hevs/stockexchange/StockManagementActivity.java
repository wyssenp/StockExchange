package ch.hevs.stockexchange;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;


public class StockManagementActivity extends ActionBarActivity {

    private ListView listViewStocks;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_management);

        ctx = getApplicationContext();
        listViewStocks = (ListView) findViewById(R.id.listView);

        DatabaseAccessObject.open(ctx);
        Cursor stocks = DatabaseAccessObject.getStocks();


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
