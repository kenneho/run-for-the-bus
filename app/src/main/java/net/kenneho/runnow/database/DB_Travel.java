package net.kenneho.runnow.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.kenneho.runnow.utils.Utils;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "Travels")
public class DB_Travel extends Model { 
	private final static String LOG = "DB_Travel";
	private final String dateformat = "MM/dd/yyyy HH:mm:ss";
	
	@Column(name = "DepartureName")
	public String departureName;

	@Column(name = "DepartureID")
	public String departureID;

	@Column(name = "DestinationName")
	public String destinationName;

	@Column(name = "DestinationID")
	public String destinationID;

	@Column(name = "LastUsed")
	public Date timestamp; 
	
	public DB_Travel(){
		super();
	}
	
	public DB_Travel(String departureName, String departureID, String destinationName, String destinationID){
		super();
		this.departureName = departureName;
		this.departureID = departureID;
		this.destinationName = destinationName;
		this.destinationID = destinationID;
		
		
	}
	
	public void updateTimestamp() {
		Date now = Utils.getTimestamp();
		timestamp = now;
		
		DateFormat df = new SimpleDateFormat(dateformat);
		String dateString = df.format(timestamp);

		Log.d(LOG, "Updating timestampt for " + departureName + " => " + destinationName + " with data " + dateString);
		save();
	}
	
}
