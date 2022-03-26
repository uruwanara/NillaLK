package lk.nilla.contentManagement;

import lk.nilla.services.Language;

public class ProductTranslation {

	public String productName = null;
	public String productDesc = null;
	public Language language = null;
	
	public ProductTranslation(String productName, String productDesc, Language language) {
		super();
		this.productName = productName;
		this.productDesc = productDesc;
		this.language = language;
	}

}
