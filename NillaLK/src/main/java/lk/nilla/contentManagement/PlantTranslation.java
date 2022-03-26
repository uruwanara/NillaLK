package lk.nilla.contentManagement;

import lk.nilla.services.Language;

public class PlantTranslation {
	
	public int plantID;
	public Language language;
	public String name;
	public String family;
	public String climate;
	public String soil;
	public String harvestDes;
	public String waterDes;
	public String makeGrnd;

	public PlantTranslation() {}
	
	public PlantTranslation(int plantID, Language language, String name, String family, String climate, String soil,
			String harvestDes, String waterDes, String makeGrnd) {
		this.plantID = plantID;
		this.language = language;
		this.name = name;
		this.family = family;
		this.climate = climate;
		this.soil = soil;
		this.harvestDes = harvestDes;
		this.waterDes = waterDes;
		this.makeGrnd = makeGrnd;
	}

}
