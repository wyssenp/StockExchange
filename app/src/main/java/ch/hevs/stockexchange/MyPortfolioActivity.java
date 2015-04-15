package ch.hevs.stockexchange;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Currency;
import ch.hevs.stockexchange.model.Portfolio;


public class MyPortfolioActivity extends ActionBarActivity {

    private ListView list_myStocks;
    private DatabaseAccessObject datasource;
    private ArrayAdapter<Portfolio> adapter;
    private Dialog sellDialog;
    private NumberPicker sellPicker;
    private List<Portfolio> portfolios;
    private Portfolio selectedPortfolio;
    private Button total_btn;
    private NumberFormat currencyFormat;
    private Currency c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguage();
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_my_portfolio);
        setContentView(R.layout.activity_my_portfolio);
        total_btn = (Button) findViewById(R.id.total_button);
        initializeList();
        total_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTotalDialog().show();
            }
        });
    }

    /**
     * This method as called after onCreate
     * It's also used to "refresh" the list when a new stock has been added
     */
    @Override
    protected void onResume() {
        setLanguage();
        super.onResume();
        setTitle(R.string.title_activity_my_portfolio);
        initializeList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_portfolio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_buyStock) {
            Intent i = new Intent(this, StockExchangeActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeList() {
        datasource = new DatabaseAccessObject(this);
        datasource.open();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String currency = sharedPref.getString("current_currency", "1");
        c = datasource.getCurrencyById(Long.parseLong(currency));

        list_myStocks = (ListView) findViewById(R.id.list_stocks);

        portfolios = datasource.getPortfolio(c);

        // Convert value of each stock in the portfolio to current currency if needed
        for(Portfolio p : portfolios) {
            double exchangerate;
            if(p.getStock().getMarket().getSymbol().equals("SIX") && !c.getSymbol().equals("CHF")) {
                exchangerate = datasource.getExchangeRateByCurrencies(1, (int)c.getId());
                p.getStock().setValue(Math.round((p.getStock().getValue()*exchangerate)*100.0)/100.0);
            } else if(p.getStock().getMarket().getSymbol().equals("DBAG") && !c.getSymbol().equals("EUR")) {
                exchangerate = datasource.getExchangeRateByCurrencies(3, (int)c.getId());
                p.getStock().setValue(Math.round((p.getStock().getValue()*exchangerate)*100.0)/100.0);
            }
        }

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,portfolios);
        list_myStocks.setAdapter(adapter);

        //Click: will show a menu to edit or delete a stock
        list_myStocks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("getItem(position)",parent.getAdapter().getItem(position).toString());

                selectedPortfolio = (Portfolio) (parent.getAdapter().getItem(position));
                parent.getItemAtPosition(position);

                createChooseDialog();
            }
        });
    }

    /**
     * <p>When the user clicks on a portfolio entry a dialog shows where he can choose to sell the
     * stock. He can also cancel the action.</p>
     * <p>The options to choose from are:</p>
     * <ul>
     * <li>Index 0 = Sell</li>
     * <li>Index 1 = Cancel</li>
     * </ul>
     */
    private void createChooseDialog() {
        DialogInterface.OnClickListener clickListener = new MyClickListener();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.sm_dialog_chooseAction);
        dialog.setCancelable(true);
        dialog.setItems(R.array.mp_dialog_choices, clickListener);
        dialog.show();
    }

    /**
     * Custom click listener for the dialog that shows when the user performs a long click on a
     * portfolio entry.
     * @author Stefan Eggenschwiler
     */
    private class MyClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which) {
                case 0: //Delete
                    //Remove stock from the database
                    //sellStock(portfolioId);
                    dialog.dismiss();
                    createSellDialog().show();
                    break;
                case 1: //Cancel
                    dialog.dismiss();
                    break;
            }
        }
    }

    /**
     * Method used by the sell dialog in order to update the portfolio.
     */
    private void sellStock() {
        if(selectedPortfolio.getAmount() > sellPicker.getValue()) {
            datasource.updatePortfolio(selectedPortfolio.getId(), selectedPortfolio.getAmount()-sellPicker.getValue());
        } else {
            datasource.deletePortfolio(selectedPortfolio.getId());
        }
        Toast.makeText(this,R.string.toast_stockSold,Toast.LENGTH_SHORT).show();

        initializeList();
    }

    /**
     * Initializes the sell dialog.
     * @return Builder of the dialog
     */
    private AlertDialog.Builder createSellDialog() {
        Resources res = getResources();
        sellPicker = new NumberPicker(MyPortfolioActivity.this);
        sellPicker.setMinValue(1);
        sellPicker.setMaxValue(selectedPortfolio.getAmount());
        sellPicker.setWrapSelectorWheel(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(String.format(res.getString(R.string.mp_sell_dialog_title), selectedPortfolio.getStock().getName()));
        builder.setView(sellPicker);
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.mp_sell_dialog_sell_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sellStock();
            }
        });

        builder.setNegativeButton(R.string.mp_sell_dialog_cancel_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });
        return builder;
    }

    /**
     * Initializes the total value of the portfolio dialog.
     * @return Builder of the dialog
     */
    private AlertDialog.Builder createTotalDialog() {
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(String.format(res.getString(R.string.mp_summary_dialog_text), calculateTotal() + c.toString()));
        builder.setCancelable(true);

        builder.setNegativeButton(R.string.mp_summary_dialog_back_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });
        return builder;
    }

    /**
     * Function for calculating the total for the portfolio.
     * @return Double of the total
     */
    private String calculateTotal() {
        double total = 0.0;
        for(Portfolio p : portfolios) {
            total += (p.getAmount() * p.getStock().getValue());
        }
        return currencyFormat.format(Math.round(total * 100.0) / 100.0);
    }

    /**
     * This method sets the current application language to the selected one and sets the
     * number format of the portfolio total.
     */
    private void setLanguage() {
        // Get the current language from shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = sharedPref.getString("current_language", "");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
        currencyFormat = NumberFormat.getNumberInstance(locale);
    }
}
