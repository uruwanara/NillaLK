package lk.nilla.contentManagement;

import java.sql.Date;

public class Advertisement {
	public int addID = 0;
	public String addCompany = null;
	public String addDetails = null;
	public String addImg = null;
	public Date addDate = null;

	public Advertisement(int addID, String addCompany, String addDetails, String addImg, Date addDate) {
		super();
		this.addID = addID;
		this.addCompany = addCompany;
		this.addDetails = addDetails;
		this.addImg = addImg;
		this.addDate = addDate;
	}
	public Advertisement() { }
}
