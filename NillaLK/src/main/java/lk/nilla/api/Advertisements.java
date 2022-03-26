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
import lk.nilla.contentManagement.Advertisement;
import lk.nilla.services.AdvertisementService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/advertisements/#?", urlPatterns = { "/api/advertisements/*" })
public class Advertisements extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AdvertisementService service = new AdvertisementService();
		RESTAttributes attribute = new RESTAttributes(request);

		try {
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			try {
				int addID = attribute.subURLAsInt(0);
				try {
					Advertisement advertisement = service.getAdvertisement(addID);
					if(advertisement != null) {
						if(user.userType == UserType.Admin) { // if owner or admin
							Gson gson = new Gson();
							String output = gson.toJson(advertisement);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().append(output);
						} else if(user.userID > 0){
							Gson gson = new Gson();
							String output = gson.toJson(advertisement);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().append(output);
//							response.setStatus(403); // forbidden
						}
					} else {
						response.setStatus(404); // Resource not found. Empty results
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatus(500); // Server error
				}
			} catch (Exception e) { // else
				String addCompany = attribute.parameterNoNull("addCompany"); // 'No Null' returns an empty string if not provided in the API request.
				if(!addCompany.isEmpty()) {
					addCompany = "%"+addCompany+"%"; // prepare for 'like' queries
				} else {
					addCompany = null; // do not filter by addCompany
				}
				int limit = attribute.parameterAsIntNoNull("limit");
				int offset = attribute.parameterAsIntNoNull("offset");
				
				// get or filter advertisements
				ArrayList<Advertisement> advertisements = null;

				if(user.userType == UserType.Admin) { // if owner or admin
					advertisements = service.getAdvertisements(addCompany, limit, offset);
				} else {
					advertisements = service.getAdvertisements(addCompany, limit, offset);
//					response.setStatus(403); // forbidden
				}
				if(advertisements != null && advertisements.size() > 0) {
					Gson gson = new Gson();
					String output = gson.toJson(advertisements);
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
		AdvertisementService service = new AdvertisementService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// if verification is successful
			
			String body = attributes.getBody();
			Gson gson = new Gson();
			Advertisement advertisement = null;
			advertisement = gson.fromJson(body, Advertisement.class);
			
			if(advertisement != null) {
				if(user.userType == UserType.Admin) {
					advertisement.addDate = Date.valueOf(LocalDate.now());
					int addID = service.createAdvertisement(advertisement);
					if(addID > 0) {
						response.setStatus(201); // created
						response.setContentType("application/json;charset=UTF-8");
						response.getWriter().append(JsonBuilder.single("addID", addID)); // send newly created add id
					} else if(addID == 0) {
						response.setStatus(406); // bad request (invalid commentID, SQL syntax)
					} else {
						response.setStatus(500);
					}					
				} else {
					response.setStatus(403); // forbidden					
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
		AdvertisementService service = new AdvertisementService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			Advertisement advertisement = new Gson().fromJson(attributes.getBody(), Advertisement.class);
			int addID = attributes.subURLAsInt(0); // take addID value through URL
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(addID > 0) {
				if(user.userType == UserType.Admin) { // if admin
					int result = service.updateAdvertisement(advertisement);
					if(result > 0) {
						response.setStatus(200); // OK						
					}
					else if(result == 0) {
						response.setStatus(400); // id error						
					}
					else {
						response.setStatus(500); // server error						
					}
				} else {
					response.setStatus(403); // forbidden
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
		AdvertisementService service = new AdvertisementService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			int addID = attributes.subURLAsInt(0); // take addID value through URL
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(addID > 0) {
				if(user.userType == UserType.Admin) { // if admin
					int result = service.deleteAdvertisement(addID);
					if(result > 0) {
						response.setStatus(200); // OK						
					}
					else if(result == 0) {
						response.setStatus(400); // id error						
					}
					else {
						response.setStatus(500); // server error						
					}
				} else {
					response.setStatus(403); // forbidden
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
}
