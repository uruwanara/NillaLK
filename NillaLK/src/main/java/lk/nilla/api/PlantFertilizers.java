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
import lk.nilla.contentManagement.PlantFertilizer;
import lk.nilla.services.RESTAttributes;
import lk.nilla.services.PlantFertilizerService;

@WebServlet(description = "responds to api/plantfertilizers/#?", urlPatterns = { "/api/plantfertilizers/*" })

public class PlantFertilizers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PlantFertilizerService service = new PlantFertilizerService();
		RESTAttributes attribute = new RESTAttributes(request);

		try {
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			try { // try by articleID
				int plantID = attribute.subURLAsInt(0); // 'No Null' returns 0 if not provided in the API request.
				int fertilizerID = attribute.subURLAsInt(1);
				try {
					PlantFertilizer plantFertilizer = service.getPlantFertilizer(plantID, fertilizerID);
					if(plantFertilizer != null) {
						Gson gson = new Gson();
						String output = gson.toJson(plantFertilizer);
						response.setContentType("application/json;charset=UTF-8");
						response.getWriter().append(output); // Return a single article
					} else {
						response.setStatus(404); // Resource not found. Empty results
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatus(500); // Server error
				}
			} catch (Exception e) { // else
/*				String title = attribute.parameterNoNull("title"); // 'No Null' returns an empty string if not provided in the API request.
				if(!title.isEmpty()) {
					title = "%"+title+"%"; // prepare for 'like' queries
				} else {
					title = null; // do not filter by title
				}
*/				int plantID = attribute.parameterAsIntNoNull("plantID"); // 'No Null' returns 0 if not provided in the API request.
				int fertilizerID = attribute.parameterAsIntNoNull("fertilizerID");
				int limit = attribute.parameterAsIntNoNull("limit");
				int offset = attribute.parameterAsIntNoNull("offset");
			
//				String state = attribute.parameter("state");			
				// get or filter articles
				ArrayList<PlantFertilizer> plantFertilizers = null;
				if(plantID > 0) {
					if(fertilizerID <= 0) { // if fertilizerID not given
						plantFertilizers = service.getPlantFertilizers(plantID, 0, limit, offset);
					} else {
						plantFertilizers = service.getPlantFertilizers(plantID, fertilizerID, limit, offset);
					}
				} else { //if no user id is given
					if(fertilizerID > 0) { // if fertilizerID given
						plantFertilizers = service.getPlantFertilizers(0, fertilizerID, limit, offset);
					} else {
						response.setStatus(404); //No resources found
					}
				}
				
				if(plantFertilizers != null && plantFertilizers.size() > 0) {
					Gson gson = new Gson();
					String output = gson.toJson(plantFertilizers);
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
		PlantFertilizerService service = new PlantFertilizerService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			String body = attributes.getBody();
			Gson gson = new Gson();
			PlantFertilizer plantFertilizer = null;
			plantFertilizer = gson.fromJson(body, PlantFertilizer.class);
			
			if(plantFertilizer != null && (user.userType == UserType.Admin || user.userType == UserType.AgriInstructor)) {
								
				int index = service.createPlantFertilizer(plantFertilizer);
				System.out.println(index);
				
				if(index == 1) {
					response.setStatus(201); // created
					response.setContentType("application/json;charset=UTF-8");
//					response.getWriter().append(JsonBuilder.single("articleID", articleID)); // send newly created article id
				} else if(index == 0) {
					response.setStatus(406); // bad request (failed to create a shop product. Check body)
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
		PlantFertilizerService service = new PlantFertilizerService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			PlantFertilizer plantFertilizer = new Gson().fromJson(attributes.getBody(), PlantFertilizer.class);
			int plantID = attributes.subURLAsInt(0); // take articleID value through URL
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(plantFertilizer != null && plantID > 0) {
				if(user.userType == UserType.Admin || user.userType == UserType.AgriInstructor) { // if 
					int result = service.updatePlantFertilizer(plantFertilizer, plantID); // 0 to skip checking against userID
					if(result > 0) {
						response.setStatus(200); // OK
					}
					else if(result == 0) {
						response.setStatus(400); // body, parameter or id error
					}
					else {
						response.setStatus(500); // server error
					}
				}
			} else {
				response.setStatus(400); // body, parameter error
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request
		}
	}	
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PlantFertilizerService service = new PlantFertilizerService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			int plantID = attributes.subURLAsInt(0); // take plantID value through URL
			int fertilizerID = attributes.subURLAsInt(1); // take fertilizerID value through URL
			
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(fertilizerID > 0 && plantID > 0) {
				if(user.userType == UserType.Admin || user.userType == UserType.AgriInstructor) { // if 
					int result = service.deletePlantFertilizer(fertilizerID, plantID); // 0 to skip checking against userID
					if(result > 0) {
						response.setStatus(200); // OK
					}
					else if(result == 0) {
						response.setStatus(400); // body, parameter or id error
					}
					else {
						response.setStatus(500); // server error
					}
				}
			} else {
				response.setStatus(400); // body, parameter error
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request
		}
	}		
}
