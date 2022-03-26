package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import lk.nilla.contentManagement.Diseases;
import lk.nilla.contentManagement.DiseasesTranslation;
import lk.nilla.contentManagement.Product;
import lk.nilla.contentManagement.ProductTranslation;
import lk.nilla.contentManagement.ProductType;

public class DiseasesService {

	// id <-> language
		private HashMap<Language, Integer> diseaseLanguages = new HashMap<Language, Integer>();
		
		public DiseasesService() {
			diseaseLanguages.put(Language.English, 1);
			diseaseLanguages.put(Language.Sinhala, 2);
			
		}
		
		private Language getLanguage(int index) {
			for(Entry<Language, Integer> entry : diseaseLanguages.entrySet()) {
				if(entry.getValue() == index) {
					return entry.getKey();
				}
			}
			return null;
		}
	//initializes database connection object
	private DBConnection connection = new DBConnection();
	
	//returns disease when given the diseaseID
	public Diseases getDisease(int diseaseID) {
		
		//from Diseases table
		try {
			ResultSet rs = new SQLStatement("SELECT", "Disease")
					.where("diseaseID", diseaseID)
					.generate(connection)
					.executeQuery();
			if(rs.next()) {
				Diseases disease = new Diseases(rs.getInt(1),rs.getString(2));
				
				ResultSet rs2 = new SQLStatement("SELECT", "DiseaseTranslation").where("diseaseID", diseaseID).generate(connection).executeQuery();
				
				while (rs2.next()) {
					DiseasesTranslation translation = new DiseasesTranslation(rs2.getInt(1),getLanguage(rs2.getInt(2)),rs2.getString(3),rs2.getString(4),rs2.getString(5));
					disease.translations.add(translation);
				}
				
				return disease;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Diseases> getDiseases(String query, int limit, int page) {
		try {
			int offset = limit * (page-1);
			PreparedStatement stmt = connection.prepare("SELECT DISTINCT diseaseID FROM DiseaseTranslation "
					+ "WHERE diseaseName LIKE ? OR diseaseDesc LIKE ?"
					+ "LIMIT ? OFFSET ?");
			stmt.setString(1, "%"+query+"%");
			stmt.setString(2, "%"+query+"%");
			stmt.setInt(3, limit);
			stmt.setInt(4, offset);
			ResultSet rs = stmt.executeQuery();

			if(rs.next()) {
				ArrayList<Diseases> diseases = new ArrayList<Diseases>(0);
				do {
					diseases.add(getDisease(rs.getInt(1)));
				} while (rs.next());
				return diseases;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//inserts record into Diseases table
	public int createDiseases(Diseases diseases) throws Exception {
		try {
			PreparedStatement rs = new SQLStatement("INSERT", "Disease")
					.set("diseaseImg", diseases.diseaseImg)
					.generate(connection);
				rs.executeUpdate();
			ResultSet id = rs.getGeneratedKeys();
			
			if(id.next()) {
				int count = 0;
				for(DiseasesTranslation translation : diseases.translations) {
					count = count + (addTranslation(id.getInt(1), translation) ? 1 : 0);
				}
				
				if(count == diseases.translations.size()) {
					return id.getInt(1);
				} else {
					new SQLStatement("DELETE", "Disease")
						.where("diseaseID",diseases.diseaseID)
						.generate(connection)
						.executeUpdate();
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
	
	
	//updates Diseases table
	public int updateDiseases(Diseases diseases) throws Exception {
		try {
			int count = 0;
			count = count + new SQLStatement("UPDATE", "Disease")
					.where("diseaseID", diseases.diseaseID)
					.set("productImg", diseases.diseaseImg)
					.generate(connection)
					.executeUpdate();
			
			//drop translations
			int status = new SQLStatement("DELETE", "DiseaseTranslation")
				.where("productID", diseases.diseaseID)
				.generate(connection)
				.executeUpdate();
			if(status > 0) {
				for(DiseasesTranslation translation : diseases.translations) {
					count = count + (addTranslation(diseases.diseaseID, translation) ? 1 : 0);
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
	
	
	// deletes record in Diseases table
	public boolean deleteDiseases(int diseaseID) {
		try {
			int status = new SQLStatement("DELETE", "Disease")
					.where("diseaseID", diseaseID)
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
	
	

	public Boolean addTranslation (int diseaseID, DiseasesTranslation translation) {
		try {
			int status = new SQLStatement("INSERT", "DiseaseTranslation")
				.set("diseaseID", diseaseID)
				.set("languageID", diseaseLanguages.get(translation.language))
				.set("diseaseName", translation.diseaseName)
				.set("diseaseDesc", translation.diseaseDesc)
				.set("treatment",translation.treatment)
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
	public Boolean deleteTranslation(int diseaseID, Language language) {
		try {
			int status = new SQLStatement("DELETE", "DiseaseTranslation")
					.where("diseaseID", diseaseID)
					.where("languageID", diseaseLanguages.get(language))
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
