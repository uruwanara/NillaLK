package lk.nilla.contentManagement;

import lk.nilla.services.Language;

public class PestTranslation {
	
	public int pestID;
	public Language language = null;
	public String pestName = null;
	public String pestDesc = null;
	public String pesticide = null;
	
	public PestTranslation(int pestID, Language language, String pestName, String pestDesc, String pesticide) {
		this.pestID = pestID;
		this.language = language;
		this.pestName = pestName;
		this.pestDesc = pestDesc;
		this.pesticide = pesticide;
	}
	
	public PestTranslation() {
		
	}
}
