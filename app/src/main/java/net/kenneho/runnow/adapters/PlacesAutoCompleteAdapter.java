package net.kenneho.runnow.adapters;

import java.util.ArrayList;
import java.util.List;
import net.kenneho.runnow.R;
import net.kenneho.runnow.RuterManager;
import net.kenneho.runnow.jsonDefinitions.JsonPlace;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<JsonPlace> implements Filterable {
	private RuterManager ruterManager;
	private final static String LOG = "PlacesAutoCompleteAdapter";
	private ArrayList<String> resultList; 
	private List<JsonPlace> places;
	private PlacesFilter placesFilter;
	private ProgressBar progressBar;

	public PlacesAutoCompleteAdapter(Context context, RuterManager ruterManager, ProgressBar progressBar) {
		super(context, R.layout.item_place);

		this.progressBar = progressBar;
		this.ruterManager = ruterManager;
		places = new ArrayList<JsonPlace>();

	}

	@Override
	public int getCount() {
		return places.size();
	}

	@Override
	public JsonPlace getItem(int index) {
		return places.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		int viewResourceId = R.layout.item_place;
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(viewResourceId, null);
		}
		String placeName = getItem(position).getPlaceName();
		if (placeName != null) {
			TextView placeNameView = (TextView) v.findViewById(R.id.placeName);
			if (placeNameView != null) {
				Log.d(LOG, "Setting label " + placeName);
				//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
				placeNameView.setText(placeName);
			}
		}
		return v;
	}

	@Override
	public Filter getFilter() {
		
		if (placesFilter == null)
			placesFilter = new PlacesFilter();

		return placesFilter;
	}

	public class PlacesFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence input_constraint) {

			FilterResults filterResults = new FilterResults();
			if (input_constraint != null) {
				String constraint = input_constraint.toString().trim().replaceAll(" ", "%20");
				constraint = constraint.replaceAll("\\.", ""); // The Ruter API doesn't handle periods very well
				Log.d(LOG, "Filtering this input: \""  + constraint.toString() + "\"");

				List<JsonPlace> updatedPlacesList = new ArrayList<JsonPlace>();
				
				try {
					updatedPlacesList = ruterManager.getPlaces(constraint.toString());
				} catch (Exception e) {
					Log.d(LOG, "Failed to lookup place \"" + constraint.toString() + "\". Most likely we're trying to lookup a complete place name, and everything is all right. Ignoring. ");
					return null; // Return something else here? 
				}
				updatedPlacesList = filterPlaces(updatedPlacesList, "Street"); // Skip object of type "Street"

				filterResults.values = updatedPlacesList;
				filterResults.count = updatedPlacesList.size();

			}

			return filterResults;

		}
		
		private List<JsonPlace> filterPlaces(List<JsonPlace> places,
				String string) {
			List<JsonPlace> list = new ArrayList<JsonPlace>();

			for (JsonPlace place : places) {
				if (place.getPlaceType().equals(string)) continue;
				list.add(place);
			}

			return list;
		}


		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {

			if (results != null && results.count > 0) {
				Log.d(LOG, "publishResults: " + constraint.toString() + " having " + results.count + " number of items");

				places = (ArrayList<JsonPlace>) results.values;

				Log.d(LOG, "notifyDataSetChanged()");					
				notifyDataSetChanged();
			} else {
				Log.d(LOG, "notifyDataSetInvalidated()");
				notifyDataSetInvalidated();
			}
		}
	}
}