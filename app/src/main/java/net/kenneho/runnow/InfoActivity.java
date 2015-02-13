package net.kenneho.runnow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.kenneho.runnow.adapters.TravelsAdapter;
import net.kenneho.runnow.customObjects.RealtimeTravel;
import net.kenneho.runnow.networking.HttpManager;
import net.kenneho.runnow.networking.IDownloadListener;
import net.kenneho.runnow.networking.NetworkManager;
import net.kenneho.runnow.utils.Utils;

import org.xmlpull.v1.XmlPullParserException;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_list_activity_view);
        Log.d(LOG, "Calling onCreate()");

        networkManager = new NetworkManager(this.getApplicationContext());
        if (!networkManager.isConnected()) {
            Log.i(LOG, "No internet connection. Calling finish() to return to previous activity");
            Utils.warnUser(getApplicationContext(), "Feilmelding", "Ingen internettforbindelse. ");
            finish();
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

        if(savedInstanceState == null || !savedInstanceState.containsKey("travels")) {
            Log.i(LOG, "Generating a new dataset to display...");
            generateListData(departureID, destinationID);
        }
        else {
            Log.i(LOG, "Re-using our old dataset when displaying the travels.");
            ArrayList<RealtimeTravel> travels = savedInstanceState.getParcelableArrayList("travels");

            try {
                Log.d(LOG, "Creating our adapter using the list of " + travels.size() + " travels");
                TravelsAdapter adapter = new TravelsAdapter(getApplicationContext(), travels);
                Log.d(LOG, "Attaching the adapter...");
                setListAdapter(adapter);
                Log.d(LOG, "Adding countdown timer to the list");
                addCountdownTimer(adapter);
                ringProgressDialog.dismiss();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

		swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
				android.R.color.holo_green_light, 
				android.R.color.holo_orange_light, 
				android.R.color.holo_red_light);

    }

	@Override 
	public void onRefresh() {
        Log.d(LOG, "Calling onRefresh()...");

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
        Log.d(LOG, "Calling onRestart()...");
        super.onRestart();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int size = getListAdapter().getCount();


        ArrayList<RealtimeTravel> travels = new ArrayList<RealtimeTravel>();
        for (int i = 0; i < size; i++) {
            RealtimeTravel travel = (RealtimeTravel) getListAdapter().getItem(i);
            travels.add(travel);

        }

        Log.i(LOG, "Saving our " + size + " travels to a parcel");
        outState.putParcelableArrayList("travels", travels);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        Log.d(LOG, "Calling onStop");
        super.onStop();
    }

	private void showProgress(String progress) {
		//mProgress.setProgress();
		ringProgressDialog.setMessage(progress);
	}
	
	private void empty() {

		Utils.warnUser(getApplicationContext(), "Informasjon", "Fant ingen ruter.");

    }

	private void generateListData(int departureID, int destinationID) {

		new GenerateList().execute(departureID, destinationID);

	}

    private void addCountdownTimer(TravelsAdapter adapter) {
        final TravelsAdapter myAdapter = adapter;
        final Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
                timerHandler.postDelayed(this, 1000); //run every second
            }

        };
        timerHandler.postDelayed(timerRunnable, 0);
    }


    private class GenerateList extends AsyncTask<Integer, String, TravelsAdapter> {

		@Override
		protected TravelsAdapter doInBackground(Integer... params) {
			Log.d(LOG, "Running background process to fetch data from Ruter....");

			myTravels = new ArrayList<RealtimeTravel>();
			TravelsAdapter adapter = null;

			int departureID = params[0];
			int destinationID = params[1];

			publishProgress("Laster ned reisealternativer fra Ruter...");
			try {

				publishProgress("Laster ned sanntidsdata fra Ruter...");

				myTravels = ruterManager.getTravels(departureID, destinationID);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {

				publishProgress("Gjør klar for å vise resultatene...");

				adapter = new TravelsAdapter(getApplicationContext(), myTravels);
			} catch (Exception e) {

				e.printStackTrace();
				Log.e(LOG, "Failed to create TravelsAdapter!");

			}

			ringProgressDialog.dismiss();

			return adapter;
		}

		protected void onPostExecute(TravelsAdapter adapter) {

			if (adapter != null) {

				if (adapter.getCount() == 0) {
					empty();
					
				}
				else {
					Log.d(LOG, "Will put " + adapter.getCount() + " elements in the list. Now, let's attach our adapter...");
					setListAdapter(adapter);

					// We've got a valid travel, so let's save it to our database 
					saveToDatabase();

                    addCountdownTimer(adapter);

				}
			}
			else {
				Log.i(LOG, "No travels found. Will notify the user");
				ringProgressDialog.dismiss();
				cancelActivity("Informasjon", getString(R.string.no_travel_found));
			}

		}


        protected void onProgressUpdate(String... progress) {
			showProgress(progress[0]);
		}

	}

	private void cancelActivity(String title, String warning) {
		AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);

		builder.setMessage(warning)
		.setTitle(title);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish(); // Exit the activity
			}
		});
		AlertDialog dialog = builder.create();

		dialog.show();

	}

	private void saveToDatabase() {

		if (databaseManager.entryExists(departureName, destinationName)) {
			Log.d(LOG, "Entry exists, not storing");
			return;
		}

		databaseManager.saveTravelEntry(departureName, departureID + "", destinationName, destinationID + "");

	}
}
