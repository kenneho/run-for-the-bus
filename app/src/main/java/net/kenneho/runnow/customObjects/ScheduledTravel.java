package net.kenneho.runnow.customObjects;

import android.util.Log;

public class ScheduledTravel extends Travel {
	private final String LOG = "ScheduledTravels";
	protected String arrivalID, arrivalName;

	public String getDestinationName() {
		return arrivalName;
	}

	public void setArrivalName(String name) {
		arrivalName = name;
	}

	public void setArrivalStopID(String ID) {
		this.arrivalID = ID;
	}

	public String getArrivalStationID() {
		return arrivalID;
	}
	
	public String getArrivalName() {
		return arrivalName;
	}
	
	
	public void prettyprint() {
		String s = "Line " + getLineName() + 
				" from departureID " + getDepartureStationID() + " - destination " + 
				getArrivalName() + "(" + arrivalID + " final destination " + getFinalDestinationName();
		Log.d(LOG, s);
		//System.out.println(s);
	}

	
}
