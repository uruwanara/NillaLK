package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.contentManagement.Address;
import lk.nilla.contentManagement.Shop;
import lk.nilla.contentManagement.ShopState;

public class ShopService {

	public ShopService() {	}
	
	private DBConnection connection = new DBConnection();

	// Get shop by shopID
	public Shop getShop(int shopID) throws Exception{
		
		Shop shop;
		try {
			ResultSet rs = new SQLStatement("SELECT", "Shop").where("shopID", shopID).generate(connection).executeQuery();

			if(!rs.next()) {
				return null;				
			}			
			shop = new Shop(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5), ShopState.valueOf(rs.getString(6)), new Address(rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)), rs.getInt(11));
			rs.close();
			return shop;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** Get shop with following filtering methods.
	 * @param shopName - provide null to skip filtering using shopName
	 * @param shopOwnerID - provide 0 to skip filtering using shopOwnerID
	 * @param shopState - provide null to skip filtering using shopState
	 * @param limit - provide 0 to skip limiting number of shops returned
	 * @param offset - provide 0 to skip offset. Ignored if limit is already 0
	 * @return ArrayList{@literal <}Shop{@literal >} - array list of Shop. Empty ArrayList (size == 0) if no results
	 */
	public ArrayList<Shop> getShops(String shopName, int shopOwnerID, String shopState, int limit, int offset){
		ArrayList<Shop> shops = new ArrayList<Shop>(0);
		try {
			SQLStatement stmt = new SQLStatement("SELECT", "Shop");

			if(shopName != null) {
				stmt = stmt.like("shopName", shopName);
			}
			if(shopOwnerID > 0) {
				stmt = stmt.where("shopOwnerID", shopOwnerID);
			}
			if(shopState != null) {
				stmt = stmt.where("shopState", shopState);
			}
			if(limit > 0) {
				stmt = stmt.limit(limit).offset(offset);
			}
			
			ResultSet rs = stmt.generate(connection).executeQuery();

			while(rs.next()) {
				shops.add(new Shop(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5), ShopState.valueOf(rs.getString(6)), new Address(rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)), rs.getInt(11)));
			}
			
			if(shops.size() > 0) {
				return shops;
			} else {
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//create new shop
	public int createShop(Shop shop) throws Exception{
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "Shop")
					.set("shopName", shop.shopName)
					.set("shopImg", shop.shopImg)
					.set("regDate", shop.regDate)
					.set("shopLocation", shop.shopLocation)
					.set("shopState", shop.shopState.toString())
					.set("addLine1", shop.address.addLine1)
					.set("addLine2", shop.address.addLine2)
					.set("city", shop.address.city)
					.set("district", shop.address.district)
					.set("shopOwnerID", shop.ShopOwnerID)
					.generate(connection);
			stmt.executeUpdate();
			
			int index = 0;
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				index = rs.getInt(1);
			}
			rs.close();
			return index;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	// update shop details
	public int updateShop(Shop shop, int shopOwnerID) throws Exception {
		try {
			SQLStatement stmt = new SQLStatement("UPDATE", "Shop");
			if(shop.shopName != null) {
				stmt.set("shopName", shop.shopName);
			}
			if(shop.shopImg != null) {
				stmt.set("shopImg", shop.shopImg);
			}
			if(shop.regDate != null) {
				stmt.set("regDate", shop.regDate);
			}
			if(shop.shopLocation != null) {
				stmt.set("shopLocation", shop.shopLocation);
			}
			if(shop.shopState != null) {
				stmt.set("shopState", shop.shopState.toString());
			}
			if(shop.address.addLine1 != null) {
				stmt.set("addLine1", shop.address.addLine1);
			}
			if(shop.address.addLine2 != null) {
				stmt.set("addLine2", shop.address.addLine2);
			}
			if(shop.address.city != null) {
				stmt.set("city", shop.address.city);
			}
			if(shop.address.district != null) {
				stmt.set("district", shop.address.district);
			}
			stmt.where("shopID", shop.shopID);
			
			if(shopOwnerID > 0) {
				stmt.where("shopOwnerID", shopOwnerID);
			}
			
			PreparedStatement prepstmt = stmt.generate(connection);
			prepstmt.executeUpdate();
			
			int rows = prepstmt.getUpdateCount();			
			return rows;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//delete selected shop 
	public int deleteShop(int shopID, int shopOwnerID) {
		try {
			SQLStatement stmt = new SQLStatement("DELETE", "Shop").where("shopID", shopID);
			if(shopOwnerID > 0) {
				stmt = stmt.where("shopOwnerID", shopOwnerID);
			}
			PreparedStatement prepstmt = stmt.generate(connection);
			prepstmt.executeUpdate();
			
			int rows = prepstmt.getUpdateCount();
			return rows;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}

