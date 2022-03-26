package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import lk.nilla.contentManagement.Product;
import lk.nilla.contentManagement.ProductTranslation;
import lk.nilla.contentManagement.ProductType;
import lk.nilla.contentManagement.Symptom;

public class SymptomService {
	
	
	// id <-> language
	private HashMap<Language, Integer> symptomLanguages = new HashMap<Language, Integer>();
	
	public SymptomService() {
		symptomLanguages.put(Language.English, 1);
		symptomLanguages.put(Language.Sinhala, 2);
		
	}
	
	private Language getLanguage(int index) {
		for(Entry<Language, Integer> entry : symptomLanguages.entrySet()) {
			if(entry.getValue() == index) {
				return entry.getKey();
			}
		}
		return null;
	}

	//initializes database connection object
	private DBConnection connection = new DBConnection();
	
	
	//returns symptom when given the symptomID
	public Symptom getSymptom(int symptomID) {
		try {
			ResultSet rs = new SQLStatement("SELECT", "Symptom")
					.where("symptomID", symptomID)
					.generate(connection)
					.executeQuery();
			if(rs.next()) {
				Symptom symptom = new Symptom(rs.getInt(1), getLanguage(rs.getInt(2)), rs.getString(3));
				
				return symptom;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public ArrayList<Symptom> getSymptoms(String query, int limit, int page) {
		try {
			int offset = limit * (page-1);
			PreparedStatement stmt = connection.prepare("SELECT DISTINCT symptomID FROM Symptom "
					+ "WHERE symtomDesc LIKE ?"
					+ "LIMIT ? OFFSET ?");
			stmt.setString(1, "%"+query+"%");
			stmt.setString(1, "%"+query+"%");
			stmt.setInt(2, limit);
			stmt.setInt(3, offset);
			ResultSet rs = stmt.executeQuery();

			if(rs.next()) {
				ArrayList<Symptom> symptoms = new ArrayList<Symptom>(0);
				do {
					symptoms.add(getSymptom(rs.getInt(1)));
				} while (rs.next());
				return symptoms;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
		
	
	public int createSymptom(Symptom symptom) {
		try {
			PreparedStatement rs = new SQLStatement("INSERT", "Symptom")
					.set("languageID", symptom.language)
					.set("symptomDesc", symptom.symptomDesc)
					.generate(connection);
			rs.executeUpdate();			
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	
	//change record of Symptom
	public int changeSymptom(Symptom symptom){
		
		try {
			updateSymptom(symptom);
		} catch (Exception e) {
			return -1;
		}
			
		return 0;
	}
	
	//updates Symptom table
	public int updateSymptom(Symptom symptom) throws Exception {
		try {
			int count = 0;
			count = count + new SQLStatement("UPDATE", "Symptom")
					.where("symptomID",symptom.symptomID)
					.set("languageID", symptom.language)
					.set("symptomDesc", symptom.symptomDesc)
					.generate(connection)
					.executeUpdate();
			
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	// deletes record in Symptom table
	public int deleteSymptom(int symptomID) {
			try {
				int status = new SQLStatement("DELETE", "Symptom")
						.where("symptomID", symptomID)
						.generate(connection)
						.executeUpdate();
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
		
	public void close()
	{
		connection.close();
	}

	
	
	
	
}