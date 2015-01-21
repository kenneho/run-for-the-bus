package test.java;

import net.kenneho.runnow.RuterManager;
import net.kenneho.runnow.adapters.PlacesAutoCompleteAdapter;
import net.kenneho.runnow.jsonDefinitions.JsonPlace;
import net.kenneho.runnow.networking.HttpManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.test.AndroidTestCase;
import android.widget.ProgressBar;

@RunWith(RobolectricTestRunner.class)
public class PlacesAutoCompleteAdapterTest extends AndroidTestCase {
	private Context context;
	private PlacesAutoCompleteAdapter adapter;
	private ProgressBar progressBar;

	@Mock
	HttpManager httpManager;

	@Before
	public void setup() {

		System.out.println("##################################################");
		System.out.println("################# TEST ###########################");
		System.out.println("##################################################");

		context = Robolectric.getShadowApplication().getApplicationContext();
		progressBar = new ProgressBar(context);
		RuterManager ruterManager = new RuterManager(httpManager);
		adapter = new PlacesAutoCompleteAdapter(context, ruterManager, progressBar);

	}

	@Test
	public void uniqueStationNameMustWork() {
		adapter.getFilter().filter("solbergliveien");
		printResults();
		assertEquals(1, adapter.getCount());	
	}

	@Test
	public void stationWithSpaceInNameMustWork() {
		adapter.getFilter().filter("ski stasjon");
		printResults();
		assertEquals(1, adapter.getCount());	
		
		adapter.getFilter().filter("st hanshaugen");
		printResults();
		assertEquals(2, adapter.getCount());	

	}

	@Test
	public void stationWithDotInNameMustWork() {
		adapter.getFilter().filter("st. hanshaugen");
		printResults();
		assertEquals(2, adapter.getCount());	
	}

	private void printResults() {
		for (int index = 0; index < adapter.getCount(); index++) {
			JsonPlace place = adapter.getItem(index);
			System.out.println(place.getPlaceName());
		}

	}
}
