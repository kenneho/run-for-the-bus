package net.kenneho.runnow.adapters;

import java.util.Date;
import java.util.List;

import net.kenneho.runnow.R;
import net.kenneho.runnow.customObjects.RealtimeTravel;
import net.kenneho.runnow.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;



public class TravelsAdapter extends ArrayAdapter<RealtimeTravel> {
    private final static String LOG = "TravelsAdapter";
    private List<RealtimeTravel> travels;

    static class ViewHolder {
        TextView routeName;
        TextView destinationName;
        TextView expectedDeparture;
        TextView scheduledDeparture;
        Date removeTime;
        TextView countdown;
        LinearLayout layout;
        int backgroundColor;
        int position;
    }

    public TravelsAdapter(Context context, List<RealtimeTravel> objects) throws Exception {
        super(context, R.layout.item_ruterdata, objects);
        travels = objects;
        Log.d(LOG, "RuterDataAdapter size: " + objects.size());
    }

    @Override
    public int getCount() {
        return travels.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) throws IndexOutOfBoundsException {

        if (getCount() < 10) Log.d(LOG, "Fetching position " + position + " of our " + getCount() + " size list");

        /*
        * Workaround for some random IndexOutOfBoundsException
        * TODO: Fix root cause
        *
        * */
        if (getCount() == 0) {
            throw new IndexOutOfBoundsException("Tried to fetch an item for an empty list. Ignoring");
        }

        // Get the data item for this position
        RealtimeTravel travel = getItem(position);
        View rowView = convertView;

        // We'll be using a kind of a "cache" for our rows
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (rowView == null) {
            rowView = LayoutInflater.from(getContext()).inflate(R.layout.item_ruterdata, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.routeName = (TextView) rowView.findViewById(R.id.routeName);
            viewHolder.destinationName = (TextView) rowView.findViewById(R.id.destinationName);
            viewHolder.expectedDeparture = (TextView) rowView.findViewById(R.id.estimatedDepartureTime);
            //viewHolder.scheduledDeparture = (TextView) rowView.findViewById(R.id.scheduledDepartureTime);
            viewHolder.countdown = (TextView) rowView.findViewById(R.id.countdown);
            viewHolder.layout = (LinearLayout) rowView.findViewById(R.id.row);

            rowView.setTag(viewHolder);
        }
        else {

            viewHolder = (ViewHolder) rowView.getTag();

            // All fields are set for all rows, except the coundown field that is set only for the
            // next few departures. Because the textview is recycled, we need to clear the value.
            viewHolder.countdown.setText("");

        }

        setRowBackground(position, viewHolder);

        // Populate the data into the template view using the data object
        viewHolder.routeName.setTextColor(Color.BLACK);
        viewHolder.routeName.setText(travel.getLineName() + " ");
        viewHolder.destinationName.setTextColor(Color.BLACK);
        viewHolder.destinationName.setText(travel.getFinalDestinationName());
        viewHolder.expectedDeparture.setTextColor(Color.BLACK);
        viewHolder.countdown.setTextColor(Color.BLACK);
        //viewHolder.scheduledDeparture.setText(Utils.dateToString(travel.getScheduledDepartureTime()));

        int bufferInSeconds = 20;
        Date actualDepartureTime = travel.getRealtimeDepartureTime(); // TODO: Må dette inn i viewHolder?
        Date scheduledDepartureTime = travel.getScheduledDepartureTime();
        viewHolder.removeTime = Utils.addSecondsToDate(actualDepartureTime, bufferInSeconds);

        Date now = Utils.getTimestamp();
        fillExpectedField(viewHolder, actualDepartureTime, scheduledDepartureTime);

        long timeToDepartureInMillis = Utils.timeDifferenceMilliseconds(actualDepartureTime, now);

        // Only show the countdown for the next 3 departures
        if (position < 3) {
            String timeToDeparture = Utils.millisToStringConvert(timeToDepartureInMillis);
            viewHolder.countdown.setText(timeToDeparture);

        }
        // Return the completed view to render on screen
        return rowView;

    }

    private boolean pastRemoveTime(long timeToDepartureInMillis) {

        if (timeToDepartureInMillis <= 0) return true;
        else return false;

    }


    private void setRowBackground(int position, ViewHolder viewHolder) {
        if (position % 2 != 0) {
            viewHolder.backgroundColor = Color.LTGRAY;
        }
        else {
            viewHolder.backgroundColor = Color.WHITE;

        }
        viewHolder.layout.setBackgroundColor(viewHolder.backgroundColor);
    }

    private void fillExpectedField(ViewHolder viewHolder,
                                   Date actualDepartureTime, Date scheduledDepartureTime) {
        long diff = Utils.timeDifferenceMilliseconds(actualDepartureTime, scheduledDepartureTime);

        String timeToDeparture = Utils.millisToStringConvert(diff);

        String departureTimeString = Utils.extractTimeFromDate(actualDepartureTime);
        String diffString = "";
        String departureString = "Avgang: ";
        if (diff > 0) diffString = "+";
        if (diff < 0) diffString = "-";
        if (diff == 0) {
            viewHolder.expectedDeparture.setText(departureString + departureTimeString + " (i rute)");
        }
        else if (diff > 0){
            viewHolder.expectedDeparture.setText(departureString + departureTimeString + " (" + timeToDeparture + " forsinket)");
        }

        else if (diff < 0) {
            diff = Utils.timeDifferenceMilliseconds(scheduledDepartureTime, actualDepartureTime);
            timeToDeparture = Utils.millisToStringConvert(diff);

            viewHolder.expectedDeparture.setText(departureString + departureTimeString + " (" + timeToDeparture + " for tidlig ute)");
        }
    }

    public List<RealtimeTravel> getItems() {
        return travels;
    }
}
