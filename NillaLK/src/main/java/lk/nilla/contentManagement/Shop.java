package lk.nilla.contentManagement;

import java.sql.Date;

public class Shop {
	public int shopID = 0;
	public String shopName = null;
	public String shopImg = null;
	public Date regDate = null;
	public String shopLocation = null;
	public ShopState shopState = null;
	public Address address = null;
	public int ShopOwnerID = 0;

	public Shop(int shopID, String shopName, String shopImg, Date regDate, String shopLocation, ShopState shopState, Address address, int ShopOwnerID) {
		super();
		this.shopID = shopID;
		this.shopName = shopName;
		this.shopImg = shopImg;
		this.regDate = regDate;
		this.shopLocation = shopLocation;
		this.shopState = shopState;
		this.address = address;
		this.ShopOwnerID = ShopOwnerID;
	}
	
	public Shop() {
		// TODO Auto-generated constructor stub
	}
}
