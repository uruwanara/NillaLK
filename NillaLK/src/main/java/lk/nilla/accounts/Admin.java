package lk.nilla.accounts;

import java.sql.Timestamp;

public class Admin extends User{
	public String description = null;
	
	public Admin(int userID, Timestamp registered, String email, String fname, String lname, String profilePicture, UserType userType, String description) {
		super(userID, registered, email, fname, lname, profilePicture, userType);
		this.description = description;
	}

	public Admin() {
		// TODO Auto-generated constructor stub
	}

}
