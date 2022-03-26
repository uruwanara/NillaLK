package lk.nilla.accounts;

import java.sql.Date;
import java.sql.Timestamp;

import lk.nilla.contentManagement.Address;

public class Gardener extends User{
	public String contactNum = null;
	public Date dob = null;
	public Address address = null;
	
	public Gardener(int userID, Timestamp registered, String email, String fname, String lname, String profilePicture, UserType userType, String contactNum, Date dob, Address address) {
		super(userID, registered, email, fname, lname, profilePicture, userType);
		this.contactNum = contactNum;
		this.dob = dob;
		this.address = address;
	}

	public Gardener() {
		// TODO Auto-generated constructor stub
	}

}
