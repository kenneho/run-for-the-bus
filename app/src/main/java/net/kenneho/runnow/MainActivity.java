package net.kenneho.runnow;

import java.util.ArrayList;
import java.util.List;
import net.kenneho.runnow.adapters.DB_ListAdapter;
import net.kenneho.runnow.adapters.PlacesAutoCompleteAdapter;
import net.kenneho.runnow.database.DB_Travel;
import net.kenneho.runnow.jsonDefinitions.JsonPlace;
import net.kenneho.runnow.networking.HttpManager;
import net.kenneho.runnow.networking.NetworkManager;
import net.kenneho.runnow.utils.Utils;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "net.kenneho.RunNow.MESSAGE";
    public final static String DEPARURE_MESSAGE = "new.kenneho.RunNow.FROM";
    public final static String DEPARURE_MESSAGE_NAME = "new.kenneho.RunNow.FROM_NAME";
    public final static String DESTINATION_MESSAGE = "new.kenneho.RunNow.TO";
    public final static String DESTINATION_MESSAGE_NAME = "new.kenneho.RunNow.TO_NAME";

    private final String LOG = "MainActivity";
    private String departureName, destinationName;
    private DelayAutoCompleteTextView textviewDeparture, textviewDestination;
    private int uniqueDepartureStation, uniqueDestinationStation;
    private RuterManager ruterManager;
    private PlacesAutoCompleteAdapter placesAdapter;
    private DatabaseManager databaseManager;
    private DB_ListAdapter db_ListAdapter;
    private List<DB_Travel> recentTravels;
    private Context myContext;
    private ProgressBar progressBar1;
    private TextView t;
    private HttpManager httpManager;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = this;

        networkManager = new NetworkManager(this);
        Log.i(LOG, "Creating MainActivity...");

        setContentView(R.layout.activity_main);

        progressBar1 = (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator);
        httpManager = new HttpManager();
        ruterManager = new RuterManager(httpManager);
        textviewDeparture =(DelayAutoCompleteTextView) findViewById(R.id.text_departure);
        textviewDestination =(DelayAutoCompleteTextView) findViewById(R.id.text_destination);

        setTextfieldListeners();
        setButtonListener();

        databaseManager = new DatabaseManager();
        try {
            createRecentList();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong when generating the list of recent travels.");
        }

        //debug_printDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clear_history:
                Toast.makeText(this, "Clearing history...", Toast.LENGTH_SHORT).show();

                databaseManager.clearDatabase();
                recentTravels.clear();
                t.setVisibility(View.GONE);
                db_ListAdapter.notifyDataSetChanged();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createRecentList() throws Exception {

        recentTravels = (ArrayList<DB_Travel>) databaseManager.getAll();
        ListView recentListview = (ListView) findViewById(R.id.list_history);
        t = (TextView) findViewById(R.id.historiTextView);

        db_ListAdapter = new DB_ListAdapter(this, recentTravels);

        if (db_ListAdapter.getCount() == 0) {
            t.setVisibility(View.GONE);
        }
        else t.setVisibility(View.VISIBLE);

        recentListview.setAdapter(db_ListAdapter);
        recentListview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DB_Travel t = db_ListAdapter.getItem(position);

                uniqueDepartureStation = Integer.parseInt(t.departureID);
                departureName = t.departureName;
                destinationName = t.destinationName;
                uniqueDestinationStation = Integer.parseInt(t.destinationID);

                verifyAndCreateIntent();

                Toast.makeText(getApplicationContext(),
                        t.departureName + " => " + t.destinationName, Toast.LENGTH_LONG)
                        .show();

                t.updateTimestamp();
            }
        });
    }

    /*
    * For development purposes
    * */
    private void debug_printDatabase() {
        List<DB_Travel> recently = databaseManager.getAll();
        for (DB_Travel travel : recently) {
            Log.d(LOG, "From database: " + travel.departureName + " => " + travel.destinationName + " at " + travel.timestamp);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setTextfieldListeners() {

        placesAdapter = new PlacesAutoCompleteAdapter(this, ruterManager, progressBar1);

        textviewDeparture.setAdapter(placesAdapter);
        textviewDeparture.setLoadingIndicator(progressBar1);

        textviewDestination.setAdapter(placesAdapter);
        textviewDestination.setLoadingIndicator(
                (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator));

        textviewDeparture.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

                JsonPlace place = (JsonPlace) parent.getItemAtPosition(position);
                departureName = place.getPlaceName();
                textviewDeparture.setText(departureName);
                uniqueDepartureStation = place.getID();

                Log.i(LOG, "The selected departure " + place.getPlaceName() + " has ID " + uniqueDepartureStation);

            }
        });

        textviewDeparture.setOnClickListener(new OnClickListener() {

            public void clicked(View v) {
                // TODO
            }

            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        textviewDestination.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                JsonPlace place = (JsonPlace) parent.getItemAtPosition(position);
                destinationName = place.getPlaceName();
                textviewDestination.setText(destinationName);
                uniqueDestinationStation = place.getID();

                Log.i(LOG, "The selected departure " + place.getPlaceName() + " has ID " + uniqueDestinationStation);

            }
        });
    }

    private void setButtonListener() {

        Button button = (Button) findViewById(R.id.button_search);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                verifyAndCreateIntent();

            }
        });
    }


    private void verifyAndCreateIntent() {

        if (!networkManager.isConnected()) {
            Log.d(LOG, "We're NOT connected to the Internet. Aborting...");
            Utils.warnUser(this, "Feil", "Vi er ikke koblet til Internett.");
            return;
        }
        else {
            Log.d(LOG, "We're connected to the Internet. Proceeding...");
        }

        if (uniqueDepartureStation == 0 || uniqueDestinationStation == 0) {
            Log.i(LOG, "Departure or destination have ID == 0");
            Utils.warnUser(myContext, "Informasjon", "Fant ikke stasjonen.");
        }
        else if (uniqueDepartureStation == uniqueDestinationStation) {
            Log.i(LOG, "Departure and destination have the same ID.");
            Utils.warnUser(myContext, "Informasjon", "Avgang og destinasjon er lik.");
        }
        else {
            createIntent();
            finish();
        }
    }

    protected void createIntent() {

        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
        intent.putExtra(DEPARURE_MESSAGE, uniqueDepartureStation);
        intent.putExtra(DEPARURE_MESSAGE_NAME, departureName);
        intent.putExtra(DESTINATION_MESSAGE, uniqueDestinationStation);
        intent.putExtra(DESTINATION_MESSAGE_NAME, destinationName);

        Log.v(LOG, "Starting Intent for class " + InfoActivity.class.getName() + " with departure name " + departureName + "(" + uniqueDepartureStation + ") and destination name " + destinationName + "("+ uniqueDestinationStation + ")");
        startActivity(intent);

    }
}
