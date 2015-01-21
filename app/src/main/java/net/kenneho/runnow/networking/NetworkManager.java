package net.kenneho.runnow.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {
	private NetworkInfo networkInfo;
	private HttpManager httpManager;

	public NetworkManager(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connMgr.getActiveNetworkInfo();
	}
	
	public boolean isConnected() {
		
		return networkInfo.isConnected();
	
	}
	public boolean isConnected_v2() throws Exception {
		String urlString = "http://reisapi.ruter.no/Heartbeat";
		String heartbeat = httpManager.makeSimpleRestCall(urlString);
		if (heartbeat.equalsIgnoreCase("\"pong\"")) {
			return true;
		}
		else {
			return false;
		}
	}

	
}
