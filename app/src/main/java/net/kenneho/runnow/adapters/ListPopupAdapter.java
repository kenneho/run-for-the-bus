package net.kenneho.runnow.adapters;

import net.kenneho.runnow.R;
import net.kenneho.runnow.jsonDefinitions.JsonPlace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListPopupAdapter extends ArrayAdapter<JsonPlace> {
	public static final String LOG = "ListPopupAdapter";
	public String hepp = "h3pp";
	
	public ListPopupAdapter(Context context) {		
		super(context, R.layout.item_place);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		JsonPlace place = getItem(position);    
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_place, parent, false);
		}
		
		// Lookup view for data population
		TextView placeNameTextView = (TextView) convertView.findViewById(R.id.placeName);
		placeNameTextView.setText(place.getPlaceName());
		// Populate the data into the template view using the data object
		// Return the completed view to render on screen
		return convertView;
	}	
}
