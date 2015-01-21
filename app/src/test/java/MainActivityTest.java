package test.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.kenneho.runnow.DelayAutoCompleteTextView;
import net.kenneho.runnow.MainActivity;
import net.kenneho.runnow.R;
import net.kenneho.runnow.RuterManager;
import net.kenneho.runnow.adapters.PlacesAutoCompleteAdapter;
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
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.emory.mathcs.backport.java.util.Collections;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.test.AndroidTestCase;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest extends AndroidTestCase {
	Activity activity;

	@Before
	public void setup() {
		activity = Robolectric.buildActivity(MainActivity.class).create().get();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	@Test
	public void todo() {

		DelayAutoCompleteTextView textview_departure = (DelayAutoCompleteTextView) activity.findViewById(R.id.text_departure); 

		textview_departure.getOnItemClickListener().onItemClick(null, textview_departure, 0, 0);
		textview_departure.performClick();
		textview_departure.callOnClick();
		
		//textview_departure.performItemClick(textview_departure.getAdapter().getView(0, null, null), 0, textview_departure.getAdapter().getItemId(0));

	}

}