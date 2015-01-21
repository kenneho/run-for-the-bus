package test.java;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

@RunWith(RobolectricTestRunner.class)
public class HttpManagerTest { // extends AndroidTestCase {

	@Before
	public void setup() {

		ShadowLog.stream = System.out;

		/*
		 * Implicitly tested through RuterManager's "getPlaces"
		 * */
		
	}
	
	@Test
	public void todo() {
        assertTrue(false);
	}
	
}