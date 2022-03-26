package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.contentManagement.PlantFertilizer;

public class PlantFertilizerService {
	
	public PlantFertilizerService() {}

	private DBConnection connection = new DBConnection();
	
	public PlantFertilizer getPlantFertilizer(int plantID, int fertilizerID) throws Exception{
		
		PlantFertilizer plantFertilizer;
		try {
			SQLStatement stmt= new SQLStatement("SELECT", "PlantFertilizer");
			if(plantID > 0) {
				stmt = stmt.where("plantID", plantID);
			}
			if(fertilizerID > 0) {
				stmt = stmt.where("fertilizerID", fertilizerID);				
			}
			ResultSet rs = stmt.generate(connection).executeQuery();
			if(!rs.next()) {
				return null;
			}
//			plantFertilizer = new PlantFertilizer(rs.getInt(1), rs.getInt(2), rs.getBigDecimal(3), rs.getString(4), ProductState.valueOf(rs.getString(5)));
			plantFertilizer = new PlantFertilizer(rs.getInt(1), rs.getInt(2));

			rs.close();
			return plantFertilizer;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<PlantFertilizer> getPlantFertilizers(int plantID, int fertilizerID, int limit, int offset){
		ArrayList<PlantFertilizer> plantFertilizers = new ArrayList<PlantFertilizer>(0);
		try {
			SQLStatement stmt = new SQLStatement("SELECT", "PlantFertilizer");
			
			if(plantID > 0) {
				stmt = stmt.where("plantID", plantID);
			}
			if(fertilizerID > 0) {
				stmt = stmt.where("fertilizerID", fertilizerID);
			}
			if(limit > 0) {
				stmt = stmt.limit(limit).offset(offset);
			}
			ResultSet rs = stmt.generate(connection).executeQuery();
			
			while(rs.next()) {
				plantFertilizers.add(new PlantFertilizer(rs.getInt(1), rs.getInt(2)));
//				plantFertilizers.add(new PlantFertilizer(rs.getInt(1), rs.getInt(2), rs.getBigDecimal(3), rs.getString(4), ProductState.valueOf(rs.getString(5))));
			}
			return plantFertilizers;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public int createPlantFertilizer(PlantFertilizer plantFertilizer) throws Exception{
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "PlantFertilizer")
					.set("fertilizerID", plantFertilizer.fertilizerID)
					.set("ShopID", plantFertilizer.plantID)
					.generate(connection);
			stmt.executeUpdate();			
			return 1;
			
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int updatePlantFertilizer(PlantFertilizer plantFertilizer, int plantID) {

		try {
			
			SQLStatement stmt = new SQLStatement("UPDATE", "PlantFertilizer");

			stmt = stmt.where("fertilizerID", plantFertilizer.fertilizerID);
			
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
	
	public int deletePlantFertilizer(int fertilizerID, int plantID) {
		try {
			// SQL statement logic
			SQLStatement stmt = new SQLStatement("DELETE", "PlantFertilizer").where("fertilizerID", fertilizerID);
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