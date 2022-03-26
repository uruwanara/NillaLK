package lk.nilla.accounts;

import java.sql.Timestamp;

public class ZonalAgriHead extends User{
	public String nic = null;
	public String contactNum = null;
	public int departmentID = 0;
	
	public ZonalAgriHead(int userID, Timestamp registered, String email, String fname, String lname, String profilePicture, UserType userType, String nic, String contactNum, int departmentID) {
		super(userID, registered, email, fname, lname, profilePicture, userType);
		this.nic = nic;
		this.contactNum = contactNum;
		this.departmentID = departmentID;
	}

	public ZonalAgriHead() {
		// TODO Auto-generated constructor stub
	}

}
