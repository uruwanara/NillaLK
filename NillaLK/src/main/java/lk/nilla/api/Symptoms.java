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

import lk.nilla.contentManagement.Symptom;
import lk.nilla.services.SymptomService;
import lk.nilla.services.JsonBuilder;

import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/symptoms/#?", urlPatterns = { "api/symptoms/*" })
public class Symptoms extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SymptomService service = new SymptomService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int symptomID = attributes.subURLAsIntNoNull(0);
			String query = attributes.parameter("query");
			
			if(symptomID > 0) {
				Symptom symptom = service.getSymptom(symptomID);
				Gson gson = new Gson();
				String output = gson.toJson(symptom);
				response.setStatus(200);
				response.getWriter().append(output); //write a single symptom
			} else if (query != null) {
				int limit = attributes.parameterAsIntNoNull("limit");
				int page = attributes.parameterAsIntNoNull("page");
				
				if(limit <= 0 || page <= 0) {
					limit = 20;
					page = 1;
				}
				
				ArrayList<Symptom> symptoms = service.getSymptoms(query, limit, page);
				Gson gson = new Gson();
				String output = gson.toJson(symptoms);
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
		
		SymptomService service = new SymptomService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			Gson gson = new Gson();
			//int symptomID = attributes.subURLAsIntNoNull(0);
			String body = attributes.getBody();
			
			if(user.userType == UserType.Admin) { //only Admin can create a symptom
				if(body != null && !body.isEmpty()) {
						Symptom symptom = gson.fromJson(body, Symptom.class);
						int addedID = service.createSymptom(symptom);
						if(addedID > 0) {
							response.setStatus(200);
							response.getWriter().append(JsonBuilder.single("symptomID", addedID));
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


	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SymptomService service = new SymptomService();
		RESTAttributes attributes = new RESTAttributes(request);
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			Gson gson = new Gson();
			int symptomID = attributes.subURLAsIntNoNull(0);
			String body = attributes.getBody();
			
			if(user.userType == UserType.Admin) { //Only admin can edit details of symptoms
				if(body != null && !body.isEmpty() && symptomID > 0) {
					Symptom symptom = gson.fromJson(body, Symptom.class);
					symptom.symptomID = symptomID;
					if(service.updateSymptom(symptom)==1) {
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
		SymptomService service = new SymptomService();
		RESTAttributes attributes = new RESTAttributes(request);
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int symptomID = attributes.subURLAsIntNoNull(0);
			String sub2 = attributes.subURLNoNull(1);

			if(user.userType == UserType.Admin) {
				//delete product
					if(service.deleteSymptom(symptomID)==1) {
						response.setStatus(200);
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
