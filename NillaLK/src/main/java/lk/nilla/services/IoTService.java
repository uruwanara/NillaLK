package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.accounts.Device;

public class IoTService {
	
	//initializes database connection object
	private DBConnection connection = new DBConnection();
			
	public Boolean setTelemetry(String deviceID, String telemetry) {
		
		try {
			int status = new SQLStatement("UPDATE", "Device")
					.where("deviceID", deviceID)
					.set("telemetry", telemetry)
					.generate(connection)
					.executeUpdate();
			
			if(status > 0) {
				return true;
			} else {
				return false;
			}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean setState(String deviceID, String state) {
		
		try {
			int status = new SQLStatement("UPDATE", "Device")
					.where("deviceID", deviceID)
					.set("state", state)
					.generate(connection)
					.executeUpdate();
			
			if(status > 0) {
				return true;
			} else {
				return false;
			}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public Device getInfo(String deviceID) {
		
		try {
			ResultSet rs = new SQLStatement("SELECT", "Device")
					.where("deviceID", deviceID)
					.generate(connection)
					.executeQuery();
			
			if(rs.next()) {
				return new Device(rs.getString(1), rs.getString(2), rs.getString(3));
			} else {
				return null;
			}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String> getDevices(int userID) {
		
		try {
			ResultSet rs = new SQLStatement("SELECT", "UserDevice")
					.where("userID", userID)
					.generate(connection)
					.executeQuery();
			
			if(rs.next()) {
				ArrayList<String> deviceIDs = new ArrayList<String>(0);
				do {
					deviceIDs.add(rs.getString(2));
				} while (rs.next());
				return deviceIDs;
			} else {
				return null;
			}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Boolean setOwner(String deviceID, int userID) {
		
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "UserDevice")
					.set("deviceID", deviceID)
					.set("userID", userID)
					.generate(connection);
			stmt.executeUpdate();
			return true;
		}  catch (Exception e) {
			return false;
		}
	}
	
	public Boolean removeOwner(String deviceID, int userID) {
		
		try {
			int status = new SQLStatement("DELETE", "UserDevice")
					.where("deviceID", deviceID)
					.where("userID", userID)
					.generate(connection)
					.executeUpdate();
			return true;
		}  catch (Exception e) {
			return false;
		}
	}
	
}
