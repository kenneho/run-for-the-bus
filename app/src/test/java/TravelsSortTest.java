package test.java;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import net.kenneho.runnow.customObjects.RealtimeTravel;
import net.kenneho.runnow.utils.TravlesSort;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.test.AndroidTestCase;

@RunWith(RobolectricTestRunner.class)
public class TravelsSortTest extends AndroidTestCase {
	private RealtimeTravel travel1, travel2;
	private TravlesSort sort; 
	
	@Before
	public void setup() throws ParseException {

		sort = new TravlesSort();
		
		Calendar calendarBefore = GregorianCalendar.getInstance();
		calendarBefore.set(2000, Calendar.JANUARY, 30);
		
		Date before = new Date();
		before = calendarBefore.getTime();

		Calendar calendarAfter = GregorianCalendar.getInstance();
		calendarAfter.set(2000, Calendar.JANUARY, 30);

		Date after = new Date();
		after = calendarAfter.getTime();
		
		travel1 = new RealtimeTravel();
		travel1.setRealtimeDepartureFullDate(before);
		
		travel2 = new RealtimeTravel();
		travel2.setRealtimeDepartureFullDate(after);
	}
	
	@Test
	public void todo() {
		assertTrue(false);

	}
}