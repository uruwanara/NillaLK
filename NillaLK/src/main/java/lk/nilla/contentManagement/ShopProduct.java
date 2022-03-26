package lk.nilla.contentManagement;

//import java.math.BigDecimal;

public class ShopProduct {

	public int productID;
	public int shopID;
	public int productPrice;
	public String productExp;
//	public ProductState productState;
	
	public ShopProduct(int shopID, int productID, int productPrice, String productExp) {
		super();
		this.shopID = shopID;		
		this.productID = productID;
		this.productPrice = productPrice;
		this.productExp = productExp;
//		this.productState = productState;
	}

}
