package lk.nilla.api;
import lk.nilla.accounts.UserType;

public class ErrorReasonWithUserType {

	ErrorType reason;
	UserType userType;
	public ErrorReasonWithUserType(ErrorType et, UserType ut) {
		reason = et;
		userType = ut;
	}

}
