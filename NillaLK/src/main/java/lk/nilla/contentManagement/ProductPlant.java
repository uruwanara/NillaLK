package lk.nilla.contentManagement;

public class ProductPlant extends Product {

	public String additionalDescription = null;
	public int plantID = 0;
	
	public ProductPlant(int productID, ProductType productType, String productImg, String additionalDescription, int plantID) {
		super(productID, productType, productImg);
		this.additionalDescription = additionalDescription;
	}

	public ProductPlant() {
	}
	
}
