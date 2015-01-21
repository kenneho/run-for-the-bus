package net.kenneho.runnow.jsonDefinitions;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class StopVisits {

	@SerializedName("MonitoredVehicleJourney") 
	private MonitoredVehicleJourney monitoredVehicleJourney;

	@SerializedName("LineRef") 
	public int lineID;

	@SerializedName("MonitoringRef")
	public String stationID;

	@SerializedName("RecordedAtTime")
	public String recordedAtTime;

	public StopVisits() {
		monitoredVehicleJourney = new MonitoredVehicleJourney();
	}

	public String getStationID() {
		return stationID;
	}

	public String getRecordedAtTime() {
		return recordedAtTime;
	}

	public MonitoredVehicleJourney getMonitoredVehicleJourney() {
		return monitoredVehicleJourney;
	}

	public static class MonitoredVehicleJourney {
		
		@SerializedName("MonitoredCall")
		public MonitoredCall monitoredCall;

		@SerializedName("FramedVehicleJourneyRef")
		public FramedVehicleJourneyRef framedVehicleJourneyRef;
		
		@SerializedName("DestinationName")
		public String destinationName;

		@SerializedName("Delay") 
		public String delay;

		@SerializedName("DestinationRef")
		public String destinationStopID;
		
		@SerializedName("VehicleMode")
		public int transportationType;

		@SerializedName("LineRef")
		public String routeName;
		
		
		@SerializedName("PublishedLineName")
		public String lineName;
		
		public MonitoredVehicleJourney() {
			monitoredCall = new MonitoredCall();
			framedVehicleJourneyRef = new FramedVehicleJourneyRef();
		}

		public String getDestinationName() {
			return destinationName;
		}

		public String getLineName() {
			return lineName;
		}

		
		public String getDestinationStopID() {
			return destinationStopID;
		}

		public int getTransportationType() {
			return transportationType;
		}
		public String getDelay() {
			return delay;
		}

		public String getRouteName() {
			return routeName;
		}

		public MonitoredCall getMonitoredCall() {
			return monitoredCall;
		}
		
		public FramedVehicleJourneyRef getFramedVehicleJourneyRef() {
			return framedVehicleJourneyRef;
		}
	}

	// TODO: To be removed?
	public static class FramedVehicleJourneyRef {

		
	}

	public static class MonitoredCall {

		@SerializedName("ExpectedDepartureTime")
		public Date expectedDepartureTime;

		@SerializedName("AimedDepartureTime")
		public Date aimedDepartureTime;

		//public MonitoredCall() {}
		
		public Date getExpectedDepartureTime() {
			return expectedDepartureTime;
		}

		public Date getAimedDepartureTime() {
			return aimedDepartureTime;
		}

	}


}
