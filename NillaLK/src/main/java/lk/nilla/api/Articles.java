package lk.nilla.api;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import io.jsonwebtoken.ExpiredJwtException;
import lk.nilla.accounts.UserType;
import lk.nilla.auth.AuthException;
import lk.nilla.auth.Authentication;
import lk.nilla.auth.JwsUser;
import lk.nilla.contentManagement.Article;
import lk.nilla.contentManagement.ArticleState;
import lk.nilla.services.ArticleService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/articles/#?", urlPatterns = { "/api/articles/*" })
public class Articles extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArticleService service = new ArticleService();
		RESTAttributes attribute = new RESTAttributes(request);

		try {
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			try { // try by articleID
				int articleID = attribute.subURLAsInt(0);
				//int articleID = attribute.parameterAsIntNoNull("articleID");
				try {
					Article article = service.getArticle(articleID);
					if(article != null) {
						if(article.articleState == ArticleState.Unpublished) { // if article is unpublished
							if(user.userID == article.userID || user.userType == UserType.Admin) { // if owner or admin
								Gson gson = new Gson();
								String output = gson.toJson(article);
								response.setContentType("application/json;charset=UTF-8");
								response.getWriter().append(output);
							} else {
								response.setStatus(403); // forbidden
							}
						} else {
							Gson gson = new Gson();
							String output = gson.toJson(article);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().append(output); // Return a single article
						}
					} else {
						response.setStatus(404); // Resource not found. Empty results
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatus(500); // Server error
				}
			} catch (Exception e) { // else
				String title = attribute.parameterNoNull("title"); // 'No Null' returns an empty string if not provided in the API request.
				if(!title.isEmpty()) {
					title = "%"+title+"%"; // prepare for 'like' queries
				} else {
					title = null; // do not filter by title
				}
				int userID = attribute.parameterAsIntNoNull("userID"); // 'No Null' returns 0 if not provided in the API request.
				int limit = attribute.parameterAsIntNoNull("limit");
				int offset = attribute.parameterAsIntNoNull("offset");
				String state = attribute.parameter("state");
				
				// get or filter articles
				ArrayList<Article> articles = null;
				if(userID > 0) {
					if(state == null) { // if state not given
						if(user.userID == userID || user.userType == UserType.Admin) { // if owner or admin
							articles = service.getArticles(title, userID, null, limit, offset);
						} else { // else
							articles = service.getArticles(title, userID, "Published", limit, offset); // filter only published articles
						}
					} else if(state.equals("Unpublished")) { // only unpublished articles?
						if(user.userID == userID || user.userType == UserType.Admin) { // if owner or admin
							articles = service.getArticles(title, userID, state, limit, offset);
						} else { // else
							articles = null; // no articles (prohibit non owners from accessing unpublished articles)
						}
					} else if(state.equals("Published")) { //only published articles?
						articles = service.getArticles(title, userID, "Published", limit, offset);
					}
				} else { //if no user id is given
					articles = service.getArticles(title, 0, "Published", limit, offset); // filter only published articles
				}
				
				if(articles != null && articles.size() > 0) {
					Gson gson = new Gson();
					String output = gson.toJson(articles);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().append(output);
				} else {
					response.setStatus(404); //No resources found
				}
			}
		} catch (AuthException | ExpiredJwtException e1) {
			e1.printStackTrace();
			response.setStatus(403); // forbidden
		} catch (Exception e1) {
			e1.printStackTrace();
			response.setStatus(500); // forbidden
		}
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArticleService service = new ArticleService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			String body = attributes.getBody();
			Gson gson = new Gson();
			Article article = null;
			article = gson.fromJson(body, Article.class);
			
			if(article != null) {
				
				article.articleDate = Date.valueOf(LocalDate.now()); // set current date
				article.userID = user.userID; // set article owner to requesting user
				
				int articleID = service.createArticle(article);
				
				if(articleID > 0) {
					response.setStatus(201); // created
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().append(JsonBuilder.single("articleID", articleID)); // send newly created article id
				} else if(articleID == 0) {
					response.setStatus(406); // bad request (failed to create a article. Check body)
				} else {
					response.setStatus(500);
				}
				
			} else {
				response.setStatus(406); // body error
			}
	
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(500); // server error
		}	
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArticleService service = new ArticleService();
		RESTAttributes attributes = new RESTAttributes(request);

		try {
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			// if verification is successful
			String body = attributes.getBody();
			Gson gson = new Gson();
			Article article = null;
			article = gson.fromJson(body, Article.class);
						
			if(article != null && article.articleID > 0) {
				if(user.userType == UserType.Admin) { // if admin
					int result = service.updateArticle(article, 0); // 0 to skip checking against userID
					if(result > 0)
						response.setStatus(200); // OK
					else if(result == 0)
						response.setStatus(400); // body, parameter or id error
					else
						response.setStatus(500); // server error
				} else {
					int result = service.updateArticle(article, user.userID); // 0 to skip checking against userID
					if(result > 0)
						response.setStatus(200); // OK
					else if(result == 0)
						response.setStatus(403); // forbidden (not owner)
					else
						response.setStatus(500); // server error
				}
			} else {
				response.setStatus(400); // body, parameter error
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid article ID)
		}
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		ArticleService service = new ArticleService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			int articleID = attributes.subURLAsInt(0); // take articleID value through URL
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(articleID > 0) {
				int result = service.deleteArticle(articleID, user.userID); // 0 to skip checking against userID
				if(result > 0)
					response.setStatus(200); // OK
				else if(result == 0)
					response.setStatus(403); // forbidden (not owner)
				else
					response.setStatus(500); // server error
			} else {
				response.setStatus(400); // body, parameter error
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid article ID)
		}
	}
}
