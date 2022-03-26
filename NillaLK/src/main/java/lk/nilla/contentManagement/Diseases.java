package lk.nilla.contentManagement;

import java.util.ArrayList;

public class Diseases {
	public int diseaseID;
	public String diseaseImg;
	public ArrayList<DiseasesTranslation> translations = new ArrayList<DiseasesTranslation>(0);

	public Diseases(int diseaseID, String diseaseImg) {
		this.diseaseID = diseaseID;
		this.diseaseImg = diseaseImg;
	}

	public Diseases() {
		
	}

}