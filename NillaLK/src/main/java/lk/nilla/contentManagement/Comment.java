package lk.nilla.contentManagement;

import java.sql.Date;

public class Comment {
	public int commentID = 0;
	public String comment = null;
	public Date commentDate = null;
	public int userID = 0;
	public int articleID = 0;
	
	public Comment(int commentID, String comment, Date commentDate, int userID, int articleID) {
		super();
		this.commentID = commentID;
		this.comment = comment;
		this.commentDate = commentDate;
		this.userID = userID;
		this.articleID = articleID;
		
	}

	public Comment() {

	}

}