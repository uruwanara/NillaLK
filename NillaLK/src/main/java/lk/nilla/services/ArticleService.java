package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.contentManagement.Article;
import lk.nilla.contentManagement.ArticleState;

public class ArticleService {
	
	public ArticleService() {	}
	
	private DBConnection connection = new DBConnection();
	
	/** Get article by articleID
	 * @param articleID
	 * @return Article object containing data
	 */
	public Article getArticle(int articleID) throws Exception{
		
		Article article;
		try {
			ResultSet rs = new SQLStatement("SELECT", "Article").where("articleID", articleID).generate(connection).executeQuery();
			
			if(!rs.next()) {
				return null;				
			}
			article = new Article(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), ArticleState.valueOf(rs.getString(5)), rs.getString(6), rs.getInt(7));
			rs.close();
			return article;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/** Get article with following filtering methods.
	 * @param title - provide null to skip filtering using title
	 * @param userID - provide 0 to skip filtering using userID
	 * @param articleState - provide null to skip filtering using articleState
	 * @param limit - provide 0 to skip limiting number of articles returned
	 * @param offset - provide 0 to skip offset. Ignored if limit is already 0
	 * @return ArrayList{@literal <}Article{@literal >} - array list of Article. Empty ArrayList (size == 0) if no results
	 */
	public ArrayList<Article> getArticles(String title, int userID, String articleState, int limit, int offset){
		ArrayList<Article> articles = new ArrayList<Article>(0);
		try {
			
			SQLStatement stmt = new SQLStatement("SELECT", "Article");
			
			if(title != null)
				stmt = stmt.like("articleTitle", title);
			if(userID > 0)
				stmt = stmt.where("userID", userID);
			if(articleState != null)
				stmt = stmt.where("articleState", articleState);
			if(limit > 0)
				stmt = stmt.limit(limit).offset(offset);
			
			ResultSet rs = stmt.generate(connection).executeQuery(); // assign so it can be used later
			
			while(rs.next()) {
				articles.add(new Article(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), ArticleState.valueOf(rs.getString(5)), rs.getString(6), rs.getInt(7)));
			}			
			return articles;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** creates a new article
	 * @param article - Article object
	 * @return newly created articleID
	 */
	public int createArticle(Article article) throws Exception{
		try {
			
			PreparedStatement stmt = new SQLStatement("INSERT", "Article") // assign so it can be used later
					.set("articleTitle", article.articleTitle)
					.set("articleDate", article.articleDate)
					.set("articleDesc", article.articleDesc)
					.set("articleState", article.articleState.toString())
					.set("articleImg", article.articleImg)
					.set("userID", article.userID)
					.generate(connection);
			stmt.executeUpdate();
			
			// get new article id
			ResultSet rs = stmt.getGeneratedKeys();
			int index = 0;
			if(rs.next())
				index = rs.getInt(1);
			rs.close();
			
			return index;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/** Update article details.
	 * @param article - Article object with updates
	 * @param userID - requesting user's userID. Limits updating only if the article belongs to the given user. Provide 0 if user is an admin to skip checking against userID
	 * @return number of updated rows
	 */
	public int updateArticle(Article article, int userID) {

		try {
			
			SQLStatement stmt = new SQLStatement("UPDATE", "Article");
			if(article.articleTitle != null)
				stmt.set("articleTitle", article.articleTitle);
			if(article.articleDesc != null)
				stmt.set("articleDesc", article.articleDesc);
			if(article.articleState != null)
				stmt.set("articleState", article.articleState.toString());
			if(article.articleImg != null)
				stmt.set("articleImg", article.articleImg);
			stmt = stmt.where("articleID", article.articleID);
			
			// use a userID so non admins can update only their articles
			if(userID > 0)
				stmt = stmt.where("userID", userID);
			
			PreparedStatement prepstmt = stmt.generate(connection); // generate prepared statement (assign so it can be used later)
			prepstmt.executeUpdate();
			
			// get number of affected rows so we can decide whether the update was successful or not (either 0 or 1)
			int rows = prepstmt.getUpdateCount();
			return rows; // 0 if failed
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/** Delete selected article from Article table 
	 * @param articleID
	 * @param userID - requesting user's userID. Limits deleting only if the article belongs to the given user. Provide 0 if user is an admin to skip checking against userID
	 * @return number of deleted articles
	 */
	public int deleteArticle(int articleID, int userID) {
		try {
			// SQL statement logic
			SQLStatement stmt = new SQLStatement("DELETE", "Article").where("articleID", articleID);
			// use a userID so non admins can delete only their articles
			if(userID > 0)
				stmt = stmt.where("userID", userID);
			
			PreparedStatement prepstmt = stmt.generate(connection); // generate prepared statement (assign so it can be used later)
			prepstmt.executeUpdate();
			
			// get number of affected rows so we can decide whether the update was successful or not
			int rows = prepstmt.getUpdateCount();
			return rows; // 0 if failed
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/** Closes DBConnection to the database. 
	 * This closes the connection to the database immediately otherwise it stays open until Java decide to destroy this instance and collect garbage.
	 * <p><b>Do not call if this service is needed in the future! Service is unusable after calling this!!</b>
	 */
	void close() {
		connection.close();
	}

	// Override finalize so the connection can be closed right before this object is destroyed even if we forgot to call the above method :)
	@Override
	protected void finalize() throws Throwable {
		connection.close();
		super.finalize();
	}
	
}
