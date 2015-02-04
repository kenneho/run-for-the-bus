package net.kenneho.runnow.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {
	private NetworkInfo networkInfo;
	private HttpManager httpManager;
    private ConnectivityManager connMgr;

    public NetworkManager(Context context) {
		connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

	}
	
	public boolean isConnected() {
        networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
	
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
