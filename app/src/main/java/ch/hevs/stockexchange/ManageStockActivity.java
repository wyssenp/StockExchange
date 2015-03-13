package ch.hevs.stockexchange;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;


public class ManageStockActivity extends ActionBarActivity {

    private Spinner spinner_markets;
    private EditText editTextSymbol;
    private EditText editTextName;
    private EditText editTextSector;
    private EditText editTextValue;
    private Button addStock;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stock);

        DatabaseAccessObject.open(ManageStockActivity.this);

        spinner_markets = (Spinner) findViewById(R.id.spinner_markets);

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this,
                R.array.sm_array_markets, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_markets.setAdapter(adapter_spinner);

        editTextSymbol = (EditText) findViewById(R.id.editTextSymbol);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextSector = (EditText) findViewById(R.id.editTextSector);
        editTextValue = (EditText) findViewById(R.id.editTextValue);

        addStock = (Button) findViewById(R.id.btn_addStock);
        ctx = getApplicationContext();

        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseAccessObject.writeStock(editTextSymbol.getText().toString(),
                        editTextName.getText().toString(),
                        editTextSector.getText().toString(),
                        Double.parseDouble(editTextValue.getText().toString()),
                        spinner_markets.getSelectedItemPosition());
                DatabaseAccessObject.close();

                Toast.makeText(ManageStockActivity.this,R.string.toast_stockCreated,Toast.LENGTH_SHORT).show();

                finish();
                //Intent i = new Intent(ctx, StockManagementActivity.class);
                //startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_stock, menu);
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
