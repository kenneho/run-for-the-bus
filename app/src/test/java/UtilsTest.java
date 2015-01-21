package test.java;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.kenneho.runnow.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.test.AndroidTestCase;

@RunWith(RobolectricTestRunner.class)
public class UtilsTest extends AndroidTestCase {
	private Date date1, date2;
	
	@Before
	public void setup() throws ParseException {
		String stringDate1 = "Sun Nov 16 13:00:00 CET 2014";
		String stringDate2 = "Sun Nov 16 13:02:30 CET 2014";

		date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(stringDate1);
		date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(stringDate2);

	}
	
	@Test
	public void testDateDiff() throws ParseException {
		
		long diffInSeconds = Utils.timeDifferenceSeconds(date2, date1);
		assert(diffInSeconds == 150);
		
		long diffInMillis = Utils.timeDifferenceMilliseconds(date2, date1);
		assert(diffInMillis == 150000);
		
	}
	
	@Test
	public void testMillisToTimeConversion() {
		long diffInMillis = Utils.timeDifferenceMilliseconds(date2, date1);

		String s = Utils.millisToStringConvert(diffInMillis);

		assertEquals("2 min, 30 sek", s);
	}
}
