package test.java;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.kenneho.runnow.R;
import net.kenneho.runnow.adapters.TravelsAdapter;
import net.kenneho.runnow.customObjects.RealtimeTravel;
import net.kenneho.runnow.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

@RunWith(RobolectricTestRunner.class)
public class TravelsAdapterTest extends AndroidTestCase {

	private RealtimeTravel onTimeArrival, earlyArrival, lateArrival, pastArrival;
	private final String LOG_PREFIX = "InfoTest";
	private TravelsAdapter adapter;

	@Before
	public void setup() throws Exception {

		System.out.println("##################################################");
		System.out.println("################# TEST ###########################");
		System.out.println("##################################################");

		ShadowLog.stream = System.out;

		//mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
		ArrayList<RealtimeTravel> data = new ArrayList<RealtimeTravel>();

		Date now = Utils.getTimestamp();

		pastArrival = new RealtimeTravel();
		pastArrival.setLineName("76");
		pastArrival.setFinalDestinationName("Helsfyr");
		pastArrival.setRealtimeDepartureTime(getPastDate(10)); // 10 secondes before present time
		pastArrival.setScheduledDepartureTime(now);

		earlyArrival = new RealtimeTravel();
		earlyArrival.setLineName("76");
		earlyArrival.setFinalDestinationName("Helsfyr");
		earlyArrival.setRealtimeDepartureTime(now);
		earlyArrival.setScheduledDepartureTime(Utils.getFutureDate(65));

		lateArrival = new RealtimeTravel();
		lateArrival.setLineName("21");
		lateArrival.setFinalDestinationName("Helsfyr");
		lateArrival.setRealtimeDepartureTime(Utils.getFutureDate(90));
		lateArrival.setScheduledDepartureTime(now);

		onTimeArrival = new RealtimeTravel();
		onTimeArrival.setLineName("21");
		onTimeArrival.setFinalDestinationName("Helsfyr");
		onTimeArrival.setRealtimeDepartureTime(now);
		onTimeArrival.setScheduledDepartureTime(now);

		data.add(pastArrival);
		data.add(earlyArrival);
		data.add(lateArrival);
		data.add(onTimeArrival);

		adapter = new TravelsAdapter(Robolectric.getShadowApplication().getApplicationContext(), data);

	}

	@Test
	public void earlyDeparturesMustShowCorrectText() throws Exception {
		Context context = Mockito.mock(Context.class);

		printStuff(earlyArrival.getRealtimeDepartureTime(), earlyArrival.getDepartureTime());
		
		assertTrue(earlyArrival.getRealtimeDepartureTime().before(earlyArrival.getDepartureTime()));
		
		String expectedString = "Avgang: " + Utils.extractTimeFromDate(earlyArrival.getRealtimeDepartureTime()) 
				+ " (1 min, 5 sek for tidlig ute)";
		LinearLayout row = (LinearLayout) adapter.getView(1, null, null);
		TextView textviewEstimatedDepartureTime = (TextView) row.findViewById(R.id.estimatedDepartureTime);
		assertEquals(expectedString, textviewEstimatedDepartureTime.getText());

	}
	
	@Test
	public void lateDeparturesMustShowCorrectText() throws Exception {
		
		printStuff(lateArrival.getRealtimeDepartureTime(), lateArrival.getDepartureTime());

		assertTrue(lateArrival.getRealtimeDepartureTime().after(lateArrival.getDepartureTime()));
		
		String expectedString = "Avgang: " + Utils.extractTimeFromDate(lateArrival.getRealtimeDepartureTime()) 
				+ " (1 min, 30 sek forsinket)";
		LinearLayout row = (LinearLayout) adapter.getView(2, null, null);
		TextView textviewEstimatedDepartureTime = (TextView) row.findViewById(R.id.estimatedDepartureTime);

		assertEquals(expectedString, textviewEstimatedDepartureTime.getText());
	
	}

	@Test
	public void onTimeDeparturesMustShowCorrectText() throws Exception {
		
		printStuff(onTimeArrival.getRealtimeDepartureTime(), onTimeArrival.getDepartureTime());

		assertTrue(onTimeArrival.getRealtimeDepartureTime().equals(onTimeArrival.getDepartureTime()));
		
		String expectedString = "Avgang: " + Utils.extractTimeFromDate(onTimeArrival.getRealtimeDepartureTime()) 
				+ " (i rute)";
		LinearLayout row = (LinearLayout) adapter.getView(3, null, null);
		TextView textviewEstimatedDepartureTime = (TextView) row.findViewById(R.id.estimatedDepartureTime);
		System.out.println(textviewEstimatedDepartureTime.getText());
		assertEquals(expectedString, textviewEstimatedDepartureTime.getText());
	
	}

	@Test
	public void rowMustBeRemovedAfterBufferTimeExpires() throws ParseException {
		
		int initialSize = adapter.getCount();
		pastArrival.setRealtimeDepartureTime(getPastDate(30)); // Buffer is 20 seconds, so the entry should now be removed from the adapter

		printStuff(pastArrival.getRealtimeDepartureTime(), pastArrival.getDepartureTime());

		adapter.getView(0, null, null);
		
		int afterSize = adapter.getCount();
		assertTrue(afterSize+1 == initialSize);
		
	}
	
	@Test
	public void coundownMustShowCorrectValues() {
		printStuff(pastArrival.getRealtimeDepartureTime(), pastArrival.getDepartureTime());
		
		LinearLayout row = (LinearLayout) adapter.getView(0, null, null);
		TextView textviewCountdown = (TextView) row.findViewById(R.id.countdown);
		System.out.println("Countdown: " + textviewCountdown.getText());
		
		String expectedString = "0 min, -10 sek";
		assertEquals(expectedString, textviewCountdown.getText());
	}
	
	private void printStuff(Date actual, Date scheduled) {
		Log.d(LOG_PREFIX, "Actual departure time: " + actual.toString() + ", scheduled departure time is " + scheduled.toString());
	}

	private Date getDateFromString(String string) throws ParseException {
		DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
		Date date = format.parse(string);
		return date;
	}
	
	public static Date getPastDate(int seconds) throws ParseException {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, -seconds);
		return now.getTime();
	}

}