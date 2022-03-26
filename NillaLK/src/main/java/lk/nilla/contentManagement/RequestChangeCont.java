package lk.nilla.contentManagement;

import java.sql.Date;

public class RequestChangeCont extends Request{
	public String changeType = null;
	public String changeTitle = null;
	public String changeDesc = null;
	public int userID = 0;
	
	public RequestChangeCont(int requestID, RequestState requestState, Date requestDate, RequestType requestType, int userID, String changeType, String changeTitle, String changeDesc) {
		super(requestID, requestDate, requestState, userID, requestType);
		this.changeType = changeType;
		this.changeTitle = changeTitle;
		this.changeDesc = changeDesc;
	}

	public RequestChangeCont() {
		// TODO Auto-generated constructor stub
	}
}



