package lk.nilla.contentManagement;

import java.util.ArrayList;

public class Product {
	public int productID = 0;
	public ProductType productType = null;
	public String productImg = null;
	public ArrayList<ProductTranslation> translations = new ArrayList<ProductTranslation>(0);
	
	public Product(int productID, ProductType productType, String productImg) {
		super();
		this.productID = productID;
		this.productType = productType;
		this.productImg = productImg;
	}

	public Product() {}
	
}