package ch.hevs.stockexchange;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import ch.hevs.stockexchange.backend.brokerModelApi.BrokerModelApi;
import ch.hevs.stockexchange.backend.brokerModelApi.model.BrokerModel;
import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Broker;


public class ManageBrokerActivity extends ActionBarActivity {

    private EditText editTextName;
    private EditText editTextBankType;
    private EditText editTextSecuritiesDealerType;
    private Button addBroker;
    private DatabaseAccessObject datasource;
    private int brokerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_manage_broker);
        setContentView(R.layout.activity_manage_broker);

        datasource = new DatabaseAccessObject(this);
        datasource.open();

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextBankType = (EditText) findViewById(R.id.editTextBankType);
        editTextSecuritiesDealerType = (EditText) findViewById(R.id.editTextSecuritiesDealerType);

        addBroker = (Button) findViewById(R.id.btn_addBroker);

        Bundle bundle = getIntent().getExtras();
        Resources res = getResources();

        /*
        Depending on whether the user creates a new stock or edits an existing stock, the appropriate listener is applied to the button
         */
        if(bundle != null) {
            brokerId = bundle.getInt("brokerId");
            Broker b = datasource.getBrokerById(brokerId);

            //Fill the EditText's with the correct data
            editTextName.setText(b.getName());
            editTextBankType.setText(b.getBankType());
            editTextSecuritiesDealerType.setText(b.getSecuritiesDealerType());

            //Change the title of the activity
            setTitle(String.format(res.getString(R.string.title_activity_manage_broker_update)));

            addBroker.setText(R.string.sm_update_stock);
            addBroker.setOnClickListener(new UpdateBrokerListener());
        } else {
            setTitle(String.format(res.getString(R.string.title_activity_manage_broker)));
            addBroker.setOnClickListener(new NewBrokerListener());
        }
    }

    /**
     * This inner class is used when the user creates a new stock
     * @author StefanEggenschwiler
     */
    private class NewBrokerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!checkFields()) {
                return;
            }

            //Update database
            datasource.createBroker(
                    editTextName.getText().toString(),
                    editTextBankType.getText().toString(),
                    editTextSecuritiesDealerType.getText().toString());

            datasource.close();

            BrokerModel brokerDS = new BrokerModel();
            brokerDS.setName(editTextName.getText().toString());
            brokerDS.setBankType(editTextBankType.getText().toString());
            brokerDS.setSecuritesDealerType(editTextSecuritiesDealerType.getText().toString());

            //Update datastore
            new InsertOrUpdateBrokerTask().execute(new Pair<BrokerModel, Long>(brokerDS, null));

            Toast.makeText(ManageBrokerActivity.this, R.string.toast_brokerCreated, Toast.LENGTH_SHORT).show();

            //Return to the calling activity
            finish();
        }
    }

    /**
     * This inner class is used when the user updates a stock
     * @author Stefan Eggenschwiler
     */
    private class UpdateBrokerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!checkFields()) {
                return;
            }

            //Update database
            datasource.updateBroker(brokerId,
                    editTextName.getText().toString(),
                    editTextBankType.getText().toString(),
                    editTextSecuritiesDealerType.getText().toString());

            datasource.close();

            BrokerModel brokerDS = new BrokerModel();
            brokerDS.setId((long) brokerId);
            brokerDS.setName(editTextName.getText().toString());
            brokerDS.setBankType(editTextBankType.getText().toString());
            brokerDS.setSecuritesDealerType(editTextSecuritiesDealerType.getText().toString());

            //Update datastore
            new InsertOrUpdateBrokerTask().execute(new Pair<BrokerModel, Long>(brokerDS, (long) brokerId));

            Toast.makeText(ManageBrokerActivity.this,R.string.toast_brokerUpdated,Toast.LENGTH_SHORT).show();

            //Return to the calling activity
            finish();

        }
    }

    /**
     * Helper method that makes sure that all fields (EditText) are filled
     */
    private boolean checkFields() {
        //If any of the text fields are empty, the user is informed
        if(isEmpty(editTextBankType) || isEmpty(editTextBankType) ||
                isEmpty(editTextSecuritiesDealerType)) {
            Toast.makeText(ManageBrokerActivity.this,R.string.toast_fieldsEmpty,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Check if a text field (EditText) is empty
     * @param editText The EditText object
     * @return false if it's not empty, true if it's empty
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private class InsertOrUpdateBrokerTask extends AsyncTask<Pair<BrokerModel,Long>, Void, Void> {

        /*
        Possible actions:
        save    Insert a new entry in the datastore (requires a BrokerModel object)
        update  Updating an entry in the datastore (requires a BrokerModel object and an ID)
         */

        private BrokerModelApi myService = null;

        @Override
        protected Void doInBackground(Pair<BrokerModel,Long>... params) {
            if (myService == null) {
                BrokerModelApi.Builder builder = new BrokerModelApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });

                myService = builder.build();
            }

            BrokerModel broker = params[0].first;
            Long brokerId = params[0].second;

            if(brokerId == null) {
                //When there's no Id it means that it's an Insert
                try {
                    myService.insert(broker).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Update
                try {
                    myService.update(brokerId,broker).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_broker, menu);
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
