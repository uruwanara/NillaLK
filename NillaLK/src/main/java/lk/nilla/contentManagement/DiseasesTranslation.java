package lk.nilla.contentManagement;

import lk.nilla.services.Language;

public class DiseasesTranslation {
	
	public int diseaseID;
	public Language language;
	public String diseaseName;
	public String diseaseDesc;
	public String treatment;

	public DiseasesTranslation() {}
	
	public DiseasesTranslation(int diseaseID, Language language, String diseaseName, String diseaseDesc, String treatment){
		this.diseaseID = diseaseID;
		this.language = language;
		this.diseaseName = diseaseName;
		this.diseaseDesc = diseaseDesc;
		this.treatment = treatment;
	}

}
