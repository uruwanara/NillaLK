package lk.nilla.contentManagement;

import java.util.ArrayList;

public class Plant {
	public int plantID;
	public String pH;
	public int germDays;
	public int disPlant;
	public int disRow;
	public int pitDepth;
	public int waterDays;
	public int harvestDays;
	public int seedBed;
	public int sunlight;
	public String plantImg;
	public int container;
	public PlantType plantType;
	public ArrayList<PlantTranslation> translations = new ArrayList<PlantTranslation>(0);

	public Plant(int plantID, String pH, int germDays, int disPlant, int disRow, int pitDepth, int waterDays,
			int harvestDays, int seedBed, int sunlight, String plantImg, int container, PlantType plantType) {
		this.plantID = plantID;
		this.pH = pH;
		this.germDays = germDays;
		this.disPlant = disPlant;
		this.disRow = disRow;
		this.pitDepth = pitDepth;
		this.waterDays = waterDays;
		this.harvestDays = harvestDays;
		this.seedBed = seedBed;
		this.sunlight = sunlight;
		this.plantImg = plantImg;
		this.container = container;
		this.plantType = plantType;
	}

	public Plant() {
		
	}

}
