package ch.hevs.stockexchange;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;


public class MainActivity extends ActionBarActivity {

    private Button myPortfolio;
    private Button stockExchange;
    private Button stockManagement;
    private Context ctx;
    private DatabaseAccessObject datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPortfolio = (Button) findViewById(R.id.btn_myPortfolio);
        stockExchange = (Button) findViewById(R.id.btn_stockExchange);
        stockManagement = (Button) findViewById(R.id.btn_stockMgmt);

        ctx = getApplicationContext();

        new DownloadTask().execute();

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

    private class DownloadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://download.finance.yahoo.com/d/quotes.csv?s=USDEUR=x,CHFUSD=x,CHFEUR=x&f=l1");
                URLConnection connection = url.openConnection();
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                List<Double> exchangeRates = new ArrayList<>();

                String line;
                while((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");
                    exchangeRates.add(Double.parseDouble(rowData[0]));
                }

                reader.close();

                //Data available at that point: USDEUR,CHFUSD,CHFEUR
                double eur2usd = 1/exchangeRates.get(0);
                double usd2chf = 1/exchangeRates.get(1);
                double eur2chf = 1/exchangeRates.get(2);

                datasource = new DatabaseAccessObject(ctx);
                datasource.open();

                //double chf_eur, double chf_usd, double eur_chf, double eur_usd, double usd_chf, double usd_eur
                datasource.updateExchangeRates(exchangeRates.get(2),exchangeRates.get(1),eur2chf,eur2usd,usd2chf,exchangeRates.get(0));

                datasource.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e) {
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
