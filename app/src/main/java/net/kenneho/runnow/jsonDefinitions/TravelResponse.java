package net.kenneho.runnow.jsonDefinitions;

import java.util.Date;
import com.google.gson.annotations.SerializedName;

public class TravelResponse {
	private static final String TAG = "TravelResponse";

	//ArrayList<TravelProposals> travelProposals = new ArrayList<TravelProposals>();
	@SerializedName("TravelProposals")
	TravelProposals[] travelProposals;

	public TravelResponse() {

	}
	public TravelProposals[] getTravelProposals() {
		return travelProposals;
	}

	public class TravelProposals {

		@SerializedName("DepartureTime")
		public Date departureTime;

		@SerializedName("TotalTravelTime")
		public String totalTravelTime;

		@SerializedName("Stages")
		public Stages[] stages;

		public Date getDepartureTime() {
			return departureTime;
		}

		public String getTotalTravelTime() {
			return totalTravelTime;
		}

		public Stages[] getStages() {
			return stages;
		}

		public class Stages {

			@SerializedName("ArrivalStop")
			public ArrivalStop arrivalStop;

			@SerializedName("DepartureStop")
			public DepartureStop departureStop;
			
			@SerializedName("ArrivalTime")
			public String arrivalTime;

			@SerializedName("ArrivalPoint")
			public ArrivalPoint arrivalPoint;
			
			@SerializedName("LineName")
			public String lineName;
			
			@SerializedName("DepartureTime")
			public Date departureTime;

			@SerializedName("Destination")
			public String destinationName;

			public String getDestinationName() {
				return destinationName;
			}

			public ArrivalStop getArrivalStop() {
				return arrivalStop;
			}

			public DepartureStop getDepartureStop() {
				return departureStop;
			}
			
			public String getArrivalTime() {
				return arrivalTime;
			}

			public String getLineName() {
				return lineName;
			}
			
			public Date getDepartureTime() {
				return this.departureTime;
			}

			public class ArrivalPoint {
				
			}
			
			public class ArrivalStop {

				@SerializedName("Lines")
				public Lines[] lines;

				@SerializedName("Name")
				public String name;

				@SerializedName("ID")
				public String id;

				public String getID() {
					return this.id;
				}
				
				public Lines[] getLines() {
					return lines;
				}
				public String getName() {
					return this.name;
				}


				public class Lines {

					@SerializedName("ID")
					public String id;

					public String getID() {
						return id;
					}
				}

			}
			
			public class DepartureStop {
				
				@SerializedName("ID")
				public String id;

				
				public String getID() {
					return id;
				}
			}


		}

	}

}


