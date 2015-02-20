package net.kenneho.runnow;

import java.util.Date;
import java.util.List;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import net.kenneho.runnow.database.DB_Travel;
import net.kenneho.runnow.utils.Utils;

public class DatabaseManager {
	private static final String LOG = "DatabaseManager";
    private int limit = 5;

	public DatabaseManager() {

	}

	public void saveTravelEntry(String departureName, String departureID, String destinationName, String destinationID) {

		String current_time = Utils.getCurrentTimeString();
		Date timestamp = Utils.getTimestamp();
		DB_Travel travelDB = new DB_Travel();
		
		travelDB.departureName = departureName;
		travelDB.departureID = departureID;
		travelDB.destinationName = destinationName;
		travelDB.destinationID = destinationID;
		travelDB.timestamp = timestamp;
		Log.i(LOG, "Saved " + departureName + " => " + destinationName + " to the database. Timestamp: " );
		travelDB.save();
	}

	public boolean entryExists(String departureName, String destinationName) {

		/*DB_Travel travel = new Select()
		.from(DB_Travel.class)
		.where("departureName = ? AND destinationNAME = ?", departureName, destinationName)
		.executeSingle();*/
        DB_Travel travel = getEntryByName(departureName, destinationName);

		if (travel == null) {
			return false;
		}
		else return true;
	}

	public List<DB_Travel> getAll() {
		return new Select()
		.from(DB_Travel.class)
		.orderBy("LastUsed DESC")
		.limit(limit)
		.execute();
	}

	public void clearDatabase() {
		Log.i(LOG, "Clearing the history database");
		new Delete().from(DB_Travel.class).execute();
	}

    public void updateTimestamp(String departureName, String destinationName) {
        DB_Travel entry = getEntryByName(departureName, destinationName);
        Log.i(LOG, "Updating timestamp for travel " + departureName + " => " + destinationName);
        entry.updateTimestamp();
    }

    private DB_Travel getEntryByName(String departureName, String destinationName) {
        DB_Travel travel = new Select()
                .from(DB_Travel.class)
                .where("departureName = ? AND destinationNAME = ?", departureName, destinationName)
                .executeSingle();


        return travel;
    }
}
