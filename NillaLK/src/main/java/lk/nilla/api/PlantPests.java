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
import lk.nilla.contentManagement.PlantPest;
import lk.nilla.services.RESTAttributes;
import lk.nilla.services.PlantPestService;

@WebServlet(description = "responds to api/plantpests/#?", urlPatterns = { "/api/plantpests/*" })

public class PlantPests extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PlantPestService service = new PlantPestService();
		RESTAttributes attribute = new RESTAttributes(request);

		try {
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			try { // try by articleID
				int plantID = attribute.subURLAsInt(0); // 'No Null' returns 0 if not provided in the API request.
				int pestID = attribute.subURLAsInt(1);
				try {
					PlantPest plantPest = service.getPlantPest(plantID, pestID);
					if(plantPest != null) {
						Gson gson = new Gson();
						String output = gson.toJson(plantPest);
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
				int pestID = attribute.parameterAsIntNoNull("pestID");
				int limit = attribute.parameterAsIntNoNull("limit");
				int offset = attribute.parameterAsIntNoNull("offset");
			
//				String state = attribute.parameter("state");			
				// get or filter articles
				ArrayList<PlantPest> plantPests = null;
				if(plantID > 0) {
					if(pestID <= 0) { // if pestID not given
						plantPests = service.getPlantPests(plantID, 0, limit, offset);
					} else {
						plantPests = service.getPlantPests(plantID, pestID, limit, offset);
					}
				} else { //if no user id is given
					if(pestID > 0) { // if pestID given
						plantPests = service.getPlantPests(0, pestID, limit, offset);
					} else {
						response.setStatus(404); //No resources found
					}
				}
				
				if(plantPests != null && plantPests.size() > 0) {
					Gson gson = new Gson();
					String output = gson.toJson(plantPests);
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
		PlantPestService service = new PlantPestService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			String body = attributes.getBody();
			Gson gson = new Gson();
			PlantPest plantPest = null;
			plantPest = gson.fromJson(body, PlantPest.class);
			
			if(plantPest != null && (user.userType == UserType.Admin || user.userType == UserType.AgriInstructor)) {
								
				int index = service.createPlantPest(plantPest);
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
		PlantPestService service = new PlantPestService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			PlantPest plantPest = new Gson().fromJson(attributes.getBody(), PlantPest.class);
			int plantID = attributes.subURLAsInt(0); // take articleID value through URL
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(plantPest != null && plantID > 0) {
				if(user.userType == UserType.Admin || user.userType == UserType.AgriInstructor) { // if 
					int result = service.updatePlantPest(plantPest, plantID); // 0 to skip checking against userID
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
		PlantPestService service = new PlantPestService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			int plantID = attributes.subURLAsInt(0); // take plantID value through URL
			int pestID = attributes.subURLAsInt(0); // take pestID value through URL
			
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(pestID > 0 && plantID > 0) {
				if(user.userType == UserType.Admin || user.userType == UserType.AgriInstructor) { // if 
					int result = service.deletePlantPest(pestID, plantID); // 0 to skip checking against userID
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
