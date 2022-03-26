package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import lk.nilla.contentManagement.Address;
import lk.nilla.contentManagement.Plant;
import lk.nilla.contentManagement.PlantTranslation;
import lk.nilla.contentManagement.PlantType;
import lk.nilla.contentManagement.Product;
import lk.nilla.contentManagement.ProductTranslation;
import lk.nilla.contentManagement.Shop;
import lk.nilla.contentManagement.ShopState;

public class PlantService {

	private HashMap<Language, Integer> plantLanguages = new HashMap<Language, Integer>();

	// constructor initializes HashMaps
	public PlantService() {
		plantLanguages.put(Language.English, 1);
		plantLanguages.put(Language.Sinhala, 2);
	}

	private Language getLanguage(int index) {
		for (Entry<Language, Integer> entry : plantLanguages.entrySet()) {
			if (entry.getValue() == index) {
				return entry.getKey();
			}
		}
		return null;
	}

	// initializes database connection object
	private DBConnection connection = new DBConnection();

	// returns plant when given the plantID
	public Plant getPlant(int plantID) throws Exception {

		// from Plant table
		Plant plant;
		PlantTranslation pTranslation;
		try {
			ResultSet rs = new SQLStatement("SELECT", "Plant").where("plantID", plantID).generate(connection)
					.executeQuery();

			if (rs.next()) {
				plant = new Plant(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6),
						rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getString(11), rs.getInt(12),
						PlantType.valueOf(rs.getString(13)));

				ResultSet rs2 = new SQLStatement("SELECT", "PlantTranslation").where("plantID", plantID)
						.generate(connection).executeQuery();

				while (rs2.next()) {
					pTranslation = new PlantTranslation(rs2.getInt(1), getLanguage(rs2.getInt(2)), rs2.getString(3),
							rs2.getString(4), rs2.getString(5), rs2.getString(6), rs2.getString(7), rs2.getString(8),
							rs2.getString(9));
					plant.translations.add(pTranslation);
				}
				rs.close();
				rs2.close();
				return plant;

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

//Return Plants according to the name or family name alike	
	public ArrayList<Plant> getPlants(String query, int limit, int page) {
		try {
			int offset = limit * (page - 1);
			PreparedStatement statement = connection.prepare("SELECT DISTINCT plantID FROM PlantTranslation "
					+ "WHERE name LIKE ? OR family LIKE ?" + "LIMIT ? OFFSET ?");
			statement.setString(1, "%" + query + "%");
			statement.setString(2, "%" + query + "%");
			statement.setInt(3, limit);
			statement.setInt(4, offset);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				ArrayList<Plant> plants = new ArrayList<Plant>(0);
				do {
					plants.add(getPlant(rs.getInt(1)));
				} while (rs.next());
				return plants;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	// inserts record into Plant table
	public int createPlant(Plant plant) throws Exception {

		try {

			PreparedStatement statementPlant = new SQLStatement("INSERT", "Plant").set("pH", plant.pH)
					.set("germDays", plant.germDays).set("disPlant", plant.disPlant).set("disRow", plant.disRow)
					.set("pitDepth", plant.pitDepth).set("waterDays", plant.waterDays)
					.set("harvestDays", plant.harvestDays).set("seedBed", plant.seedBed).set("sunlight", plant.sunlight)
					.set("plantImg", plant.plantImg).set("container", plant.container)
					.set("plantType", plant.plantType.name()).generate(connection);

			statementPlant.executeUpdate();

			// get index
			int plantIndex = 0;
			ResultSet resultSet = statementPlant.getGeneratedKeys();

			if (resultSet.next()) {
				plantIndex = resultSet.getInt(1);
				int count = 0;
				for (PlantTranslation translation : plant.translations) {
					count = count + (createPlantTranslation(translation, plantIndex));
				}

				if (count == plant.translations.size()) {
					return resultSet.getInt(1);
				} else {
					new SQLStatement("DELETE", "Plant").where("plantID", plant.plantID).generate(connection)
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
	//Insert into plant translation table
	public int createPlantTranslation(PlantTranslation plantTranslation, int plantID) {

		try {

			int langID;
			if (plantTranslation.language == Language.English)
				langID = 1;
			else
				langID = 2;

			PreparedStatement statementTrans = new SQLStatement("INSERT", "PlantTranslation").set("plantId", plantID)
					.set("languageId", langID).set("name", plantTranslation.name).set("family", plantTranslation.family)
					.set("climate", plantTranslation.climate).set("soil", plantTranslation.soil)
					.set("harvestDes", plantTranslation.harvestDes).set("waterDes", plantTranslation.waterDes)
					.set("makeGrnd", plantTranslation.makeGrnd).generate(connection);
			statementTrans.executeUpdate();

			return 1;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}


	// updates Plant table
	public int updatePlant(Plant plant) throws Exception {
		try {
			int count=0;
			count = count + new SQLStatement("UPDATE","Plant").where("plantID", plant.plantID)
			.set("pH", plant.pH)
			.set("germDays", plant.germDays)
			.set("disPlant", plant.disPlant)
			.set("disRow", plant.disRow)
			.set("pitDepth", plant.pitDepth)
			.set("waterDays", plant.waterDays)
			.set("harvestDays", plant.harvestDays)
			.set("seedBed", plant.seedBed)
			.set("sunlight", plant.sunlight)
			.set("plantImg", plant.plantImg)
			.set("container", plant.container)
			.set("plantType", plant.plantType.name()).generate(connection).executeUpdate();
			
			
			
			int status = new SQLStatement("DELETE", "PlantTranslation")
					.where("plantID", plant.plantID)
					.generate(connection)
					.executeUpdate();
				if(status > 0) {
					for(PlantTranslation translation : plant.translations) {
						count = count + (createPlantTranslation(translation,plant.plantID));
					}
				}
				
				if(count > 0) {
					return 1;
				} else {
					return 0;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
	}


	// deletes record in Plant table
	public int deletePlant(int plantID) {
		try {
			int status=new SQLStatement("DELETE","Plant").where("plantID",plantID).generate(connection).executeUpdate();
		if(status>0){
			return 1;
		}
		else
			return 0;
			
		} catch (Exception e) {
			return 1;
		}
	}
	
	public Boolean deletePTranslation(int plantID, Language language) {
		try {
			int status = new SQLStatement("DELETE", "PlantTranslation")
					.where("plantID", plantID)
					.where("languageID", plantLanguages.get(language))
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
