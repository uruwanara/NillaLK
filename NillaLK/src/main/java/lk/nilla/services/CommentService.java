package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.contentManagement.Comment;

public class CommentService {

	private DBConnection connection = new DBConnection();
	
	public Comment getComment(int commentID) throws Exception {

		/** Get comment by commentID
		 * @param commentID
		 * @return Comment object containing data
		 */
		Comment comment;
		try {
			ResultSet rs = new SQLStatement("SELECT", "Comment").where("commentID", commentID).generate(connection).executeQuery();
			
			if(!rs.next()) {
				return null;
			}
			comment = new Comment(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getInt(4), rs.getInt(5));
			rs.close();
			return comment;
		} catch (Exception e) {
			e.printStackTrace();
			return null;	
		}		
	}
	
	/** Get article with following filtering methods.
	 * @param articleID - provide 0 to skip filtering using articleID
	 * @param limit - provide 0 to skip limiting number of articles returned
	 * @param offset - provide 0 to skip offset. Ignored if limit is already 0
	 * @return ArrayList{@literal <}Article{@literal >} - array list of Article. Empty ArrayList (size == 0) if no results
	 */
	public ArrayList<Comment> getComments(int articleID, int limit, int offset){
		ArrayList<Comment> comments = new ArrayList<Comment>(0);
		try {
			SQLStatement stmt = new SQLStatement("SELECT", "Comment");
			
			if(articleID > 0) {
				stmt = stmt.where("articleID", articleID);
			}
			if(limit > 0) {
				stmt = stmt.limit(limit).offset(offset);
			}

			ResultSet rs = stmt.generate(connection).executeQuery();
			
			while(rs.next()) {
				comments.add(new Comment(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getInt(4), rs.getInt(5)));
			}
			
			if(comments.size() > 0) {
				return comments;				
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//create new comment
	public int createComment(Comment comment) throws Exception{
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "Comment")
					.set("comment", comment.comment)
					.set("commentDate", comment.commentDate)
					.set("userID", comment.userID)
					.set("articleID", comment.articleID)
					.generate(connection);
			stmt.executeUpdate();
			
			int index = 0;
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				index = rs.getInt(1);
			}
			rs.close();
			return index;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//update Comment table
	public int updateComment(Comment comment, int userID) throws Exception {
		try {
			SQLStatement stmt = new SQLStatement("UPDATE", "Comment");
			if(comment.comment != null) {
				stmt.set("comment", comment.comment);
			}
			if(comment.commentDate != null) {
				stmt.set("commentDate", comment.commentDate);
			}
			stmt.where("articleID", comment.articleID);
			
			if(userID > 0) {
				stmt.where("userID", userID);
			}
			
			PreparedStatement prepstmt = stmt.generate(connection);
			prepstmt.executeUpdate();
			
			int rows = prepstmt.getUpdateCount();
			return rows;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//delete selected comment form Comment table 
	public int deleteComment(int commentID, int userID) {
		try {
			SQLStatement stmt = new SQLStatement("DELETE", "Comment").where("commentID", commentID);
			if(commentID > 0) {
				stmt = stmt.where("userID", userID);
			}
			PreparedStatement prepstmt = stmt.generate(connection);
			prepstmt.executeUpdate();
			
			int rows = prepstmt.getUpdateCount();
			return rows;			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
