package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.contentManagement.PlantPest;

public class PlantPestService {
	
	public PlantPestService() {}

	private DBConnection connection = new DBConnection();
	
	public PlantPest getPlantPest(int plantID, int pestID) throws Exception{
		
		PlantPest plantPest;
		try {
			SQLStatement stmt= new SQLStatement("SELECT", "PlantPest");
			if(plantID > 0) {
				stmt = stmt.where("plantID", plantID);
			}
			if(pestID > 0) {
				stmt = stmt.where("pestID", pestID);				
			}
			ResultSet rs = stmt.generate(connection).executeQuery();
			if(!rs.next()) {
				return null;
			}
//			plantPest = new PlantPest(rs.getInt(1), rs.getInt(2), rs.getBigDecimal(3), rs.getString(4), ProductState.valueOf(rs.getString(5)));
			plantPest = new PlantPest(rs.getInt(1), rs.getInt(2));

			rs.close();
			return plantPest;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<PlantPest> getPlantPests(int plantID, int pestID, int limit, int offset){
		ArrayList<PlantPest> plantPests = new ArrayList<PlantPest>(0);
		try {
			SQLStatement stmt = new SQLStatement("SELECT", "PlantPest");
			
			if(plantID > 0) {
				stmt = stmt.where("plantID", plantID);
			}
			if(pestID > 0) {
				stmt = stmt.where("pestID", pestID);
			}
			if(limit > 0) {
				stmt = stmt.limit(limit).offset(offset);
			}
			ResultSet rs = stmt.generate(connection).executeQuery();
			
			while(rs.next()) {
				plantPests.add(new PlantPest(rs.getInt(1), rs.getInt(2)));
//				plantPests.add(new PlantPest(rs.getInt(1), rs.getInt(2), rs.getBigDecimal(3), rs.getString(4), ProductState.valueOf(rs.getString(5))));
			}
			return plantPests;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public int createPlantPest(PlantPest plantPest) throws Exception{
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "PlantPest")
					.set("pestID", plantPest.pestID)
					.set("ShopID", plantPest.plantID)
					.generate(connection);
			stmt.executeUpdate();			
			return 1;
			
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int updatePlantPest(PlantPest plantPest, int plantID) {

		try {
			
			SQLStatement stmt = new SQLStatement("UPDATE", "PlantPest");

			stmt = stmt.where("pestID", plantPest.pestID);
			
			if(plantID > 0) {
				stmt = stmt.where("plantID", plantID);
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
	
	public int deletePlantPest(int pestID, int plantID) {
		try {
			// SQL statement logic
			SQLStatement stmt = new SQLStatement("DELETE", "PlantPest").where("pestID", pestID);
			if(plantID > 0) {
				stmt = stmt.where("plantID", plantID);				
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