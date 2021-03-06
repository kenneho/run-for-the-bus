package net.kenneho.runnow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.kenneho.runnow.adapters.TravelsAdapter;
import net.kenneho.runnow.customObjects.RealtimeTravel;
import net.kenneho.runnow.networking.HttpManager;
import net.kenneho.runnow.networking.IDownloadListener;
import net.kenneho.runnow.networking.NetworkManager;
import net.kenneho.runnow.utils.Utils;
import org.xmlpull.v1.XmlPullParserException;
import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class InfoActivity extends ListActivity implements OnRefreshListener {

    private final String LOG = "InfoActivity";
    private IDownloadListener downloadListener;
    private RuterManager ruterManager;
    private SwipeRefreshLayout swipeLayout;
    private int departureID, destinationID;
    private ProgressBar mProgress;
    private ProgressDialog ringProgressDialog;
    private DatabaseManager databaseManager;
    private String departureName, destinationName;
    private HttpManager httpManager;
    private NetworkManager networkManager;
    private List<RealtimeTravel> myTravels; // Will be modified in separate thread.
    private final Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private TravelsAdapter travelsAdapter;
    boolean callbackPresent = false;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_activity_view);
        Log.d(LOG, "onCreate()");

        networkManager = new NetworkManager(this.getApplicationContext());
        if (!networkManager.isConnected()) {
            Log.i(LOG, "No internet connection. Calling finish() to return to previous activity");
            Utils.warnUser(getApplicationContext(), "Feilmelding", "Ingen internettforbindelse. ");
            exitActivity();
            return;
        }

        // TODO: Already initialized in MainActivity. Look into dependency injections to avoid
        // initializing multiple instances of this class
        httpManager = new HttpManager();

        ruterManager = new RuterManager(httpManager);
        databaseManager = new DatabaseManager();

        ringProgressDialog = ProgressDialog.show(this, "Vennligst vent ...", "Vennligs vent ...", true);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        // get data via the key
        departureID = extras.getInt(MainActivity.DEPARURE_MESSAGE);
        departureName = extras.getString(MainActivity.DEPARURE_MESSAGE_NAME);
        destinationID = extras.getInt(MainActivity.DESTINATION_MESSAGE);
        destinationName = extras.getString(MainActivity.DESTINATION_MESSAGE_NAME);

        Log.v(LOG, departureName + "(" + departureID + ") => " + destinationName + "(" + destinationID + ")");

        setTitle(departureName);
        View header = getLayoutInflater().inflate(R.layout.item_ruterdata_header, null);
        ListView listView = getListView();
        listView.addHeaderView(header);

        getListView().setHeaderDividersEnabled(true);

        if (savedInstanceState == null) {
            Log.i(LOG, "Generating a new dataset to display...");
            generateListData(departureID, destinationID);
        }

        swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        /*
        * If the user exits the application before the adapter has been set, our
        * list adapter is null. .
        * */
        if (getListAdapter() == null) {
            Log.i(LOG, "Saving instance state, but our listadapter is null");
            return;
        }

        int size = getListAdapter().getCount();

        ArrayList<RealtimeTravel> travels = new ArrayList<RealtimeTravel>();
        for (int i = 0; i < size; i++) {
            RealtimeTravel travel = (RealtimeTravel) getListAdapter().getItem(i);
            travels.add(travel);
        }

        Log.i(LOG, "onSaveInstanceState(): Saving our " + size + " travels to a parcel");
        outState.putParcelableArrayList("travels", travels);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(LOG, "onRestoreInstanceState(): Re-using our old dataset when displaying the travels.");

        ArrayList<RealtimeTravel> travels = savedInstanceState.getParcelableArrayList("travels");

        try {
            Log.d(LOG, "Creating our adapter using the list of " + travels.size() + " travels");
            TravelsAdapter adapter = new TravelsAdapter(getApplicationContext(), travels);
            setListAdapter(adapter);
            addCountdownTimer(adapter);
            ringProgressDialog.dismiss();

        }
        catch (Exception e) {
            Utils.warnUser(getApplicationContext(), "Informasjon", getString(R.string.prefixErrorCode) + " 20.");
            Log.e(LOG, "Something went really wrong! This is the exception thrown: " + e.toString());
            e.printStackTrace();
            exitActivity();
        }

        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onRefresh() {
        Log.d(LOG, "onRefresh()...");

        if (!networkManager.isConnected()) {
            Log.d(LOG, "We're NOT connected to the Internet. Aborting...");
            AlertDialog  dialog = Utils.warnUser(this, "Feil", "Du er ikke koblet til Internett.");

        }
        else {
            Log.d(LOG, "Refreshing the list....");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                    Log.d(LOG, "onRefresh()");
                    generateListData(departureID, destinationID);

                }
            }, 5000);
        }
    }

    @Override
    public void onRestart() {
        Log.d(LOG, "onRestart()");
        super.onRestart();

    }

    @Override
    public void onBackPressed() {
        Log.d(LOG, "onBackPressed()");
        exitActivity();
    }

    @Override
    public void onStop() {
        Log.d(LOG, "onStop()");
        super.onStop();
    }

    @Override
    public void onStart() {
        Log.d(LOG, "onStart()");
        super.onStart();
    }

    @Override
    public void onPause() {
        Log.d(LOG, "onPause(): Removing timerHandler callbacks....");
        callbackPresent = false;
        timerHandler.removeCallbacks(timerRunnable); // No need to update anything while sleeping
        super.onPause();
    }

    @Override
    public void onResume() {
        if (!callbackPresent) {
            callbackPresent = true;
            Log.d(LOG, "onResume(): Restarting timerHandler with timerHandler.postDelayed...");
            timerHandler.postDelayed(timerRunnable, 1000);
        }
        else {
            Log.d(LOG, "onResume()");
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG, "onDestroy()");
        super.onDestroy();
    }

    private void exitActivity() {
        Log.i(LOG, "Returning to MainActivity by calling finish()...");
        finish();
    }

    private void showProgress(String progress) {
        ringProgressDialog.setMessage(progress);
    }

    private void empty() {
        Utils.warnUser(getApplicationContext(), "Informasjon", getString(R.string.no_travel_found));
    }

    private void generateListData(int departureID, int destinationID) {
        new GenerateList().execute(departureID, destinationID);
    }

    private void removeExpiredTravels(TravelsAdapter myTravels) throws NullPointerException {
        List<RealtimeTravel> removeList = new ArrayList<RealtimeTravel>();
        for (RealtimeTravel travel : myTravels.getItems()) {

            if (travel == null) {
                Log.e(LOG, "Read a null object from the adapter.");
                exitActivity();
            }

            if (hasExpired(travel)) {

                try {
                    removeList.add(travel);
                } catch (IndexOutOfBoundsException e) {
                    throw new IndexOutOfBoundsException("Failed to remove item from the TravelsAdapter.");
                }
            }
        }

        for (RealtimeTravel travel : removeList) {
            Log.i(LOG, "Removing expired entry " + travel.getRealtimeDepartureTime());
            myTravels.remove(travel);
        }
    }

    private void addCountdownTimer(final TravelsAdapter adapter) {
        final TravelsAdapter myAdapter = adapter;
        Log.d(LOG, "Adding CountdownTimer");

        // Make sure we stop existing timers, if any.
        timerHandler.removeCallbacks(timerRunnable);

        timerRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    removeExpiredTravels(myAdapter);
                }
                catch (NullPointerException npe) {
                    Log.e(LOG, "Caught an nullpointer while removing expired travels. Exiting the activity. ");
                    exitActivity();
                }
                myAdapter.notifyDataSetChanged();

                /*
                 * Make the handler send a message to the Runnable object every second,
                 * in effect making the Runnable object call itself every second
                 *
                 * */
                timerHandler.postDelayed(this, 1000); // Re-schedule running "this", i.e the Runnable object, 1000 ms into the future
            }

        };

        callbackPresent = true;

        // Fire off the first run
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private class GenerateList extends AsyncTask<Integer, String, Object> {

        @Override
        protected Object doInBackground(Integer... params) {
            Log.d(LOG, "Running background process to fetch data from Ruter....");

            myTravels = new ArrayList<RealtimeTravel>();

            int departureID = params[0];
            int destinationID = params[1];

            publishProgress("Laster ned reisealternativer fra Ruter...");
            try {

                publishProgress("Laster ned sanntidsdata fra Ruter...");

                myTravels = ruterManager.getTravels(departureID, destinationID);

            } catch (IllegalArgumentException e) {
                Log.i(LOG, "ruterManager.getTravels threw this IllegalArgumentException: " + e.toString());
                return getString(R.string.no_travel_found);
            } catch (NetworkErrorException e) {
                Log.i(LOG, "ruterManager.getTravels threw this NetworkErrorException: " + e.toString());
                return getString(R.string.network_error);
            } catch (IOException e) {
                Log.i(LOG, "ruterManager.getTravels threw this IOException: " + e.toString());
                return getString(R.string.prefixErrorCode) + "2";
            } catch (XmlPullParserException e) {
                Log.i(LOG, "ruterManager.getTravels threw this XmlPullParserException: " + e.toString());
                return getString(R.string.prefixErrorCode) + "3";
            } catch (Exception e) {
                Log.i(LOG, "ruterManager.getTravels threw this Exception: " + e.toString());
                return getString(R.string.prefixErrorCode) + "4";
            }

            try {
                publishProgress("Gjør klar for å vise resultatene...");
                travelsAdapter = new TravelsAdapter(getApplicationContext(), myTravels);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG, "Failed to create TravelsAdapter!");

            }

            ringProgressDialog.dismiss();

            return travelsAdapter;
        }

        protected void onPostExecute(Object value) {
            /*
            * Errors from the background thread are returned as String values.
            * */
            if (value instanceof String) {
                String errorMessage = (String) value;
                ringProgressDialog.dismiss();
                cancelActivity(getString(R.string.dialogHeader_error), errorMessage);

            } else if (value instanceof TravelsAdapter) {
                Log.d(LOG, "Back in the UI thread. Let's attach the adapter to the listview.");
                TravelsAdapter adapter = (TravelsAdapter) value;
                if (adapter != null) {

                    if (adapter.getCount() == 0) {
                        cancelActivity("Informasjon", getString(R.string.no_travel_found));
                    } else {
                        Log.d(LOG, "Will put " + adapter.getCount() + " elements in the list. Now, let's attach our adapter...");
                        setListAdapter(adapter);

                        // We've got a valid travel, so let's save it to our database
                        saveToDatabase();
                        updateSearchTimestamp();
                        addCountdownTimer(adapter);
                    }
                } else {
                    Log.i(LOG, "No travels found. Will notify the user");
                    ringProgressDialog.dismiss();
                    cancelActivity("Informasjon", getString(R.string.no_travel_found));
                }
            } else {
                Log.i(LOG, "Background task returned an object of type " + value.getClass());
                cancelActivity(getString(R.string.dialogHeader_error), getString(R.string.prefixErrorCode) + "1.");
            }

        }

        protected void onProgressUpdate(String... progress) {
            showProgress(progress[0]);
        }

    }

    private void cancelActivity(String title, String warning) {
        Log.d(LOG, "cancelActivity(): Title " + title + ", message: " + warning);

        AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Log.d(LOG, "Calling finish()");
                finish(); // Exit the activity
            }
        });

        builder.setMessage(warning)
                .setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveToDatabase() {

        if (databaseManager.entryExists(departureName, destinationName)) {
            Log.d(LOG, "Database entry exists, not storing");
            return;
        }

        databaseManager.saveTravelEntry(departureName, departureID + "", destinationName, destinationID + "");

    }

    private void updateSearchTimestamp() {
        databaseManager.updateTimestamp(departureName, destinationName);
    }

    private boolean hasExpired(RealtimeTravel travel) {

        Date now = Utils.getTimestamp();

        if (now.after(travel.getExpirationTime())) {
            return true;
        } else return false;
    }
}
