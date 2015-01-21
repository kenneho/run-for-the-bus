package net.kenneho.runnow.jsonDefinitions;
import android.util.Log;

import com.google.gson.annotations.SerializedName;


public class JsonPlace {
	private static final String LOG = "JsonPlace";

	@SerializedName("PlaceType")
	public String placeType;

	@SerializedName("ID")
	public int id;

	@SerializedName("District")
	public String disctrict;

	@SerializedName("Stops")
	public Stops[] stops;

	@SerializedName("Name")
	public String placeName;

	public void prettyPrint() {
		Log.d(LOG, "Name: " + placeName + ", type: " + placeType + ", district: " + disctrict);

//		if (stops != null) {
//			for (Stops s : stops) {
//				s.prettyPrint();
//			}
//		}
	}

	public int getID() {
		return id;
	}

	public Stops[] getStops() {
		return stops;
	}

	public String getPlaceName() {
		return placeName;
	}

	public String getPlaceType() {
		return placeType;
	}

	public String getDistrict() {
		return disctrict;
	}

	public class Stops {

		private static final String LOG = "Stops";

		//		@SerializedName("Stop")
		//		public Stop[] stops;
		//		
		//		
		//		public class Stop {
		//
		@SerializedName("ID")
		public int id;

		@SerializedName("Name")
		public String name;

		public int getID() {
			return id;
		}

		public void prettyPrint() {
			Log.d(LOG, "Name: " + name + ", id: " + id);
		}

		public String getName() {
			return name;
		}
		//		}

	}

}

