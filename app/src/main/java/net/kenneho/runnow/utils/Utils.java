package net.kenneho.runnow.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import net.kenneho.runnow.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;

@SuppressLint("DefaultLocale")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Utils {
	public static final String LOG = "Utils";

	public static Date parseDate(String inputDate) throws ParseException {
		Date date = null;
		SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

		/* The inputDate from Ruter API comes in different formats. Example:
		 		2014-09-14T16:36:31.312+02:00
		 		2014-09-14T17:05:24+02:00
		  We'll need to parse using all patterns
		 */
		String isoFormatBasic = "yyyy-MM-dd'T'HH:mm:ss";
		String isoFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		String isoFormatIncludingFractionalSeconds = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

		SimpleDateFormat parseFormat = new SimpleDateFormat(isoFormatBasic, Locale.getDefault());

		try {
			date = parseFormat.parse(inputDate);
		}
		catch (ParseException e) {
			Log.d(LOG, "Failed to parse string " + inputDate + ". Trying backup iso format...");
			parseFormat = new SimpleDateFormat(isoFormatIncludingFractionalSeconds, Locale.getDefault());

			try {
				date = parseFormat.parse(inputDate);
			}
			catch (ParseException e1) {
				Log.d(LOG, "Failed to parse string " + inputDate + ". Trying another backup iso format...");
				parseFormat = new SimpleDateFormat(isoFormat, Locale.getDefault());
				date = parseFormat.parse(inputDate);

			}
		}

		return date;

	}

	public static long timeDifferenceMinutes(Date timeA, Date timeB) {
		long diff = timeA.getTime() - timeB.getTime();

		long diffMinutes = diff / (60 * 1000) % 60;
		return diffMinutes;

	}

	public static long timeDifferenceMilliseconds(Date timeA, Date timeB) {
		long diff = timeA.getTime() - timeB.getTime();

		return diff;

	}

	
	public static long timeDifferenceSeconds(Date timeA, Date timeB) {
		long diffMilliSeconds = timeA.getTime() - timeB.getTime();
		long diffSeconds = diffMilliSeconds / 1000;
		
		return diffSeconds;

	}
	
	public static String millisToStringConvert(long millis) {
		return String.format(Locale.getDefault(), "%d min, %d sek", 
			    TimeUnit.MILLISECONDS.toMinutes(millis),
			    TimeUnit.MILLISECONDS.toSeconds(millis) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
			);
	}

	public static String extractTimeFromDate(Date date) {

		SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm:ss");
		return printFormat.format(date); // prints 09:30:51

	}

	public static String getCurrentTimeString() {

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

	}

	public static Date getTimestamp() {

		java.util.Date date= new Date();
		return date;
	}

	public static long millisToSeconds(long millis) {
		return millis / 1000;
	}
	
	public static long secondsToMillis(int seconds) {
		return seconds * 1000;
	}

    public void setDateFromString(String date) throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
		sf.setLenient(true);
	}

	public static String dateToString(Date date) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		// Get the date today using Calendar object.
		Date today = Calendar.getInstance().getTime();        

			// Using DateFormat format method we can create a string
		// representation of a date with the defined format.
		String reportDate = df.format(today);
		return reportDate;

	}

	public static void warnUser(Context context, String title, String warning) {

		//AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage(warning)
		.setTitle(title);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button

			}
		});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();

		dialog.show();
	}

	public static Date getFutureDate(int seconds) throws ParseException {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, seconds);
		// 24 hours format
		return now.getTime();
	}
	
	public static Date addSecondsToDate(Date date, int seconds) {
		long dateInMillis = date.getTime();
		long addMillis = secondsToMillis(seconds);
		
		long newDateInMillis = dateInMillis + addMillis;
		return new Date(newDateInMillis);
		
	}
}
