package net.kenneho.runnow.adapters;

import java.util.ArrayList;

import net.kenneho.runnow.R;
import net.kenneho.runnow.customObjects.RuterData;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RuterDataAdapter extends ArrayAdapter<RuterData> {

	public RuterDataAdapter(Context context, ArrayList<RuterData> objects) throws Exception {
		super(context, R.layout.item_ruterdata, objects);
		
		if (objects.size() == 0) throw new Exception("The RuterData list is empty!");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		RuterData ruterData = getItem(position);    
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_ruterdata, parent, false);
		}
		
		if (position % 2 != 0) {
			convertView.setBackgroundColor(Color.LTGRAY);
		}
		
		// Lookup view for data population
		TextView destinationNameTextView = (TextView) convertView.findViewById(R.id.destinationName);
		TextView estimatedDepartureTimeView = (TextView) convertView.findViewById(R.id.estimatedDepartureTime);
		TextView routeNameView = (TextView) convertView.findViewById(R.id.routeName);
		
		if (ruterData.getRouteName() == null) {
			System.exit(1);
		}

		// Populate the data into the template view using the data object
		routeNameView.setTextColor(Color.BLACK);
		routeNameView.setText(ruterData.getRouteName() + " ");
		destinationNameTextView.setTextColor(Color.BLACK);
		destinationNameTextView.setText(ruterData.getDestinationName());
		estimatedDepartureTimeView.setTextColor(Color.BLACK);
		estimatedDepartureTimeView.setText("Expected departure: " + ruterData.getExpectedDepartureTime());

		return convertView;
	}

}
