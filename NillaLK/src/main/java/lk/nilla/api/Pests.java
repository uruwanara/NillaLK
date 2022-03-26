package lk.nilla.api;

import java.io.IOException;
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
import lk.nilla.contentManagement.Pest;
import lk.nilla.contentManagement.PestTranslation;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.Language;
import lk.nilla.services.PestService;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/pests/#?", urlPatterns = { "api/pests/*" })
public class Pests extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PestService service = new PestService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int pestID = attributes.subURLAsIntNoNull(0);
			String query = attributes.parameter("query");
			
			if(pestID > 0) {
				Pest pest = service.getPest(pestID);
				Gson gson = new Gson();
				String output = gson.toJson(pest);
				response.setStatus(200);
				response.getWriter().append(output); //write a single pest
			} else if (query != null) {
				int limit = attributes.parameterAsIntNoNull("limit");
				int page = attributes.parameterAsIntNoNull("page");
				
				if(limit <= 0 || page <= 0) {
					limit = 20;
					page = 1;
				}
				
				ArrayList<Pest> pests = service.getPests(query, limit, page);
				Gson gson = new Gson();
				String output = gson.toJson(pests);
				response.setStatus(200);
				response.getWriter().append(output);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PestService service = new PestService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			Gson gson = new Gson();
			int pestID = attributes.subURLAsIntNoNull(0);
			String body = attributes.getBody();
			
			if(user.userType == UserType.Admin) {
				if(body != null && !body.isEmpty()) {//only admin can create a pest
					if(pestID > 0) { // add translation
						PestTranslation translation = gson.fromJson(body, PestTranslation.class);
						if(service.createPestTranslation(translation, pestID) == 1) {
							response.setStatus(200);
							response.getWriter().append(JsonBuilder.single("pestID", pestID));
						} else {
							response.setStatus(406);
						}
					} else {
						Pest pest = gson.fromJson(body, Pest.class);
						int addedID = service.createPest(pest);
						if(addedID > 0) {
							response.setStatus(200);
							response.getWriter().append(JsonBuilder.single("pestID", addedID));
						} else {
							response.setStatus(406);
						}
					}
				} else {
					response.setStatus(406);
				}
			} else {
				response.setStatus(403);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PestService service = new PestService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			Gson gson = new Gson();
			int pestID = attributes.subURLAsIntNoNull(0);
			String body = attributes.getBody();
			
			if(user.userType == UserType.Admin) {
				if(body != null && !body.isEmpty() && pestID > 0) {
					Pest pest = gson.fromJson(body, Pest.class);
					pest.pestID = pestID;
					if(service.updatePest(pest) == 1) {
						response.setStatus(200);
					} else {
						response.setStatus(406);
					}
				} else {
					response.setStatus(406);
				}
			} else {
				response.setStatus(403);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PestService service = new PestService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int pestID = attributes.subURLAsIntNoNull(0);
			String sub2 = attributes.subURLNoNull(1);

			if(user.userType == UserType.Admin) {
				if(pestID > 0) {
					if(sub2.equalsIgnoreCase("translations")) { //delete translation
						Language language = Language.valueOf(attributes.subURLNoNull(2));
						if(service.deleteTranslation(pestID, language)) {
							response.setStatus(200);
						} else {
							response.setStatus(406);
						}
					} else { //delete pest
						if(service.deletePest(pestID)) {
							response.setStatus(200);
						} else {
							response.setStatus(406);
						}
					}
				} else {
					response.setStatus(406);
				}
			} else {
				response.setStatus(403);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
		
	}

}