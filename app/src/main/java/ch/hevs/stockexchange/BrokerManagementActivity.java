package ch.hevs.stockexchange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;

import ch.hevs.stockexchange.backend.brokerModelApi.BrokerModelApi;
import ch.hevs.stockexchange.dbaccess.DatabaseAccessObject;
import ch.hevs.stockexchange.model.Broker;


public class BrokerManagementActivity extends ActionBarActivity {

    private ListView listViewBroker;
    private DatabaseAccessObject datasource;
    private ArrayAdapter<Broker> adapter;
    private int brokerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_broker_management);
        setContentView(R.layout.activity_broker_management);
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
        super.onResume();
        setTitle(R.string.title_activity_broker_management);
        initializeList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_broker_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_add_broker) {
            Intent i = new Intent(this, ManageBrokerActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void removeBroker() {
        datasource.deleteBroker(brokerId);

        //Update datastore
        if(brokerId > 6)
            new DeleteBrokerTask().execute((long) brokerId);

        Toast.makeText(this, R.string.toast_brokerDeleted, Toast.LENGTH_SHORT).show();

        initializeList();
    }

    /**
     * Inner class to handle the deletion of an entry on the datastore
     */
    private class DeleteBrokerTask extends AsyncTask<Long, Void, Void> {

        private BrokerModelApi myService = null;

        @Override
        protected Void doInBackground(Long... params) {
            if (myService == null) {
                //Local
                /*BrokerModelApi.Builder builder = new BrokerModelApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });*/
                //Online (cloud)
                BrokerModelApi.Builder builder = new BrokerModelApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://stockexchange-hesso.appspot.com/_ah/api/");

                myService = builder.build();
            }

            try {
                myService.remove(params[0]).execute();
            } catch(IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void initializeList() {
        datasource = new DatabaseAccessObject(this);
        datasource.open();

        listViewBroker = (ListView) findViewById(R.id.listView);

        List<Broker> brokers = datasource.getBrokers();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,brokers);
        listViewBroker.setAdapter(adapter);

        //Click: will show a menu to edit or delete a stock
        listViewBroker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("getItem(position)",parent.getAdapter().getItem(position).toString());

                Broker b = (Broker) (parent.getAdapter().getItem(position));
                brokerId = (int) b.getId();
                parent.getItemAtPosition(position);

                createChooseDialog();
            }
        });

        //LongClick: just for testing, will have no functionality in the final build
        listViewBroker.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Selected item", "position=" + position + "; id=" + id);
                return false;
            }
        });
    }

    /**
     * Custom click listener for the dialog that shows when the user performs a long click on a stock
     * @author Stefan Eggenschwiler
     */
    private class MyClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which) {
                case 0: //Edit
                    //Open ManageStockActivity with the corresponding data
                    Intent i = new Intent(BrokerManagementActivity.this,ManageBrokerActivity.class);
                    i.putExtra("brokerId",brokerId);
                    startActivity(i);
                    break;
                case 1: //Delete
                    //Remove stock from the database and the datastore
                    removeBroker();
                    break;
                case 2: //Cancel
                    dialog.dismiss();
                    break;
            }
        }
    }
}
