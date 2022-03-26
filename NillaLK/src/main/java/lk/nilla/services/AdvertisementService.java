package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import lk.nilla.contentManagement.Advertisement;


public class AdvertisementService {

	public AdvertisementService() { }
	
	private DBConnection connection = new DBConnection();

	//Get advertisement by addID
	public Advertisement getAdvertisement(int addID) throws Exception {
		
		Advertisement advertisement;
		try {
			ResultSet rs = new SQLStatement("SELECT", "Advertisement").where("addID", addID).generate(connection).executeQuery();

			if(!rs.next()) {
				return null;				
			}
			advertisement = new Advertisement(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDate(5));
			rs.close();
			return advertisement;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//display all advertisements
	public ArrayList<Advertisement> getAdvertisements(String addCompany, int limit, int offset) throws Exception {
		ArrayList<Advertisement> advertisements = new ArrayList<Advertisement>(0);
		try {
			SQLStatement stmt = new SQLStatement("SELECT", "Advertisement");

			if(addCompany != null) {
				stmt = stmt.like("addCompany", addCompany);
			}
			if(limit > 0) {
				stmt = stmt.limit(limit).offset(offset);
			}
			
			ResultSet rs = stmt.generate(connection).executeQuery();
			while(rs.next()) {
				advertisements.add(new Advertisement(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDate(5)));
			}
			if(advertisements.size() > 0) {
				return advertisements;				
			} else {
				return null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//create new advertisement
	public int createAdvertisement(Advertisement advertisement) throws Exception {
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "Advertisement")
					.set("addCompany", advertisement.addCompany)
					.set("addDetails", advertisement.addDetails)
					.set("addDate", advertisement.addDate)
					.set("addImg", advertisement.addImg)
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
	
	//update existing advertisement details
	public int updateAdvertisement(Advertisement advertisement) throws Exception {
		try {
			SQLStatement stmt = new SQLStatement("UPDATE", "Advertisement");
			if(advertisement.addCompany != null) {
				stmt = stmt.set("addCompany", advertisement.addCompany);
			}
			if(advertisement.addDetails != null) {
				stmt = stmt.set("addDetails", advertisement.addDetails);
			}
			if(advertisement.addDate != null) {
				stmt = stmt.set("addDate", advertisement.addDate);
			}
			if(advertisement.addImg != null) {
				stmt = stmt.set("addImg", advertisement.addImg);
			}
			stmt.where("addID", advertisement.addID);
			
			PreparedStatement prepstmt = stmt.generate(connection);
			prepstmt.executeUpdate();
			
			int rows = prepstmt.getUpdateCount();
			return rows;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//delete existing advertisement details
	public int deleteAdvertisement(int addID) throws Exception {
		try {
			SQLStatement stmt = new SQLStatement("DELETE", "Advertisement").where("addID", addID);

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
