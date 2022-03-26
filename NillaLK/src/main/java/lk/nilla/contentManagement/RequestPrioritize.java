package lk.nilla.contentManagement;

import java.sql.Date;

public class RequestPrioritize extends Request{
	public String paymentImage = null;
	public int shopID = 0;
	
	public RequestPrioritize(int requestID, RequestState requestState, Date requestDate, RequestType requestType, int userID, String paymentImage, int shopID) {
		super(requestID, requestDate, requestState, userID, requestType);
		this.paymentImage = paymentImage;
		this.shopID  =shopID;
	}

	public RequestPrioritize() {
		// TODO Auto-generated constructor stub
	}
}


