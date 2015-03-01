package test.java;

import net.kenneho.runnow.InfoActivity;
import net.kenneho.runnow.MainActivity;
import net.kenneho.runnow.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLog;

import android.app.AlertDialog;
import android.content.Intent;
import android.test.AndroidTestCase;
import android.widget.ListView;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Config(manifest = "app/src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class InfoActivityTest extends AndroidTestCase {

	private InfoActivity infoActivity;
	private Intent triggerIntent, startedIntent;

	@Before
	public void setup() {

		System.out.println("##################################################");
		System.out.println("################# TEST ###########################");
		System.out.println("##################################################");

		ShadowLog.stream = System.out;

		//mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();

		triggerIntent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), InfoActivity.class);		
	}

    @Test
    public void testNetworkErrorMustWarnUser() throws Exception{
        int departureID = 3011335; // Solbergliveien;
        int destinationID = 3011554; // Trasoppveien;

        triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE, departureID);
        triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE, destinationID);
        triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE_NAME, "dummy");
        triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE_NAME, "dummy");

        System.out.println("000");

        String MOCKED_WORD = "test";
        MockWebServer mMockWebServer = new MockWebServer();
        mMockWebServer.enqueue(new MockResponse().setBody(MOCKED_WORD));
        mMockWebServer.play();

        //Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
        //Robolectric.getFakeHttpLayer().interceptResponseContent(false);

        //Robolectric.addPendingHttpResponse(500, "feil");
        //Robolectric.setDefaultHttpResponse(400, null);

        System.out.println("111");
        infoActivity = Robolectric.buildActivity(InfoActivity.class).withIntent(triggerIntent).create().get();
        System.out.println("222");
        infoActivity.setIntent(triggerIntent);
        System.out.println("333");

        ListView listview = (ListView) infoActivity.findViewById(android.R.id.list);

        assertTrue(listview.getCount() > 5);

    }

	@Test
	public void testFromStationToStationMustWork() {
		int departureID = 3011335; // Solbergliveien;
		int destinationID = 3011554; // Trasoppveien;

		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE, departureID);
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE, destinationID);
		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE_NAME, "dummy");
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE_NAME, "dummy");
		
		infoActivity = Robolectric.buildActivity(InfoActivity.class).withIntent(triggerIntent).create().get();		
		infoActivity.setIntent(triggerIntent);

		ListView listview = (ListView) infoActivity.findViewById(android.R.id.list);

		assertTrue(listview.getCount() > 5);
		
		// Populate the list with actual items. "setListAdapter"
	}

	@Test
	public void fromAreaToStationMustWork() {
		int departureID = 1000023708; // Birkelunden (område)
		int destinationID = 3010065; // Brugata

		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE, departureID);
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE, destinationID);
		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE_NAME, "dummy");
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE_NAME, "dummy");

		infoActivity = Robolectric.buildActivity(InfoActivity.class).withIntent(triggerIntent).create().get();
		infoActivity.setIntent(triggerIntent);

		ListView listview = (ListView) infoActivity.findViewById(android.R.id.list);
		assertTrue(listview.getCount() > 5);
		
		Robolectric.shadowOf(infoActivity.getListView()).populateItems();

	}

	@Test
	public void fromStationToAreaMustWork() {
		int departureID = 3011335; // Solbergliveien
		int destinationID = 1000021179; // Helsfyr (område)

		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE, departureID);
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE, destinationID);
		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE_NAME, "dummy");
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE_NAME, "dummy");

		infoActivity = Robolectric.buildActivity(InfoActivity.class).withIntent(triggerIntent).create().get();
		infoActivity.setIntent(triggerIntent);

		ListView listview = (ListView) infoActivity.findViewById(android.R.id.list);
		assertTrue(listview.getCount() > 5);
	}


	@Test
	public void fromAreaToAreaMustWork() {
		int departureID = 1000027573; // Jernbanetorget (område)
		int destinationID = 1000022429; // Nationaltheatret (område)

		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE, departureID);
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE, destinationID);
		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE_NAME, "dummy");
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE_NAME, "dummy");

		infoActivity = Robolectric.buildActivity(InfoActivity.class).withIntent(triggerIntent).create().get();
		infoActivity.setIntent(triggerIntent);

		ListView listview = (ListView) infoActivity.findViewById(android.R.id.list);
		assertTrue(listview.getCount() > 5);
	}

	@Test
	public void travelsWithWalkingStageMustFail() {
		int departureID = 1000027573; // Jernbanetorget (område)
		int destinationID = 3011335; // Solbergliveien

		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE, departureID);
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE, destinationID);
		triggerIntent.putExtra(MainActivity.DEPARURE_MESSAGE_NAME, "dummy");
		triggerIntent.putExtra(MainActivity.DESTINATION_MESSAGE_NAME, "dummy");

		infoActivity = Robolectric.buildActivity(InfoActivity.class).withIntent(triggerIntent).create().get();
		infoActivity.setIntent(triggerIntent);

		ListView listview = (ListView) infoActivity.findViewById(android.R.id.list);
		assertTrue(listview.getCount() == 0);

		AlertDialog alert =	ShadowAlertDialog.getLatestAlertDialog();
		
		// The ShadowAlertDialog gives us access to more info about the AlertDialog
		ShadowAlertDialog sAlert = Robolectric.shadowOf(alert);
		assertEquals(infoActivity.getString(R.string.no_travel_found), sAlert.getMessage().toString());

	}

}
