package lk.nilla.auth;

import lk.nilla.accounts.UserType;

public class JwsUser {

	public int userID;
	public UserType userType;

	public JwsUser(int userID, UserType userType) {
		this.userID = userID;
		this.userType = userType;
	}
}
