package net.kenneho.runnow.utils;

import java.util.Comparator;
import java.util.Date;

import net.kenneho.runnow.customObjects.RealtimeTravel;
import android.util.Log;

public class TravlesSort implements Comparator<RealtimeTravel>{
	private final String LOG = "TravelsSort";
	
	@Override
	public int compare(RealtimeTravel lhs, RealtimeTravel rhs) {

		Date date1 = lhs.getRealtimeDepartureFullDate();
		Date date2 = rhs.getRealtimeDepartureFullDate();

		if (date1 == null || date2 == null) {
			Log.e(LOG, "Error! Can't compare dates because one or both is NULL");
			return 0;
		}
		else {
			if (date1.after(date2)) {
				return 1; 
			}
			else if (date2.after(date1)) {
				return -1; 
			}
			else return 0;

		}
		
	}

}
