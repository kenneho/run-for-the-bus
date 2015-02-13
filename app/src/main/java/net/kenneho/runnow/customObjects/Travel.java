package net.kenneho.runnow.customObjects;

import java.util.Date;

public class Travel {
	protected Date scheduledDepartureTime;
	protected String lineName;
	protected String departureStationID;
	protected int finalDestinationStationID;
	private String finalDestinationStationName;
	
	public Travel() {
	}

	public void setLineName(String name) {
		lineName = name;
	}

	public void setDepartureStationID(String ID) {
		this.departureStationID = ID;
	}

	public void setFinalDestinationStationID(int id) {
		this.finalDestinationStationID = id;
	}

	public void setScheduledDepartureTime(Date time) {
		scheduledDepartureTime = time;
	}
	
	public Date getScheduledDepartureTime() {
		return scheduledDepartureTime;
	}

	public String getLineName() {
		return lineName;
	}
	
	public String getDepartureStationID() {
		return departureStationID;
	}
	
	public String getFinalDestinationName() {
		return finalDestinationStationName;
	}

	public void setFinalDestinationName(String destinationName) {
		this.finalDestinationStationName = destinationName;
	}

	public int getFinalDestinationID() {
		return finalDestinationStationID;
	}

}
