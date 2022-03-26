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
import lk.nilla.contentManagement.Comment;
import lk.nilla.services.CommentService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/comments/#?", urlPatterns = { "api/comments/*" })
public class Comments extends HttpServlet  {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CommentService service = new CommentService();
		RESTAttributes attribute = new RESTAttributes(request);
		
		try {
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// if verification is successful
			
			try { // try by commentID
				System.out.println("H1");
				int commentID = attribute.subURLAsInt(0);
				try {
					Comment comment = service.getComment(commentID);
					if(comment != null) {
						if(user.userID == comment.userID) {
							Gson gson = new Gson();
							String output = gson.toJson(comment);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().append(output); // Return a single comment
						} else {
							response.setStatus(403); // forbidden							
						}
					} else {
						response.setStatus(404); // Resource not found. Empty results
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatus(500); // Server error
				}
			} catch (Exception e) { // else
				System.out.println("H2");

				int articleID = attribute.parameterAsIntNoNull("articleID"); // 'No Null' returns 0 if not provided in the API request.
				int limit = attribute.parameterAsIntNoNull("limit");
				int offset = attribute.parameterAsIntNoNull("offset");
				
				// get or filter articles
				ArrayList<Comment> comments = null;
				if(articleID > 0) {
					comments = service.getComments(articleID, limit, offset);
				} else { //if article id is not given
					response.setStatus(403); // forbidden
				}
				
				if(comments != null && comments.size() > 0) {
					Gson gson = new Gson();
					String output = gson.toJson(comments);
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
		CommentService service = new CommentService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			String body = attributes.getBody();
			Gson gson = new Gson();
			Comment comment = null;
			comment = gson.fromJson(body, Comment.class);

			if(comment != null) {
				comment.commentDate = Date.valueOf(LocalDate.now());
				comment.userID = user.userID;

				int commentID = service.createComment(comment);
				
				if(commentID > 0) {
					response.setStatus(201); // created
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().append(JsonBuilder.single("commentID", commentID));
				} else if(commentID == 0) {
					response.setStatus(400); // bad request (SQL error)
				} else {
					response.setStatus(500); // internal error
				}
			} else {
				response.setStatus(406);				
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(500); // server error
		}	
	}
	

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CommentService service = new CommentService();
		RESTAttributes attributes  = new RESTAttributes(request);
		
		try {
			Comment comment = new Gson().fromJson(attributes.getBody(), Comment.class);
			int commentID = attributes.subURLAsInt(0);
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			if(comment != null && commentID > 0) {
				if(user.userType == UserType.Admin) {
					int result = service.deleteComment(commentID, 0);
					if(result > 0) {
						response.setStatus(200);
					} else if(result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}	
				} else {
					int result = service.deleteComment(commentID, user.userID);
					if(result > 0) {
						response.setStatus(200);
					} else if(result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}	
				}				
			} else {
				response.setStatus(400);
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid shop ID)
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CommentService service = new CommentService();
		RESTAttributes attributes = new RESTAttributes(request);

		try {
			int commentID = attributes.subURLAsInt(0); 	//check URL value
			
			//verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			if(commentID > 0) {
				if(user.userType == UserType.Admin) {
					int result = service.deleteComment(commentID, 0);
					if(result > 0) {
						response.setStatus(200);
					} else if(result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}	
				} else {
					int result = service.deleteComment(commentID, user.userID);
					if(result > 0) {
						response.setStatus(200);
					} else if(result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}	
				}
			} else {
				response.setStatus(400); //bad request (Invalid commentID)
			}
			
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid shop ID)
		}		
	}
}
