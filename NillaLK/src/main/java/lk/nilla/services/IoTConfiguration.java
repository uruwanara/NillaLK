package lk.nilla.services;

public class IoTConfiguration {
	public String deviceID;
	public String privateKey;
	
	public IoTConfiguration(String deviceID, String privateKey) {
		this.deviceID = deviceID;
		this.privateKey = privateKey;
	}
}
