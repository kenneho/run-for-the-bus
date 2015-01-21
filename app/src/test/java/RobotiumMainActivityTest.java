package test.java;

import net.kenneho.runnow.MainActivity;
import net.kenneho.runnow.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import android.test.AndroidTestCase;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.test.ActivityInstrumentationTestCase2;
import android.app.Activity;
import com.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;


public class RobotiumMainActivityTest extends  ActivityInstrumentationTestCase2<MainActivity> {

	
	private Solo solo;

	  public RobotiumMainActivityTest() {
	    super(MainActivity.class);
	  }

	  public void setUp() throws Exception {
	    solo = new Solo(getInstrumentation(), getActivity());
	  }

	  @Override
	  public void tearDown() throws Exception {
	    solo.finishOpenedActivities();
	  }
	 
	  public void test() {
		  // TODO
	  }
}

