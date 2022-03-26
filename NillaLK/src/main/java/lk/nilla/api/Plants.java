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
import lk.nilla.contentManagement.Plant;
import lk.nilla.contentManagement.PlantTranslation;
import lk.nilla.contentManagement.PlantType;
import lk.nilla.services.PlantService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.Language;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/plants/#?", urlPatterns = { "api/plants/*" })
public class Plants extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PlantService service = new PlantService();
		RESTAttributes attribute = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");

		try {

			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			// if verification is successful
			int plantID = attribute.subURLAsIntNoNull(0);
			String query = attribute.parameter("query");

			Plant plant = service.getPlant(plantID);
			if (plantID > 0) {
				// every users can view plant details
				Gson gson = new Gson();
				String output = gson.toJson(plant);
				response.setStatus(200);
				response.getWriter().append(output); // return a single plant
			} else if (query != null) {
				int limit = attribute.parameterAsIntNoNull("limit");
				int page = attribute.parameterAsIntNoNull("page");

				if (limit <= 0 || page <= 0) {
					limit = 20;
					page = 1;
				}

				ArrayList<Plant> plants = service.getPlants(query, limit, page);
				Gson gson = new Gson();
				String output = gson.toJson(plants);
				response.setStatus(200);
				response.getWriter().append(output);
			}

		} catch (AuthException | ExpiredJwtException e1) {
			e1.printStackTrace();
			response.setStatus(403); // forbidden
		} catch (Exception e1) {
			e1.printStackTrace();
			response.setStatus(500); // forbidden
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PlantService service = new PlantService();
		RESTAttributes attributes = new RESTAttributes(request);

		try {
			Authentication auth = new Authentication(); // check weather the user is valid user
			JwsUser user = auth.verify(request);
			response.setContentType("application/json;charset=UTF-8");
			// convert request body into string

			if (user.userType == UserType.Admin) { // only admin can create plantt
				String body = attributes.getBody();
				Gson gson = new Gson();
				Plant plant = null;
				plant = gson.fromJson(body, Plant.class);
				int plantID = attributes.subURLAsIntNoNull(0);

				if (plant != null && !body.isEmpty()) {

					if (plantID > 0) {
						PlantTranslation pTranslation = gson.fromJson(body, PlantTranslation.class);
						if (service.createPlantTranslation(pTranslation, plantID) == 1) {
							response.setStatus(201); // created
							response.getWriter().append(JsonBuilder.single("plantID", plantID)); // send newly created
																									// plant id
						} else {
							response.setStatus(406);
						}
					} else {
						Plant plant1 = gson.fromJson(body, Plant.class);
						int addedID = service.createPlant(plant1);
						if(addedID > 0) {
							response.setStatus(200);
							response.getWriter().append(JsonBuilder.single("plantID", addedID));
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
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(500); // server error
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PlantService service = new PlantService();
		RESTAttributes attributes = new RESTAttributes(request);

		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request); // this will verify the user

			if (user.userType == UserType.Admin) {
				// only admin can edit the details of a plant
				Gson gson = new Gson();
				int plantID = attributes.subURLAsIntNoNull(0);
				String body = attributes.getBody();
				
				if (body != null && !body.isEmpty() && plantID>0) {

					Plant plant =gson.fromJson(body, Plant.class);
					plant.plantID = plantID; // substract the plant ID from the URL

					if(service.updatePlant(plant)==1) {
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
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid plant ID)
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PlantService service = new PlantService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int plantID = attributes.subURLAsIntNoNull(0);
			String sub2 = attributes.subURLNoNull(1);

			if(user.userType == UserType.Admin) { //only admin can delete plants
				if(plantID > 0) {
					if(sub2.equalsIgnoreCase("translations")) { //delete translation
						Language language = Language.valueOf(attributes.subURLNoNull(2));
						if(service.deletePTranslation(plantID, language)) {
							response.setStatus(200);
						} else {
							response.setStatus(406);
						}
					} else { //delete product
						if(service.deletePlant(plantID)==1) {
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
