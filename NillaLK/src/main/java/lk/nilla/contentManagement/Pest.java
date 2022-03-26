package lk.nilla.contentManagement;

import java.util.ArrayList;

public class Pest {
	
	public int pestID = 0;
	public String pestImg = null;
	public String pestSciName = null;
	public ArrayList<PestTranslation> translations = new ArrayList<PestTranslation>(0);	

	public Pest(int pestID, String pestImg, String pestSciName) {
		super();
		this.pestID = pestID;
		this.pestImg = pestImg;
		this.pestSciName = pestSciName;
	}

	public Pest() {}
}
