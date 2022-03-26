package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import lk.nilla.contentManagement.Product;
import lk.nilla.contentManagement.ProductTranslation;
import lk.nilla.contentManagement.ProductType;

public class ProductService {

	//initializes database connection object
	private DBConnection connection = new DBConnection();
	
	/**
	 * Get product when productID is given
	 * @param productID
	 * @return Product instance
	 */
	public Product getProduct(int productID) {
		try {
			ResultSet rs = new SQLStatement("SELECT", "Product")
					.where("productID", productID)
					.generate(connection)
					.executeQuery();
			if(rs.next()) {
				Product product = new Product(rs.getInt(1), ProductType.valueOf(rs.getString(2)), rs.getString(3));
				
				ResultSet rs2 = new SQLStatement("SELECT", "ProductTranslation")
						.where("productID", productID)
						.generate(connection)
						.executeQuery();
				
				while (rs2.next()) {
					ProductTranslation translation = new ProductTranslation(rs2.getString(3), rs2.getString(4), getLanguage(rs2.getInt(2)));
					product.translations.add(translation);
				}
				
				return product;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns products matching the query
	 * @param query
	 * @return ArrayList{@literal <}Product{@literal >}
	 */
	public ArrayList<Product> getProducts(String query, int limit, int page) {
		try {
			int offset = limit * (page-1);
			PreparedStatement stmt = connection.prepare("SELECT DISTINCT productID FROM ProductTranslation "
					+ "WHERE productName LIKE ? OR productDesc LIKE ?"
					+ "LIMIT ? OFFSET ?");
			stmt.setString(1, "%"+query+"%");
			stmt.setString(2, "%"+query+"%");
			stmt.setInt(3, limit);
			stmt.setInt(4, offset);
			ResultSet rs = stmt.executeQuery();

			if(rs.next()) {
				ArrayList<Product> products = new ArrayList<Product>(0);
				do {
					products.add(getProduct(rs.getInt(1)));
				} while (rs.next());
				return products;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Updates product when Product instance is given
	 * @param product
	 * @return success(Boolean)
	 */
	public Boolean updateProduct(Product product) {
		try {
			int count = 0;
			count = count + new SQLStatement("UPDATE", "Product")
					.where("productID", product.productID)
					.set("productType", product.productType.toString())
					.set("productImg", product.productImg)
					.generate(connection)
					.executeUpdate();
			
			//drop translations
			int status = new SQLStatement("DELETE", "ProductTranslation")
				.where("productID", product.productID)
				.generate(connection)
				.executeUpdate();
			if(status > 0) {
				for(ProductTranslation translation : product.translations) {
					count = count + (addTranslation(product.productID, translation) ? 1 : 0);
				}
			}
			
			if(count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Inserts new product when Product instance is given
	 * @param product
	 * @return ProductID
	 */
	public int createProduct(Product product) {
		try {
			PreparedStatement rs = new SQLStatement("INSERT", "Product")
					.set("productType", product.productType.toString())
					.set("productImg", product.productImg)
					.generate(connection);
			rs.executeUpdate();
			ResultSet id = rs.getGeneratedKeys();
			
			if(id.next()) {
				int count = 0;
				for(ProductTranslation translation : product.translations) {
					count = count + (addTranslation(id.getInt(1), translation) ? 1 : 0);
				}
				
				if(count == product.translations.size()) {
					return id.getInt(1);
				} else {
					// rollback
					new SQLStatement("DELETE", "Product")
						.where("productID", product.productID)
						.generate(connection)
						.executeUpdate();
					return 0;
				}
			} else {
				return 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Deletes product
	 * @param productID
	 * @return success(Boolean)
	 */
	public Boolean deleteProduct(int productID) {
		try {
			int status = new SQLStatement("DELETE", "Product")
					.where("productID", productID)
					.generate(connection)
					.executeUpdate();
			if(status > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Add new product translation
	 * @param productID
	 * @param translation
	 * @return
	 */
	public Boolean addTranslation (int productID, ProductTranslation translation) {
		try {
			int status = new SQLStatement("INSERT", "ProductTranslation")
				.set("productID", productID)
				.set("languageID", productLanguages.get(translation.language))
				.set("productName", translation.productName)
				.set("productDesc", translation.productDesc)
				.generate(connection)
				.executeUpdate();
			if(status > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deletes product translation
	 * @param productID
	 * @param language
	 * @return success(Boolean)
	 */
	public Boolean deleteTranslation(int productID, Language language) {
		try {
			int status = new SQLStatement("DELETE", "ProductTranslation")
					.where("productID", productID)
					.where("languageID", productLanguages.get(language))
					.generate(connection)
					.executeUpdate();
			if(status > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public void close() {
		connection.close();
	}
	
	
	// id <-> language
	private HashMap<Language, Integer> productLanguages = new HashMap<Language, Integer>();
	
	public ProductService() {
		productLanguages.put(Language.English, 1);
		productLanguages.put(Language.Sinhala, 2);
		
	}
	
	private Language getLanguage(int index) {
		for(Entry<Language, Integer> entry : productLanguages.entrySet()) {
			if(entry.getValue() == index) {
				return entry.getKey();
			}
		}
		return null;
	}

}
