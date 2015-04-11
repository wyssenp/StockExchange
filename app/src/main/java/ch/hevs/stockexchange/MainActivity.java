package ch.hevs.stockexchange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;

/**
 * Created by Pierre-Alain Wyssen on 12.03.2015.
 * Project: StockExchange
 * Package: ch.hevs.stockexchange
 * Description:
 * Main activity of the app. First screen that the user sees when he launches the app, contains a main menu.
 */
public class MainActivity extends ActionBarActivity {

    private Button myPortfolio;
    private Button stockExchange;
    private Button stockManagement;
    private Context ctx;
    private DatabaseAccessObject datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguage();
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        setContentView(R.layout.activity_main);

        myPortfolio = (Button) findViewById(R.id.btn_myPortfolio);
        stockExchange = (Button) findViewById(R.id.btn_stockExchange);
        stockManagement = (Button) findViewById(R.id.btn_stockMgmt);

        //Check if the phone is online or not; if not, the async task will not be executed
        //Source: http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()) {
            //Update the database with the most current exchange rates
            new DownloadTask().execute();
            Toast.makeText(ctx,R.string.toast_updateSucessfull,Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ctx,R.string.toast_noNetwork,Toast.LENGTH_LONG).show();
        }


        myPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, MyPortfolioActivity.class);
                startActivity(i);
            }
        });

        stockExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, StockExchangeActivity.class);
                startActivity(i);
            }
        });

        stockManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, StockManagementActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        setLanguage();
        super.onResume();
    }

    /**
     * Inner class used to update the exchange rates
     *
     * Sources used:
     * http://stackoverflow.com/questions/5787910/android-file-download-in-background
     * http://stackoverflow.com/questions/5360628/get-and-parse-csv-file-in-android
     */
    private class DownloadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                //Establish a connection through the Yahoo! Finance API to get a CSV with the current exchange rates
                URL url = new URL("http://download.finance.yahoo.com/d/quotes.csv?s=USDEUR=x,CHFUSD=x,CHFEUR=x&f=l1");
                URLConnection connection = url.openConnection();
                connection.connect();

                //Read the lines from the CSV and add them to an ArrayList
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                List<Double> exchangeRates = new ArrayList<>();

                String line;
                while((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");
                    exchangeRates.add(Double.parseDouble(rowData[0]));
                }

                reader.close();

                //Data available at that point: USDEUR,CHFUSD,CHFEUR
                //Calculate the remaining exchange rates by using the reciprocal value of the data available
                double eur2usd = 1/exchangeRates.get(0);
                double usd2chf = 1/exchangeRates.get(1);
                double eur2chf = 1/exchangeRates.get(2);

                //Open database and re-insert the values
                datasource = new DatabaseAccessObject(ctx);
                datasource.open();

                //Parameters: double chf_eur, double chf_usd, double eur_chf, double eur_usd, double usd_chf, double usd_eur
                datasource.updateExchangeRates(exchangeRates.get(2),exchangeRates.get(1),eur2chf,eur2usd,usd2chf,exchangeRates.get(0));

                datasource.close();

            } catch(IOException e) {
                //Gets thrown when no connection is available
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent i = new Intent(ctx, SettingsActivity.class);
            finish();
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
