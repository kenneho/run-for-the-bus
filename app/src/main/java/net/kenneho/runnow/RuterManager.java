package net.kenneho.runnow;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.kenneho.runnow.customObjects.RealtimeTravel;
import net.kenneho.runnow.customObjects.ScheduledTravel;
import net.kenneho.runnow.customObjects.Travel;
import net.kenneho.runnow.jsonDefinitions.JsonPlace;
import net.kenneho.runnow.jsonDefinitions.StopVisits;
import net.kenneho.runnow.jsonDefinitions.StopVisits.MonitoredCall;
import net.kenneho.runnow.jsonDefinitions.TravelResponse;
import net.kenneho.runnow.jsonDefinitions.StopVisits.MonitoredVehicleJourney;
import net.kenneho.runnow.jsonDefinitions.TravelResponse.TravelProposals;
import net.kenneho.runnow.jsonDefinitions.TravelResponse.TravelProposals.Stages;
import net.kenneho.runnow.networking.HttpManager;
import net.kenneho.runnow.utils.TravlesSort;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.accounts.NetworkErrorException;
import android.util.Log;

public class RuterManager  {
    private HttpManager data = new HttpManager();
    private static final String LOG = "RuterManager";
    private HttpManager httpManager;

    public RuterManager(HttpManager httpManager) {
        this.httpManager = httpManager;

    }

    public List<JsonPlace> getPlaces(String placeName) throws Exception, NetworkErrorException {

        JsonPlace[] response = null;

        /*
        * Let's encode the place name to get the call to work with norwegian characters
        * */
        placeName = placeName.trim();
        placeName = URLEncoder.encode(placeName, "utf-8");

        String urlString = "http://reisapi.ruter.no/Place/GetPlaces/" + placeName;

        try {
            response = (JsonPlace[]) httpManager.makeRestCall(urlString, JsonPlace[].class);
            if (response != null) printGson(response); // For debugging
        }
        catch (Exception e) {
            throw new NetworkErrorException("Could not read data from Ruter.");
        }

        List<JsonPlace> list = Arrays.asList(response);
        list = addCustomJsonPlaceData(list);

        return list;
    }

    /*
     * Based on a departure ID and destination ID, find all possible direct routes (i.e. travels),
     * including their real time data.
     * */
    public List<RealtimeTravel> getTravels(int departureID, int destinationStationID) throws IllegalArgumentException, Exception {
        Log.d(LOG, "Adding realtime data to Travel objects");

		/*
		 * Get all travels from our destination station/area to our destination station/area
		 * */
        List<ScheduledTravel> scheduledTravels = getScheduledDepartures(departureID, destinationStationID);

        if (scheduledTravels.size() == 0) {
            throw new IllegalArgumentException("No direct routes between " + departureID + " and " + destinationStationID + "found.");
        }

		/*
		 * Create a list of unique departure stations within the area
		 * */
        List<String> uniqueDepartureStationIDs = extractUniqueDepartureStationID(scheduledTravels);

		/*
		 * Get real time data from each of the departure stations
		 * */
        List<StopVisits> realtimeData = new ArrayList<StopVisits>();
        for (String stationID  : uniqueDepartureStationIDs) {
            System.out.println("Getting realtime data for station with ID " + stationID);

            Log.d(LOG, "Getting realtime data for station with ID " + stationID);
            for (StopVisits visit : getRealtimeDataByStationID(stationID)) {
				/* TODO: Drop the for loop, and use something like this instead:
				   realtimeData.add(getRealtimeDataByStationID(stationID))
				*/
                realtimeData.add(visit);
            }
        }

		/*
		 * Which StopVisit have a DestinationName we're interested in?
		 *   1) Extract all unique destination names from our schedules travels
		 *   2) Filter StopVisits that match the destination names. By looking up the final destination
		 *      destination names we'll know which travels are relevant. 
		 * */
        List<String> uniqueDestinationStationNames = getUniqueDestinationNames(scheduledTravels);
        List<StopVisits> relevantStopVisits = extractRelevantVisits(realtimeData, uniqueDestinationStationNames);

        List<RealtimeTravel> myTravels = createTravelsFromStopVisits(relevantStopVisits);

        Log.i(LOG, "Sorting the travels based on departure time.");
        Collections.sort(myTravels, new TravlesSort());

        return myTravels;
    }

    public List<ScheduledTravel> getScheduledDepartures(int departureId, int destinationId) throws NetworkErrorException, Exception {

        List<ScheduledTravel> travelList = new ArrayList<ScheduledTravel>();

		/*
		 * Let's tune the algorithm to punish travel proposals that include walking and changing 
		 * transportation interchange, so that we end up with only the alternatives that takes
		 * us directly to our destination
		 * */
        int changePunish = 199;
        int changeMargin = 99;

        String urlString = "http://reisapi.ruter.no/Travel/GetTravels?fromplace=" + departureId +
                "&toplace=" + destinationId + "&isafter=false&changepunish=" + changePunish + "&maxwalkingminutes=0&changemargin=" + changeMargin;

		/* Fetching all scheduled travels between our departure station/area and
		   our destination station/area. This way we'll know which lines (and towards which destination)
		   we'll be looking for when looking up realtime date for the departure station
		*/

        TravelResponse response = null;
        try {
            response = (TravelResponse) httpManager.makeRestCall(urlString, TravelResponse.class);
        }
        catch (Exception e) {
            throw new NetworkErrorException("Could not read data from Ruter.");
        }

        if (response == null) throw new Exception("The response from Ruter was a null object.");

        for (TravelProposals tp : response.getTravelProposals()) {
            if (tp.getStages().length > 1 || proposalContainsWalkingStage(tp))
                continue;

            travelList.add(extractTravel(tp));

        }

        return travelList;
    }

    private List<JsonPlace> addCustomJsonPlaceData(List<JsonPlace> list) {
        List<JsonPlace> newList = new ArrayList<JsonPlace>();
        for (JsonPlace place : list) {
            place.placeName += ", " + place.disctrict;
            newList.add(place);
        }
        return newList;
    }

    /* For development purposes */
    private void printGson(Object o) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Log.d(LOG, gson.toJson(o));
    }

    private boolean proposalContainsWalkingStage(TravelProposals tp) {
        for (Stages stage : tp.getStages()) {
            if (stage.getDepartureStop() == null) {
                Log.d(LOG, "Walking stage!");
                return true;
            }
        }
        return false;
    }

    private ScheduledTravel extractTravel(TravelProposals tp) {

        Stages s = tp.getStages()[0];

        ScheduledTravel travel = new ScheduledTravel();
        travel.setLineName(s.getLineName());

        travel.setScheduledDepartureTime(s.getDepartureTime());
        travel.setDepartureStationID(s.getDepartureStop().getID());
        travel.setArrivalStopID(s.getArrivalStop().getID());
        travel.setArrivalName(s.getArrivalStop().getName());
        travel.setFinalDestinationName(s.getDestinationName());
        Log.d(LOG, "Added this to travels: ");
        travel.prettyprint();
        return travel;
    }

    private List<StopVisits> getRealtimeDataByStationID(String stationID) throws Exception {

        String urlString = "http://reisapi.ruter.no/StopVisit/GetDepartures/" + stationID;
        StopVisits[] res = null;

        Log.v(LOG, "Reading URL " + urlString);

        try {
            res = (StopVisits[]) httpManager.makeRestCall(urlString, StopVisits[].class);
        }
        catch (Exception e) {
            throw new NetworkErrorException("Could not read data from Ruter.");
        }

        if (res == null) {
            throw new Exception("Res is null for station id " + stationID);
        }
        List<StopVisits> response = Arrays.asList(res);

        return response;
    }

    private List<RealtimeTravel> createTravelsFromStopVisits(List<StopVisits> relevantStopVisits) throws Exception {
        List<RealtimeTravel> travelList = new ArrayList<RealtimeTravel>();

        for (StopVisits visit : relevantStopVisits) {
            RealtimeTravel travel = new RealtimeTravel();

            MonitoredVehicleJourney vehicle = visit.getMonitoredVehicleJourney();
            MonitoredCall realtimeData = vehicle.getMonitoredCall();

            travel.setLineName(vehicle.getLineName());
            travel.setScheduledDepartureTime(realtimeData.getAimedDepartureTime());
            travel.setRealtimeDepartureTime(realtimeData.getExpectedDepartureTime());
            travel.setRealtimeDepartureFullDate(realtimeData.getExpectedDepartureTime()); // TODO: Remove this
            travel.setDepartureStationID(visit.getStationID());
            travel.setFinalDestinationName(vehicle.getDestinationName());
            travel.setExpirationTime();
            travelList.add(travel);
            travel.prettyprint();

        }
        return travelList;
    }

    private List<StopVisits> extractRelevantVisits(
            List<StopVisits> realtimeData,
            List<String> uniqueDestinationStationNames) {

        Log.d(LOG, "Filtering out the realtime data that is relevant for our travel...");
        Log.d(LOG, "Relevant destination name: " + uniqueDestinationStationNames.toString());
        List<StopVisits> list = new ArrayList<StopVisits>();

        for (StopVisits visit : realtimeData) {
            String sheduledDestinationName = visit.getMonitoredVehicleJourney().getDestinationName();

            if (uniqueDestinationStationNames.contains(sheduledDestinationName)) {
                list.add(visit);
            }
        }

        return list;
    }


    private List<String> getUniqueDestinationNames(List<ScheduledTravel> scheduledTravles) {
        List<String> list = new ArrayList<String>();

        for (Travel travel : scheduledTravles) {
            String destinationName = travel.getFinalDestinationName();
            //System.out.println(destinationName);
            if (!list.contains(destinationName)) {
                list.add(destinationName);
            }

        }

        Log.d(LOG, "De-duplicated realtime destination station names into " + list.toString());
        return list;
    }

    private List<String> extractUniqueDepartureStationID(List<ScheduledTravel> travels) {
        List<String> list = new ArrayList<String>();

        for (Travel travel : travels) {
            String departureID = travel.getDepartureStationID();
            if (!list.contains(departureID)) {
                list.add(departureID);
            }
        }

        return list;
    }
}
