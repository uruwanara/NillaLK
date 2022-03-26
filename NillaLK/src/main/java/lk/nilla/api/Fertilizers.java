package lk.nilla.api;

import java.io.IOException;

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
import lk.nilla.contentManagement.Fertilizer;
import lk.nilla.services.FertilizerService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;


@WebServlet(description = "responds to api/fertilizers/#?", urlPatterns = { "api/fertilizers/*" })
public class Fertilizers extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		FertilizerService service = new FertilizerService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");

		try {
			// to verify the user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			int fertiID = attributes.subURLAsIntNoNull(0);
			String query = attributes.parameter("query");

			if (fertiID > 0) {
				Fertilizer fertilizer = service.getFerti(fertiID);
				Gson gson = new Gson();
				String output = gson.toJson(fertilizer);
				response.setStatus(200);
				response.getWriter().append(output); // write a single fertilizer
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		FertilizerService service = new FertilizerService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");

		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			Gson gson = new Gson();
			String body = attributes.getBody();
			Fertilizer fertilizer = null;
			fertilizer = gson.fromJson(body, Fertilizer.class);

			if (user.userType == UserType.Admin) { // Only admin can add new Ferlizers to the system
				if (fertilizer != null) {
					int fertiID = service.createFerti(fertilizer);

					if (fertiID > 0) {
						response.setStatus(201); // created
						response.setContentType("application/json;charset=UTF-8");
						response.getWriter().append(JsonBuilder.single("fertiID", fertiID)); // send newly created
																								// fertiID

					} else if (fertiID == 0) {
						response.setStatus(406); // bad request
					} else {
						response.setStatus(500);
					}

				}
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		FertilizerService service = new FertilizerService();
		RESTAttributes attributes = new RESTAttributes(request);

		try {
			Fertilizer fertilizer = new Gson().fromJson(attributes.getBody(), Fertilizer.class);
			int fertiID = attributes.subURLAsInt(0);

			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			if (fertilizer != null && fertiID > 0) {
				if (user.userType == UserType.Admin) { // only admin can update ferilizer details
					int result = service.updateFerti(fertilizer);
					if (result > 0)
						response.setStatus(200); // OK
					else if (result == 0)
						response.setStatus(400); // body, parameter or id error
					else
						response.setStatus(500); // server error
				}

			} else {
				response.setStatus(400); // body, parameter error
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request wrong fertiID
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		FertilizerService service = new FertilizerService();
		RESTAttributes attribute = new RESTAttributes(request);

		try {
			int fertiID = attribute.subURLAsInt(0);

			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			if (fertiID > 0) {
				if (user.userType == UserType.Admin) {
					int result = service.deleteFerti(fertiID);
					if (result > 0) {
						response.setStatus(200);
					} else if (result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}
				} else {
					response.setStatus(400); // bad request

				}
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request
		}

	}
}