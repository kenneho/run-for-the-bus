package net.kenneho.runnow.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;


import android.os.AsyncTask;

// A testable asynctask-solution based on http://www.making-software.com/2012/10/31/testable-android-asynctask/
public class DownloadManager
{
	private IDownloadListener downloadListener;

	public DownloadManager( IDownloadListener downloadListener )
	{
		this.downloadListener = downloadListener;
	}

	public void download( URL url ) throws URISyntaxException
	{
		// TODO: Deprecated. Please fix.
		String urlString = java.net.URLDecoder.decode(url.toString());
		new DownloadFilesTask().execute(url);
	}

	// Code based on http://developer.android.com/training/basics/network-ops/connecting.html
	private class DownloadFilesTask extends AsyncTask<URL, Integer, String> {
		protected String doInBackground(URL... urls) {
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}

		private String downloadUrl(URL url) throws IOException {
			InputStream is = null;
			// Only display the first 500 characters of the retrieved
			// web page content.
			int len = 500;

			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();
				System.out.println("The response is: " + response);
				is = conn.getInputStream();

				// Convert the InputStream into a string
				String contentAsString = readIt(is, len);
				return contentAsString;

				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} finally {
				if (is != null) {
					is.close();
				} 
			}
		}
		// Reads an InputStream and converts it to a String.
		public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
			Reader reader = null;
			reader = new InputStreamReader(stream, "UTF-8");        
			char[] buffer = new char[len];
			reader.read(buffer);
			return new String(buffer);
		}

		@Override
		protected void onPostExecute(String result) {
			downloadListener.downloadCompleted(result);
		}
	}

}
