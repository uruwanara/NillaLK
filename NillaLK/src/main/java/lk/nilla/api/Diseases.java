package lk.nilla.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
import lk.nilla.contentManagement.DiseasesTranslation;
import lk.nilla.services.DiseasesService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.Language;

import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/diseases/#?", urlPatterns = { "api/diseases/*" })
public class Diseases extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    DiseasesService service = new DiseasesService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int diseaseID = attributes.subURLAsIntNoNull(0);
			String query = attributes.parameter("query");
			
			if(diseaseID > 0) {
				lk.nilla.contentManagement.Diseases disease = service.getDisease(diseaseID);
				Gson gson = new Gson();
				String output = gson.toJson(disease);
				response.setStatus(200);
				response.getWriter().append(output); //write a single disease
			} else if (query != null) {
				int limit = attributes.parameterAsIntNoNull("limit");
				int page = attributes.parameterAsIntNoNull("page");
				
				if(limit <= 0 || page <= 0) {
					limit = 20;
					page = 1;
				}
				
				ArrayList<lk.nilla.contentManagement.Diseases> diseases = service.getDiseases(query, limit, page);
				Gson gson = new Gson();
				String output = gson.toJson(diseases);
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
		
		DiseasesService service = new DiseasesService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			Gson gson = new Gson();
			int diseaseID = attributes.subURLAsIntNoNull(0);
			String body = attributes.getBody();
			
			if(user.userType == UserType.Admin) { // only admin can add disease
				if(body != null && !body.isEmpty()) {
					if(diseaseID > 0) { // add translation of disease
						DiseasesTranslation translation = gson.fromJson(body,DiseasesTranslation.class);
						if(service.addTranslation(diseaseID, translation)) {
							response.setStatus(200);
							response.getWriter().append(JsonBuilder.single("diseaseID", diseaseID));
						} else {
							response.setStatus(406);
						}
					} else {
						lk.nilla.contentManagement.Diseases disease = gson.fromJson(body, lk.nilla.contentManagement.Diseases.class);
						int addedID=service.createDiseases(disease);
						if(addedID > 0) {
							response.setStatus(200);
							response.getWriter().append(JsonBuilder.single("diseaseID", addedID));
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
		
		DiseasesService service = new DiseasesService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");

		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			Gson gson = new Gson();
			int diseaseID = attributes.subURLAsIntNoNull(0);
			String body = attributes.getBody();
			
			if(user.userType == UserType.Admin) { // Only admin can update the disease
				if(body != null && !body.isEmpty() && diseaseID > 0) {
					lk.nilla.contentManagement.Diseases disease = gson.fromJson(body,lk.nilla.contentManagement.Diseases.class);
					disease.diseaseID = diseaseID;
					if(service.updateDiseases(disease)==1) {
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
			e.printStackTrace();
			response.setStatus(500);
		}
	}
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DiseasesService service = new DiseasesService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int diseaseID = attributes.subURLAsIntNoNull(0);
			String sub2 = attributes.subURLNoNull(1);

			if(user.userType == UserType.Admin) {//only admin can delete a disease
				if(diseaseID > 0) {
					if(sub2.equalsIgnoreCase("translations")) { //delete translation
						Language language = Language.valueOf(attributes.subURLNoNull(2));
						if(service.deleteTranslation(diseaseID, language)) {
							response.setStatus(200);
						} else {
							response.setStatus(406);
						}
					} else { //delete product
						if(service.deleteDiseases(diseaseID)) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
		
	}

}
