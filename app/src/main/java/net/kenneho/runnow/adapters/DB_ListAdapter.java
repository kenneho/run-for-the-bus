package net.kenneho.runnow.adapters;

import java.util.List;

import net.kenneho.runnow.R;
import net.kenneho.runnow.database.DB_Travel;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DB_ListAdapter extends ArrayAdapter<DB_Travel> {
	private final String LOG = "DB_ListAdapter";
	private List<DB_Travel> travels; 
    private Activity mContext; 

	public DB_ListAdapter(Activity context, List<DB_Travel> objects) throws Exception {
		super(context, R.layout.list_history_entry, objects);
		mContext = context;
		this.travels = objects;
		Log.d(LOG, "Travel history size: " + objects.size());	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		DB_Travel travel = getItem(position);    
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_history_entry, parent, false);
		}

		// Lookup view for data population
		TextView departureNameTextView = (TextView) convertView.findViewById(R.id.departureName);
		
		TextView destinationNameTextView = (TextView) convertView.findViewById(R.id.destinationName);
		departureNameTextView.setText(travel.departureName);
		destinationNameTextView.setTextColor(Color.BLACK);
		destinationNameTextView.setText(travel.destinationName);
		return convertView;

	}
	
	public void refresh(List<DB_Travel> list) {
		
		Log.i(LOG, "Refreshing the recent history list");
		notifyDataSetChanged();
		
	}

}
