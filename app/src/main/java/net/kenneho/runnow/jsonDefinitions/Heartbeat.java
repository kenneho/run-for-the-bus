package net.kenneho.runnow.jsonDefinitions;
import com.google.gson.annotations.SerializedName;


public class Heartbeat {
	private static final String LOG = "JsonHeartbeat";

	@SerializedName("String")
	public String ping;


}

