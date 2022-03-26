package lk.nilla.contentManagement;

import java.sql.Date;

public class RequestRegShop extends Request{
	public String regDesc = null;
	public int shopID = 0;
	
	public RequestRegShop(int requestID,  RequestState requestState, Date requestDate, RequestType requestType, int userID, String regDesc, int shopID) {
		super(requestID, requestDate, requestState, userID, requestType);
		this.regDesc = regDesc;
		this.shopID = shopID;
	}

	public RequestRegShop() {
		// TODO Auto-generated constructor stub
	}
}



