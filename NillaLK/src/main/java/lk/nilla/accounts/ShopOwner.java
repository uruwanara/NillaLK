package lk.nilla.accounts;

import java.sql.Timestamp;

public class ShopOwner extends User{
	public String contactNum = null;
	
	public ShopOwner(int userID, Timestamp registered, String email, String fname, String lname, String profilePicture, UserType userType, String contactNum) {
		super(userID, registered, email, fname, lname, profilePicture, userType);
		this.contactNum = contactNum;
	}

	public ShopOwner() {
		// TODO Auto-generated constructor stub
	}

}
