package net.kenneho.runnow.customObjects;

import java.util.Date;
import android.util.Log;

public class RealtimeTravel extends Travel {
	private Date realtimeDepartureTime;
	private Date realtimeDepartureFullDate;
	private final String LOG = "RealtimeTravels";

	
	public void setRealtimeDepartureTime(Date time) {
		realtimeDepartureTime = time;
	}

	public Date getRealtimeDepartureTime() {
		return realtimeDepartureTime;
	}

	public void setRealtimeDepartureFullDate(Date expectedDepartureTime) {
		this.realtimeDepartureFullDate = expectedDepartureTime;
	}

    public Date getRealtimeDepartureFullDate() {
		return realtimeDepartureFullDate;
	}	
	
	public void prettyprint() {
		String s = "Line " + getLineName() + " - expected departure " + getRealtimeDepartureTime() + 
				" from departureID " + getDepartureStationID() + " - destination ";
		Log.d(LOG, s);
		//System.out.println(s);
	}

	
	
}
