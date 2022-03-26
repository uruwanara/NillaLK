package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import lk.nilla.contentManagement.Pest;
import lk.nilla.contentManagement.PestTranslation;

public class PestService {

	// id <-> language
		private HashMap<Language, Integer> pestLanguages = new HashMap<Language, Integer>();
		
		public PestService() {
			pestLanguages.put(Language.English, 1);
			pestLanguages.put(Language.Sinhala, 2);
			
		}
		
		private Language getLanguage(int index) {
			for(Entry<Language, Integer> entry : pestLanguages.entrySet()) {
				if(entry.getValue() == index) {
					return entry.getKey();
				}
			}
			return null;
		}
	//initializes database connection object
	private DBConnection connection = new DBConnection();
	
	//returns pest when given the pestID
	public Pest getPest(int pestID) {
		
		//from Pest table
		try {
			ResultSet rs = new SQLStatement("SELECT", "Pest")
					.where("pestID", pestID)
					.generate(connection)
					.executeQuery();
			if(rs.next()) {
				Pest pest = new Pest(rs.getInt(1),rs.getString(2),rs.getString(3));
				
				ResultSet rs2 = new SQLStatement("SELECT", "PestTranslation").where("pestID", pestID).generate(connection).executeQuery();
				
				while (rs2.next()) {
					PestTranslation translation = new PestTranslation(rs2.getInt(1),getLanguage(rs2.getInt(2)),rs2.getString(3),rs2.getString(4),rs2.getString(5));
					pest.translations.add(translation);
				}
				
				return pest;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Pest> getPests(String query, int limit, int page) {
		try {
			int offset = limit * (page-1);
			PreparedStatement stmt = connection.prepare("SELECT DISTINCT pestID FROM PestTranslation "
					+ "WHERE pestName LIKE ? OR pestDesc LIKE ?"
					+ "LIMIT ? OFFSET ?");
			stmt.setString(1, "%"+query+"%");
			stmt.setString(2, "%"+query+"%");
			stmt.setInt(3, limit);
			stmt.setInt(4, offset);
			ResultSet rs = stmt.executeQuery();

			if(rs.next()) {
				ArrayList<Pest> pest = new ArrayList<Pest>(0);
				do {
					pest.add(getPest(rs.getInt(1)));
				} while (rs.next());
				return pest;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//inserts record into Pest table
	public int createPest(Pest pest) throws Exception {
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "Pest")
					.set("pestImg", pest.pestImg)
					.set("pestSciName", pest.pestSciName)
					.generate(connection);
				stmt.executeUpdate();
			
			int pestIndex = 0;
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()) {
				pestIndex = rs.getInt(1);
				int count = 0;
				for(PestTranslation translation : pest.translations) {
					count = count + (createPestTranslation(translation, pestIndex));
				}
				
				if(count == pest.translations.size()) {
					return rs.getInt(1);
				} else {
					new SQLStatement("DELETE", "Pest")
						.where("pestID",pest.pestID)
						.generate(connection)
						.executeLargeUpdate();
					return 0;
				}
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public int createPestTranslation (PestTranslation pestTranslation, int pestID) {
		try {
			int langID;
			if (pestTranslation.language == Language.English)
				langID = 1;
			else
				langID = 2;
			
			PreparedStatement stmtTrans = new SQLStatement("INSERT", "PestTranslation")
				.set("pestID", pestID)
				.set("languageID", langID)
				.set("pestName", pestTranslation.pestName)
				.set("pestDesc", pestTranslation.pestDesc)
				.set("pesticide",pestTranslation.pesticide)
				.generate(connection);
				stmtTrans.executeUpdate();
				return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}	
	
	//updates Pest table
	public int updatePest(Pest pest) throws Exception {
		try {
			int count = 0;
			count = count + new SQLStatement("UPDATE", "Pest")
					.where("pestID", pest.pestID)
					.set("pestImg", pest.pestImg)
					.set("pestSciName", pest.pestSciName)
					.generate(connection)
					.executeUpdate();
			
			//drop translations
			int status = new SQLStatement("DELETE", "PestTranslation")
				.where("pestID", pest.pestID)
				.generate(connection)
				.executeUpdate();
			if(status > 0) {
				for(PestTranslation translation : pest.translations) {
					count = count + (createPestTranslation(translation, pest.pestID));
				}
			}
			
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
	
	
	
	
	
	// deletes record in Pest table
	public boolean deletePest(int pestID) {
		try {
			int status = new SQLStatement("DELETE", "Pest")
					.where("pestID", pestID)
					.generate(connection)
					.executeUpdate();
			if(status > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean deleteTranslation(int pestID, Language language) {
		try {
			int status = new SQLStatement("DELETE", "PestTranslation")
					.where("pestID", pestID)
					.where("languageID", pestLanguages.get(language))
					.generate(connection)
					.executeUpdate();
			if(status > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}
	
	public void close() {
		connection.close();
	}
	

}
