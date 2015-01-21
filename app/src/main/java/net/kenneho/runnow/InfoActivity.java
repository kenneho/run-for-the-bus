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
import android.widget.ListView;
import android.widget.ProgressBar;

public class InfoActivity extends ListActivity implements OnRefreshListener {
    private final String LOG = "InfoActivity";

    private NetworkManager networkManager;
	private IDownloadListener downloadListener;
	private RuterManager ruterManager;
	private SwipeRefreshLayout swipeLayout;
	private int departureID, destinationID;
	private ProgressBar mProgress;
	private ProgressDialog ringProgressDialog;
	private DatabaseManager databaseManager;
	private String departureName, destinationName;
	private HttpManager httpManager;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_list_activity_view);

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

		generateListData(departureID, destinationID);

		swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_container);		
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
				android.R.color.holo_green_light, 
				android.R.color.holo_orange_light, 
				android.R.color.holo_red_light);
	}

	@Override 
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
			@Override public void run() {
				swipeLayout.setRefreshing(false);
				Log.d(LOG, "onRefresh()");
				generateListData(departureID, destinationID);

			}
		}, 5000);
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

	private class GenerateList extends AsyncTask<Integer, String, TravelsAdapter> {

		@Override
		protected TravelsAdapter doInBackground(Integer... params) {
			Log.d(LOG, "Running background process to fetch data from Ruter....");

			List<RealtimeTravel> myTravels = new ArrayList<RealtimeTravel>();
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
