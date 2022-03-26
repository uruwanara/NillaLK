package lk.nilla.contentManagement;

import java.sql.Date;

public class Article {
	public int articleID = 0;
	public String articleTitle = null;
	public Date articleDate = null;
	public String articleDesc = null;
	public ArticleState articleState = null;
	public String articleImg = null;
	public int userID = 0;

	public Article(int articleID, String articleTitle, Date articleDate, String articleDesc, ArticleState articleState, String articleImg, int userID) {
		super();
		this.articleID = articleID;
		this.articleTitle = articleTitle;
		this.articleDate = articleDate;
		this.articleDesc = articleDesc;
		this.articleState = articleState;
		this.articleImg = articleImg;
		this.userID = userID;
	}
	
	public Article() {
		
	}

}
