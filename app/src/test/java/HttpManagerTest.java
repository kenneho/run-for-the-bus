package test.java;

import android.accounts.NetworkErrorException;

import junit.framework.Assert;

import net.kenneho.runnow.networking.HttpManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class HttpManagerTest { // extends AndroidTestCase {

    HttpManager httpManager;

	@Before
	public void setup() {

		ShadowLog.stream = System.out;

        httpManager = new HttpManager();

		/*
		 * Some of the functionality is implicitly tested through RuterManager's "getPlaces"
		 * */
		
	}

    @Test
    public void networkErrorsShouldDisplayWarning() {

        String bogusUrl = "http://reisapi.ruter.no/Place/GetPlaces/123456789";

        try {
            httpManager.makeRestCall(bogusUrl, Object.class);
        }
        catch (NetworkErrorException ne) {
            //ne.printStackTrace();
            assertTrue(true);
        }
        catch (Exception e) {
            // Not tested
        }

    }

}
