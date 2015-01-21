package test.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.kenneho.runnow.RuterManager;
import net.kenneho.runnow.customObjects.RealtimeTravel;
import net.kenneho.runnow.customObjects.ScheduledTravel;
import net.kenneho.runnow.customObjects.Travel;
import net.kenneho.runnow.jsonDefinitions.JsonPlace;
import net.kenneho.runnow.jsonDefinitions.StopVisits;
import net.kenneho.runnow.jsonDefinitions.TravelResponse;
import net.kenneho.runnow.networking.HttpManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.emory.mathcs.backport.java.util.Collections;

import android.test.AndroidTestCase;

@RunWith(RobolectricTestRunner.class)
public class RuterManagerTest extends AndroidTestCase{
	private RuterManager ruterManager;

	private final String LOG = "RuterManagerTest";

	private JsonPlace[] mockedJsonPlaces;

	@Mock
	HttpManager httpManager;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		ruterManager = new RuterManager(httpManager);

	}

	private void createMockedPlacesData() {
		mockedJsonPlaces = new JsonPlace[2];

		JsonPlace place1 = new JsonPlace();
		place1.placeName = "Solbergliveien";
		place1.disctrict = "Oslo";
		place1.placeType = "Stop";
		mockedJsonPlaces[0] = place1;


		JsonPlace place2 = new JsonPlace();
		place2.placeName = "Solbergliveien";
		place2.disctrict = "Oslo";
		place2.placeType = "Street";
		mockedJsonPlaces[1] = place2;

	}

	@Test
	public void testGetPlaces() throws Exception {
		createMockedPlacesData();

		String departureName = "Solbergliveien";
		String urlString = "http://reisapi.ruter.no/Place/GetPlaces/" + departureName;
		Mockito.when(httpManager.makeRestCall(urlString, JsonPlace[].class)).thenReturn(mockedJsonPlaces);

		List<JsonPlace> places = ruterManager.getPlaces(departureName);
		//debugPrintPlaces(places);

		List<JsonPlace> expectedPlaces = new ArrayList<JsonPlace>();
		JsonPlace place1 = new JsonPlace();
		place1.placeName = departureName + ", Oslo";
		expectedPlaces.add(place1);
		expectedPlaces.add(place1); // We'll get two entries, on for the stop, and one for the street name

		assertTrue(comparePlacesListByPlaceName(expectedPlaces, places));

	}

	private TravelResponse createMockedTravelResponse(String jsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss");;
		Gson gson = gsonBuilder.create();

		return gson.fromJson(jsonString, TravelResponse.class);
	}

	private List<ScheduledTravel> getTravelsInBirkelundenArea() throws Exception {

		int areaDepartureID = 1000023708; // Birkelunden (område)
		int areaDestinationID = 1000027573; // Jernbanetorget (område)

		/*
		 * This json string contains three proposals:
		 *   1) One with two stages, where as one is a walking stage
		 *   2) One with a regular departure stage
		 *   3) One with only a walking stage 
		 * 
		 * Our ruterManager should thus return only one travel
		 * proposal - the one only a regular departure stage. 
		 * */
		String jsonString = "{ \"TravelProposals\": [ { \"DepartureTime\": \"2014-12-13T12:27:00\", \"Stages\": [ { \"ArrivalStop\": { \"ID\": 3010062, \"Name\": \"Kirkeristen (i Storgata)\", \"District\": \"Oslo\", \"PlaceType\": \"Stop\" }, \"ArrivalTime\": \"2014-12-13T12:35:00\", \"DepartureStop\": { \"ID\": 3010520, \"Name\": \"Birkelunden [trikk]\", \"District\": \"Oslo\", \"PlaceType\": \"Stop\" }, \"DepartureTime\": \"2014-12-13T12:27:00\", \"Destination\": \"Majorstuen\", \"LineID\": 12, \"LineName\": 12, \"Transportation\": 7 }, { \"ArrivalPoint\":{ \"X\":598365, \"Y\":6644609 }, \"ArrivalTime\":\"2014-12-13T14:54:00\", \"DeparturePoint\":{ \"X\":598335, \"Y\":6644742 }, \"DepartureTime\":\"2014-12-13T14:50:00\", \"Transportation\":0, \"WalkingTime\":\"00:04:00\" } ] }, { \"DepartureTime\": \"2014-12-13T12:27:00\", \"Stages\": [ { \"ArrivalStop\": { \"ID\": 3010062, \"Name\": \"Kirkeristen (i Storgata)\", \"District\": \"Oslo\", \"PlaceType\": \"Stop\" }, \"ArrivalTime\": \"2014-12-13T12:35:00\", \"DepartureStop\": { \"ID\": 3010520, \"Name\": \"Birkelunden [trikk]\", \"District\": \"Oslo\", \"PlaceType\": \"Stop\" }, \"DepartureTime\": \"2014-12-13T12:27:00\", \"Destination\": \"Majorstuen\", \"LineID\": 12, \"LineName\": 12, \"Transportation\": 7 } ] } ], \"ReisError\": null }";
		TravelResponse tr = createMockedTravelResponse(jsonString);

		String urlString = "http://reisapi.ruter.no/Travel/GetTravels?fromplace=" + areaDepartureID + "&toplace=" + areaDestinationID + "&isafter=false&changepunish=199&maxwalkingminutes=0&changemargin=99";
		Mockito.when(httpManager.makeRestCall(urlString, TravelResponse.class)).thenReturn(tr);

		return ruterManager.getScheduledDepartures(areaDepartureID, areaDestinationID);
	}


	@Test
	public void testGetScheduledDepartures() throws Exception {
		/* An area consist of one or more stations. 
		 * When querying Ruter for areas, we'll get in response travels from
		 * concrete stations to stations. 
		 * */
		String expectedDepartureID = "3010520"; // Brikelunden [trikk]
		String expectedDestinationID = "3010062"; // Kirkeristen (i Storgata)

		List<ScheduledTravel> list = getTravelsInBirkelundenArea();

		assertTrue(list.size() == 1);

		ScheduledTravel t = list.get(0);
		assertEquals("12", t.getLineName());
		assertEquals(expectedDepartureID, t.getDepartureStationID());
		assertEquals(expectedDestinationID, t.getArrivalStationID());

	}



	@Test
	public void testGetTravelsFromAreaToAreaMustWork() throws Exception {
		int areaDepartureID = 1000023708; // Birkelunden (område)
		int areaDestinationID = 1000027573; // Jernbanetorget (område)

	}

	private Object getJsonDataFromFile(String filename, Class<?> myclass) throws IOException {

		/*
		 * TODO: Use getResources instead of absolute file system path
		 * */
		filename = "/home/kenneho/workspace/ws04/RunNow/tests/resources/" + filename; 
		File f = new File(filename);
		if (!f.exists()) {
			throw new IOException("Could not find file " + filename);
		}

		GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss");;
		Gson gson = gsonBuilder.create();

		BufferedReader ff = new BufferedReader(new FileReader(filename));  
		
		return gson.fromJson(ff, myclass);
	}
	
	@Test
	public void testGetTravelsFromStationToAreaMustWork() throws Exception {
		int departureStationID = 3010065; // Brugata
		int destinationAreaID = 1000023708; // Birkelunden

		String fileStopVisits = "stopVisitBrugata.json";
		StopVisits[] mockedStopVisits = (StopVisits[]) getJsonDataFromFile(fileStopVisits, StopVisits[].class);
		//debugPrintStopVisists(Arrays.asList(mockedStopVisits));
		String urlString = "http://reisapi.ruter.no/StopVisit/GetDepartures/" + departureStationID;
		Mockito.when(httpManager.makeRestCall(urlString, StopVisits[].class)).thenReturn(mockedStopVisits);

		String fileTravelResponse = "travelResponseStationToArea.json";
		TravelResponse mockedTravelResponse = (TravelResponse) getJsonDataFromFile(fileTravelResponse, TravelResponse.class);
		urlString = "http://reisapi.ruter.no/Travel/GetTravels?fromplace=" + departureStationID + "&toplace=" + destinationAreaID + "&isafter=false&changepunish=199&maxwalkingminutes=0&changemargin=99";
		Mockito.when(httpManager.makeRestCall(urlString, TravelResponse.class)).thenReturn(mockedTravelResponse);
		
		List<RealtimeTravel> list = ruterManager.getTravels(departureStationID, destinationAreaID);
		//debugPrintTravels(list);
		
		List<String> expectedLineName = new ArrayList<String>();
		expectedLineName.add("11"); 
		expectedLineName.add("12"); 
		expectedLineName.add("13");
		expectedLineName.add("30"); 
		int expectedListLength = 47;
		String expectedDepartureStationID = departureStationID + "";
		String expectedDepartureTimePrefix = "Tue Dec 16";
		List<String> expectedFinalDestinationNames = new ArrayList<String>();
		expectedFinalDestinationNames.add("Kjelsås"); 
		expectedFinalDestinationNames.add("Storo-Grefsen stasjon"); 
		expectedFinalDestinationNames.add("Disen"); 
		
		assertTrue(travelsDataSanityCheck(list, expectedListLength, expectedLineName, 
				expectedDepartureStationID, expectedDepartureTimePrefix, expectedFinalDestinationNames));
		
	}
	
	@Test
	public void testGetTravelsFromStationToStationMustWork() throws Exception {
		//int expectedDestinationID = 3010062; // Kirkeristen (i Storgata)

		int departureStationID = 3011335; // Solbergliveien
		int destinationStationID = 3011551;        // Trasop skole

		String fileStopVisits = "stopVisitSolbergliveien.json";
		StopVisits[] mockedStopVisits = (StopVisits[]) getJsonDataFromFile(fileStopVisits, StopVisits[].class);
		//debugPrintStopVisists(Arrays.asList(mockedStopVisits));
		String urlString = "http://reisapi.ruter.no/StopVisit/GetDepartures/" + departureStationID;
		Mockito.when(httpManager.makeRestCall(urlString, StopVisits[].class)).thenReturn(mockedStopVisits);

		String fileTravelResponse = "travelResponseStationToStation.json";
		TravelResponse mockedTravelResponse = (TravelResponse) getJsonDataFromFile(fileTravelResponse, TravelResponse.class);
		urlString = "http://reisapi.ruter.no/Travel/GetTravels?fromplace=" + departureStationID + "&toplace=" + destinationStationID + "&isafter=false&changepunish=199&maxwalkingminutes=0&changemargin=99";
		Mockito.when(httpManager.makeRestCall(urlString, TravelResponse.class)).thenReturn(mockedTravelResponse);
		
		List<RealtimeTravel> list = ruterManager.getTravels(departureStationID, destinationStationID);
		//debugPrintTravels(list);
		
		List<String> expectedLineName = new ArrayList<String>();
		expectedLineName.add("76"); 
		expectedLineName.add("79");
		int expectedListLength = 16;
		String expectedDepartureStationID = departureStationID + "";
		String expectedDepartureTimePrefix = "Sun Dec 14";
		List<String> expectedFinalDestinationNames = new ArrayList<String>();
		expectedFinalDestinationNames.add("Helsfyr T"); 
		expectedFinalDestinationNames.add("Grorud T"); 

		assertTrue(travelsDataSanityCheck(list, expectedListLength, expectedLineName, 
				expectedDepartureStationID, expectedDepartureTimePrefix, expectedFinalDestinationNames));
		
	}

	private boolean travelsDataSanityCheck(List<RealtimeTravel> list, int expectedSize, List<String> expectedLineName, 
			String expectedDepartureStationID, String expectedDepartureTimePrefix, 
			List<String> expectedFinalDestinationNames) {
		assertEquals(expectedSize, list.size());

		for (RealtimeTravel travel : list) {
			String lineName = travel.getLineName();
			assertTrue(expectedLineName.contains(lineName));
			
			assertEquals(expectedDepartureStationID, travel.getDepartureStationID());
	
			assertTrue(travel.getDepartureTime().toString().startsWith(expectedDepartureTimePrefix));
			
			String finalDestinationName = travel.getFinalDestinationName();
			assertTrue(expectedFinalDestinationNames.contains(finalDestinationName));

			assertTrue(travel.getRealtimeDepartureTime().toString().startsWith(expectedDepartureTimePrefix));
			
		}
		
		return true;
	}

	private List<String> getTravelLineNames(List<Travel> travels) {
		List<String> actualLineName = new ArrayList<String>();
		for (Travel travel : travels) {
			String lineName = travel.getLineName();
			if (!actualLineName.contains(lineName)) {
				actualLineName.add(lineName);
			}
		}
		Collections.sort(actualLineName);
		return actualLineName;
	}


	private void debugPrintPlaces(List<JsonPlace> places) {
		System.out.println("Printing " + places.size() + " places: ");
		for (JsonPlace place : places) {
			System.out.println(" - place: " + place.getPlaceName());
		}
	}

	private void debugPrintStopVisists(List<StopVisits> visists) {
		System.out.println("Printing " + visists.size() + " stop visits: ");
		for (StopVisits visit : visists) {
			System.out.println(" - Line Name: " + visit.getMonitoredVehicleJourney().getLineName() + "  " +
					visit.getMonitoredVehicleJourney().getMonitoredCall().expectedDepartureTime.toString() + "  " +
					visit.getMonitoredVehicleJourney().destinationName);
		}
	}


	private void debugPrintTravels(List<RealtimeTravel> travels) {
		System.out.println("Printing " + travels.size() + " travels: ");
		for (RealtimeTravel travel : travels) {
			System.out.println(" - line name: " + travel.getLineName());
			System.out.println(" - departure station id: " + travel.getDepartureStationID());
			System.out.println(" - departure time: " + travel.getDepartureTime());
			System.out.println(" - realtime departure: " + travel.getRealtimeDepartureTime());
			System.out.println(" - finel destination name: " + travel.getFinalDestinationName() + " (" + travel.getFinalDestinationID() + ")");

		}
	}

	private boolean comparePlacesListByPlaceName(List<JsonPlace> list1, List<JsonPlace> list2) {

		assertTrue(list1.size() == list2.size());
		for (int i = 0; i < list1.size(); i++) {
			String placeName1 = list1.get(i).placeName;
			String placeName2 = list2.get(i).placeName;
			if (!placeName1.equals(placeName2)) {
				return false;
			}
		}
		return true;
	}

}
