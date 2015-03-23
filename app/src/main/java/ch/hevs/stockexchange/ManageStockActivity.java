package ch.hevs.stockexchange;

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
import ch.hevs.stockexchange.model.Stock;


public class ManageStockActivity extends ActionBarActivity {

    private Spinner spinner_markets;
    private EditText editTextSymbol;
    private EditText editTextName;
    private EditText editTextSector;
    private EditText editTextValue;
    private Button addStock;
    private DatabaseAccessObject datasource;
    private int stockId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stock);

        datasource = new DatabaseAccessObject(this);

        datasource.open();

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

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if(b != null) {
            stockId = b.getInt("stockId");

            Stock s = datasource.getStockById(stockId);

            editTextSymbol.setText(s.getSymbol());
            editTextName.setText(s.getName());
            editTextSector.setText(s.getSector());
            editTextValue.setText(Double.toString(s.getValue()));
            spinner_markets.setSelection((int) (s.getMarket().getId()-1));

            setTitle(R.string.title_activity_manage_stock_update);

            addStock.setText(R.string.sm_update_stock);
            addStock.setOnClickListener(new UpdateStockListener());
        } else {
            addStock.setOnClickListener(new NewStockListener());
        }
    }

    private class NewStockListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            checkFields();

            datasource.writeStock(editTextSymbol.getText().toString(),
                    editTextName.getText().toString(),
                    editTextSector.getText().toString(),
                    Double.parseDouble(editTextValue.getText().toString()),
                    spinner_markets.getSelectedItemPosition()+1); //Swiss market = 0, German market = 1
            datasource.close();

            Toast.makeText(ManageStockActivity.this,R.string.toast_stockCreated,Toast.LENGTH_SHORT).show();

            //Return to the calling activity
            finish();
        }
    }

    private class UpdateStockListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            checkFields();

            //Update database

        }
    }

    private void checkFields() {
        //If any of the text fields are empty, the user is informed
        if(isEmpty(editTextSymbol)||isEmpty(editTextName)||
                isEmpty(editTextSector)||isEmpty(editTextValue)) {
            Toast.makeText(ManageStockActivity.this,R.string.toast_fieldsEmpty,Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * Check if a text field (EditText) is empty
     * @param editText The EditText object
     * @return false if it's not empty, true if it's empty
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
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
