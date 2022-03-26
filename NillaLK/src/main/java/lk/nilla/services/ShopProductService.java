package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.contentManagement.ShopProduct;

public class ShopProductService {
	
	public ShopProductService() {}

	private DBConnection connection = new DBConnection();
	
	public ShopProduct getShopProduct(int shopID, int productID) throws Exception{
		
		ShopProduct shopProduct;
		try {
			SQLStatement stmt= new SQLStatement("SELECT", "ShopProduct");
			if(shopID > 0) {
				stmt = stmt.where("shopID", shopID);
			}
			if(productID > 0) {
				stmt = stmt.where("productID", productID);				
			}
			ResultSet rs = stmt.generate(connection).executeQuery();
			if(!rs.next()) {
				return null;
			}
//			shopProduct = new ShopProduct(rs.getInt(1), rs.getInt(2), rs.getBigDecimal(3), rs.getString(4), ProductState.valueOf(rs.getString(5)));
			shopProduct = new ShopProduct(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4));

			rs.close();
			return shopProduct;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<ShopProduct> getShopProducts(int shopID, int productID, int limit, int offset){
		ArrayList<ShopProduct> shopProducts = new ArrayList<ShopProduct>(0);
		try {
			SQLStatement stmt = new SQLStatement("SELECT", "ShopProduct");
			System.out.println("2 "+shopID+" 2 "+productID);
			if(shopID > 0) {
				stmt = stmt.where("shopID", shopID);
			}
			if(productID > 0) {
				stmt = stmt.where("productID", productID);
			}
			if(limit > 0) {
				stmt = stmt.limit(limit).offset(offset);
			}
			ResultSet rs = stmt.generate(connection).executeQuery();
			
			while(rs.next()) {
				shopProducts.add(new ShopProduct(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4)));
//				shopProducts.add(new ShopProduct(rs.getInt(1), rs.getInt(2), rs.getBigDecimal(3), rs.getString(4), ProductState.valueOf(rs.getString(5))));
			}
			return shopProducts;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public int createShopProduct(ShopProduct shopProduct) throws Exception{
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "ShopProduct")
					.set("productID", shopProduct.productID)
					.set("ShopID", shopProduct.shopID)
					.set("productPrice", shopProduct.productPrice)
					.set("productExp", shopProduct.productExp)
//					.set("productState", shopProduct.productState.toString())
					.generate(connection);
			stmt.executeUpdate();			
			return 1;
			
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int updateShopProduct(ShopProduct shopProduct, int shopID) {

		try {
			
			SQLStatement stmt = new SQLStatement("UPDATE", "ShopProduct");
			if(shopProduct.productExp != null) {
				stmt.set("productExp", shopProduct.productExp);				
			}
			if(shopProduct.productPrice != 0) {
				stmt.set("productPrice", shopProduct.productID);
			}
/*			if(shopProduct.productState != null) {
				stmt.set("productState", shopProduct.productState.toString());
			}
*/			stmt = stmt.where("productID", shopProduct.productID);
			
			if(shopID > 0) {
				stmt = stmt.where("shopID", shopID);
			} else {
				return -1;
			}
			
			PreparedStatement prepstmt = stmt.generate(connection); // generate prepared statement (assign so it can be used later)
			prepstmt.executeUpdate();
			
			// get number of affected rows so we can decide whether the update was successful or not (either 0 or 1)
			int rows = prepstmt.getUpdateCount();
			return rows; // 0 if failed
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int deleteShopProduct(int productID, int shopID) {
		try {
			// SQL statement logic
			SQLStatement stmt = new SQLStatement("DELETE", "ShopProduct").where("productID", productID);
			if(shopID > 0) {
				stmt = stmt.where("shopID", shopID);				
			} else {
				return -1;
			}
			
			PreparedStatement prepstmt = stmt.generate(connection); // generate prepared statement (assign so it can be used later)
			prepstmt.executeUpdate();
			
			// get number of affected rows so we can decide whether the update was successful or not
			int rows = prepstmt.getUpdateCount();
			return rows; // 0 if failed
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}	
}