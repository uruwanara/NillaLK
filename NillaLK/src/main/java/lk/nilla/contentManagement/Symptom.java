package lk.nilla.contentManagement;

import lk.nilla.services.Language;

public class Symptom {
	
	public int symptomID;
	public Language language;
	public String symptomDesc;

	public Symptom() {}
	
	public Symptom(int symptomID, Language language, String symptomDesc){
		this.symptomID = symptomID;
		this.language = language;
		this.symptomDesc = symptomDesc;
	}

}
