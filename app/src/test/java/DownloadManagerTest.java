package test.java;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import android.test.AndroidTestCase;
import net.kenneho.runnow.networking.DownloadManager;
import net.kenneho.runnow.networking.IDownloadListener;

// Source: http://www.making-software.com/2012/10/31/testable-android-asynctask/
@RunWith(RobolectricTestRunner.class)
public class DownloadManagerTest extends AndroidTestCase implements IDownloadListener {

	DownloadManager downloader;
    CountDownLatch signal;
    URL url; 
    String result;
    String urlString = "http://reisapi.ruter.no/Heartbeat";
 
	@Before
    public void setUp() throws Exception {
        super.setUp();
        
        signal = new CountDownLatch(1);
        downloader = new DownloadManager(this);
        url = new URL(urlString);
    	}
 
    @Test 
	public void testDownload() throws InterruptedException, URISyntaxException
     {
         downloader.download(url);
         
         // TODO: Rewrite code, don't want to actually download and wait in a Unit test.
         signal.await(10, TimeUnit.SECONDS);

         // The Heartbeat REST call should return "Pong"
         assertTrue(result.contains("Pong"));
     }
	
	@Override
	public void downloadCompleted(String result) {
		signal.countDown();
		this.result = result;
	}

}
