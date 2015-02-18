package net.kenneho.runnow.networking;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpManager {
	public DownloadManager downloadManager;
	private static String LOG = "HttpManager";

	public HttpManager() {
	}

	public Object makeRestCall(String urlString, Class<?> myclass) throws Exception {
		InputStream in = null;

        URI uri = new URI(urlString);
		
		// HTTP Get
		try {

			URL url = uri.toURL();
            Log.v(LOG, "Calling URL " + url);

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            System.out.println("Setting charset...");
            urlConnection.setRequestProperty("accept-charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

			in = urlConnection.getInputStream();

		} catch (Exception e ) {
			Log.d(LOG, "Fail to read data from Ruter using URL " + urlString + ": " + e.getMessage());
			return null;
		}

		try {
			Reader reader = new InputStreamReader(in);
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss");;
			Gson gson = gsonBuilder.create();
			
			return gson.fromJson(reader, myclass);

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to parse Gson data!" + e.toString());
		}
	}
	
	public String makeSimpleRestCall(String urlString) throws Exception {
		InputStream in = null;
		HttpURLConnection urlConnection = null;
		Log.v(LOG, "Calling simple URL " + urlString);

		// HTTP Get
		try {
			URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			if (urlConnection == null) {
				System.out.println("urlConnection == null!");
			}
			in = urlConnection.getInputStream();

		} catch (Exception e ) {
			e.printStackTrace();
			Log.d(LOG, "Fail to read data from Ruter using URL " + urlString + ": " + e.getMessage());
			return null;
		}

		try {
			String val = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
			return val;
		}

		catch (Exception e) {
			e.printStackTrace();
			in.close();
			throw new Exception("Failed to parse data!" + e.toString());
		}
	}
}