package test.java;

import net.kenneho.runnow.networking.HttpManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import android.test.AndroidTestCase;

@RunWith(RobolectricTestRunner.class)
public class DatabaseManagerTest extends AndroidTestCase {

	@Mock
	HttpManager httpManager;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void todo() {
        assertTrue(false);
	}
	
}
