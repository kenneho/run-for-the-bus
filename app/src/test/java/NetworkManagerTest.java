package test.java;

import net.kenneho.runnow.networking.NetworkManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.test.AndroidTestCase;

// Testing code based on code from http://bit.ly/1p2vDWU
@RunWith(RobolectricTestRunner.class)
public class NetworkManagerTest extends AndroidTestCase {
	NetworkManager networkManager; 
	ShadowConnectivityManager shadowConnectivityManager;
	ShadowNetworkInfo shadowNetworkInfo;
	ConnectivityManager cm;

	@Before
	public void setup() {

		System.setProperty("dexmaker.dexcache", Environment.getDownloadCacheDirectory().getPath());

		cm =(ConnectivityManager)Robolectric.application.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Let's create a shadow of the ConnectivityManager so that we can access and manipulate the object.
		shadowConnectivityManager = Robolectric.shadowOf(cm);
		shadowNetworkInfo = Robolectric.shadowOf(cm.getActiveNetworkInfo());
		Context context = Robolectric.application.getApplicationContext();
		networkManager = new NetworkManager(context);

	}
	@Test
	public void shouldNotBeNull() {
		assertNotNull(cm);
		assertNotNull(cm.getActiveNetworkInfo());
	}

	@Test
	public void returnTrueWhenConnectedToNetwork() {
		
		shadowNetworkInfo.setConnectionStatus(true);
		assertTrue(networkManager.isConnected());
	}
	@Test
	public void returnFalseWhenNotConnectedToNetwork() {

		shadowNetworkInfo.setConnectionStatus(false);
		assertFalse(networkManager.isConnected());

	}
	
}


