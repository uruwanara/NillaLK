package lk.nilla.accounts;

import java.sql.Timestamp;

public class User {
	public int userID = 0;
	public Timestamp registered = null;
	public String email = null;
	public String firstName = null;
	public String lastName = null;
	public String profilePicture = null;
	public UserType userType = null;
	
	public User(int userID, Timestamp registered, String email, String fname, String lname, String profilePicture, UserType userType) {
		super();
		this.userID = userID;
		this.registered = registered;
		this.email = email;
		this.firstName = fname;
		this.lastName = lname;
		this.profilePicture = profilePicture;
		this.userType = userType;
	}
	
	public User() {
		// TODO Auto-generated constructor stub
	}

}
