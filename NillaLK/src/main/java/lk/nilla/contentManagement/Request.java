package lk.nilla.contentManagement;

import java.sql.Date;

public class Request {
	public int requestID = 0;
	public Date requestDate  = null;
	public RequestState requestState = null;
	public int userID = 0;
	public RequestType requestType = null;
	
	public Request(int requestID, Date requestDate, RequestState requestState, int userID, RequestType requestType) {
		super();
		this.requestID = requestID;
		this.requestDate = requestDate;
		this.requestState = requestState;
		this.userID = userID;
		this.requestType = requestType;
	}
	
	public Request() {
		// TODO Auto-generated constructor stub
	}
}
