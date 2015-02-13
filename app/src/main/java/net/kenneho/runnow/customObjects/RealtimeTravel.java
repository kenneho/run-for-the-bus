package net.kenneho.runnow.customObjects;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RealtimeTravel extends Travel implements Parcelable {
	private Date realtimeDepartureTime;
	private Date realtimeDepartureFullDate;
	private final String LOG = "RealtimeTravels";

	public RealtimeTravel() {

    }

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

    private RealtimeTravel(Parcel in) {
        realtimeDepartureTime = (java.util.Date) in.readSerializable();
        realtimeDepartureFullDate = (java.util.Date) in.readSerializable();
        super.setDepartureStationID(in.readString());
        super.setFinalDestinationName(in.readString());
        super.setLineName(in.readString());
        super.setScheduledDepartureTime((java.util.Date) in.readSerializable());
        super.setFinalDestinationStationID(in.readInt());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeSerializable(realtimeDepartureTime);
        parcel.writeSerializable(realtimeDepartureFullDate);
        parcel.writeString(super.getDepartureStationID());
        parcel.writeString(super.getFinalDestinationName());
        parcel.writeString(super.getLineName());
        parcel.writeSerializable(super.getScheduledDepartureTime());
        parcel.writeInt(super.getFinalDestinationID());

    }

    public static final Parcelable.Creator<RealtimeTravel> CREATOR
            = new Parcelable.Creator<RealtimeTravel>() {
        public RealtimeTravel createFromParcel(Parcel in) {
            //return new TravelsAdapter(in);
            return new RealtimeTravel(in);
        }

        public RealtimeTravel[] newArray(int size) {
            return new RealtimeTravel[size];
        }
    };

}
