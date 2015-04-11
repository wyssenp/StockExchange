package ch.hevs.stockexchange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Currency;
import ch.hevs.stockexchange.model.Stock;


public class StockManagementActivity extends ActionBarActivity {

    private ListView listViewStocks;
    private DatabaseAccessObject datasource;
    private ArrayAdapter<Stock> adapter;
    private int stockId;
    private Currency c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguage();
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_stock_management);
        setContentView(R.layout.activity_stock_management);
    }

    /**
     * <p>When the user clicks on a stock a dialog shows where he can choose whether he
     * wants to edit or delete a stock. He can also cancel the action.</p>
     * <p>The options to choose from are:</p>
     * <ul>
     * <li>Index 0 = Edit</li>
     * <li>Index 1 = Delete</li>
     * <li>Index 2 = Cancel</li>
     * </ul>
     */
    private void createChooseDialog() {
        DialogInterface.OnClickListener clickListener = new MyClickListener();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.sm_dialog_chooseAction);
        dialog.setCancelable(true);
        dialog.setItems(R.array.sm_dialog_choices, clickListener);
        dialog.show();

    }

    /**
     * This method as called after onCreate
     * It's also used to "refresh" the list when a new stock has been added
     */
    @Override
    protected void onResume() {
        setLanguage();
        super.onResume();
        setTitle(R.string.title_activity_stock_management);
        initializeList();
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

        if(id == R.id.action_add_stock) {
            Intent i = new Intent(this, ManageStockActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeStock(int stockId) {
        datasource.deleteStock(stockId);

        Toast.makeText(this,R.string.toast_stockDeleted,Toast.LENGTH_SHORT).show();

        initializeList();
    }

    private void initializeList() {
        datasource = new DatabaseAccessObject(this);
        datasource.open();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String currency = sharedPref.getString("current_currency", "");
        c = datasource.getCurrencyById(Long.parseLong(currency));

        listViewStocks = (ListView) findViewById(R.id.listView);

        List<Stock> stocks = datasource.getStocks(c);

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,stocks);
        listViewStocks.setAdapter(adapter);

        //Click: will show a menu to edit or delete a stock
        listViewStocks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("getItem(position)",parent.getAdapter().getItem(position).toString());

                Stock s = (Stock) (parent.getAdapter().getItem(position));
                stockId = (int) s.getId();
                parent.getItemAtPosition(position);

                createChooseDialog();
            }
        });

        //LongClick: just for testing, will have no functionality in the final build
        listViewStocks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Selected item", "position="+position+"; id="+id);
                return false;
            }
        });
    }

    /**
     * Custom click listener for the dialog that shows when the user performs a long click on a stock
     * @author Pierre-Alain Wyssen
     */
    private class MyClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which) {
                case 0: //Edit
                    //Open ManageStockActivity with the corresponding data
                    Intent i = new Intent(StockManagementActivity.this,ManageStockActivity.class);
                    i.putExtra("stockId",stockId);
                    startActivity(i);
                    break;
                case 1: //Delete
                    //Remove stock from the database
                    removeStock(stockId);
                    break;
                case 2: //Cancel
                    dialog.dismiss();
                    break;
            }
        }
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
