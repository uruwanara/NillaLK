package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.contentManagement.Fertilizer;
import lk.nilla.contentManagement.Product;


public class FertilizerService {

	public FertilizerService() {	}
	
	private DBConnection connection = new DBConnection();

	// Get shop by fertilizerID
	public Fertilizer getFerti(int fertiID) throws Exception{
		try {
			ResultSet resultSet = new SQLStatement("SELECT", "Ferilizer").where("ferilizerID", fertiID).generate(connection).executeQuery();
			
			if(resultSet.next()) {
				Fertilizer fertilizer = new Fertilizer(resultSet.getInt(1), resultSet.getInt(2),resultSet.getFloat(3),resultSet.getFloat(4),resultSet.getFloat(5));
				return fertilizer;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	//create new Fertilizer
	public int createFerti(Fertilizer fertilizer) {
		try {
			PreparedStatement resultSet = new SQLStatement("INSERT", "Fertilizer")
					.set("period", fertilizer.period)
					.set("ferti1", fertilizer.ferti1)
					.set("ferti2", fertilizer.ferti2)
					.set("ferti3", fertilizer.ferti3)
					.generate(connection);
			resultSet.executeUpdate();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	// update shop details
	public int updateFerti(Fertilizer fertilizer) {
		try {
			int count = 0;
			count = count + new SQLStatement("UPDATE", "Fertilizer")
					.where("fertilizerID", fertilizer.fertilizerID)
					.set("period", fertilizer.period)
					.set("ferti1", fertilizer.ferti1)
					.set("ferti2", fertilizer.ferti2)
					.set("ferti3", fertilizer.ferti3)
					.generate(connection).executeUpdate();
			
			if(count > 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	//delete a fertilizer
	public int deleteFerti(int fertiID) {
		try {
			int status = new SQLStatement("DELETE", "Fertilizer").where("fertilizerID", fertiID).generate(connection).executeUpdate();
			if(status > 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
		e.printStackTrace();
			return 0;
		}
	}
}
