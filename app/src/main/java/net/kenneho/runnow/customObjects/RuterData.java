package net.kenneho.runnow.customObjects;

import android.os.Parcel;
import android.os.Parcelable;

public class RuterData implements Parcelable {
	private int transportationType;
	private String destinationName, aimedDepartureTime;
	private String expectedDepartureTime;
	private int stationID = -1;
	private String routeName;

	public void setTransportationType(int type) {
		transportationType = type;
	}

	public void setDestinationName(String name) {
		destinationName = name;
	}

	public void setStationID(int id) {
		stationID = id;
	}

	public void setExpectedDepartureTime(String expectedDepartureTime) {
		this.expectedDepartureTime = expectedDepartureTime;

	}
	public void setAimedDepartureTime(String aimedDepartureTime) {
		this.aimedDepartureTime = aimedDepartureTime;
	}
	
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public int getTransportationType() { return transportationType; }

	public int getStationID() { return stationID; }

	public String getDestinationName() { return destinationName; }

	public String getAimedDepartureTime() { return aimedDepartureTime; }

	public String getExpectedDepartureTime() { return expectedDepartureTime; }

	public String getRouteName() { return routeName; }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	// Flatten the object out for serialization
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		// TODO Auto-generated method stub
		out.writeInt(transportationType);
		out.writeInt(stationID);
		out.writeString(destinationName);
		out.writeString(aimedDepartureTime);
		out.writeString(expectedDepartureTime.toString());
		out.writeString(routeName);
	
	}

	public static final Parcelable.Creator<RuterData> CREATOR
	= new Parcelable.Creator<RuterData>() {
		public RuterData createFromParcel(Parcel in) {
			return new RuterData(in);
		}

		public RuterData[] newArray(int size) {
			return new RuterData[size];
		}
	};

	// Assemble the object 
	private RuterData(Parcel in) {
		transportationType = in.readInt();
		stationID = in.readInt();
		destinationName = in.readString();
		aimedDepartureTime = in.readString();
		expectedDepartureTime = in.readString();
		routeName = in.readString();
	}


}
