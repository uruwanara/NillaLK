package lk.nilla.accounts;

public class Device {

	public String deviceID;
	public String state;
	public String telemetry;
	
	public Device(String deviceID, String state, String telemetry) {
		this.deviceID = deviceID;
		this.state = state;
		this.telemetry = telemetry;
	}
}
