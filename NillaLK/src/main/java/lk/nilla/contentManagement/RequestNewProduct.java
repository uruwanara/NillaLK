package lk.nilla.contentManagement;

import java.sql.Date;

public class RequestNewProduct extends Request{
	public String newProductType = null;
	public String newProductName = null;
	public String newProductDes = null;
	public String newProductImg = null;
	public int shopID = 0;
	
	public RequestNewProduct(int requestID, Date requestDate, RequestState requestState, RequestType requestType, int userID, String newProductType, String newProductName, String newProductDes, String newProductImg, int shopID) {
		super(requestID, requestDate, requestState, userID, requestType);
		this.newProductType = newProductType;
		this.newProductName = newProductName;
		this.newProductDes = newProductDes;
		this.newProductImg = newProductImg;
		this.shopID = shopID;
	}

	public RequestNewProduct() {
		// TODO Auto-generated constructor stub
	}
}



