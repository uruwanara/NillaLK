package lk.nilla.accounts;

import java.sql.Timestamp;

public class AgriInstructor extends User{
	public String nic = null;
	public String contactNum = null;
	public String freeSlots = null;
	
	public AgriInstructor(int userID, Timestamp registered, String email, String fname, String lname, String profilePicture, UserType userType, String nic, String contactNum, String freeSlots) {
		super(userID, registered, email, fname, lname, profilePicture, userType);
		this.nic = nic;
		this.contactNum = contactNum;
		this.freeSlots = freeSlots;
	}

	public AgriInstructor() {
		// TODO Auto-generated constructor stub
	}

	
}
