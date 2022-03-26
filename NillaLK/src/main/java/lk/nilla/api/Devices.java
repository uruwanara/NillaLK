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
import lk.nilla.accounts.Device;
import lk.nilla.accounts.UserType;
import lk.nilla.auth.AuthException;
import lk.nilla.auth.Authentication;
import lk.nilla.auth.JwsUser;
import lk.nilla.services.IoTService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;

/**
 * Servlet implementation class Devices
 */
@WebServlet(description = "Endpoint for IoT", urlPatterns = { "/iot/*" })
public class Devices extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		IoTService service = new IoTService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			String deviceID = attributes.subURLNoNull(0);
			if(deviceID.length() == 36) {
				
				String channel = attributes.subURLNoNull(1);
				Device info = service.getInfo(deviceID);
				
				if(channel.equalsIgnoreCase("telemetry")) {
					response.getWriter().append(info.telemetry);
				} else if(channel.equalsIgnoreCase("state")) {
					response.getWriter().append(info.state);
				} else {
					response.setStatus(406);
				}
				
			} else if (deviceID.length() == 0) {
				Authentication auth = new Authentication();
				JwsUser user = auth.verify(request);
				
				ArrayList<String> devices = service.getDevices(user.userID);
				
				if(devices != null) {
					Gson gson = new Gson();
					response.getWriter().append(gson.toJson(devices));
				} else {
					response.setStatus(404);
				}
			} else {
				response.setStatus(406);
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
		IoTService service = new IoTService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			String deviceID = attributes.subURLNoNull(0);
			if(deviceID.length() == 36) {
				
				String channel = attributes.subURLNoNull(1);
				String body = attributes.getBody();
				Device info = service.getInfo(deviceID);
				
				if(channel.equalsIgnoreCase("telemetry")) {
					if(service.setTelemetry(deviceID, body).booleanValue()) {
						response.getWriter().append(info.state);
					} else {
						response.setStatus(406);
					}
				} else if(channel.equalsIgnoreCase("state")) {
					if(service.setState(deviceID, body).booleanValue()) {
						response.getWriter().append(info.telemetry);
					} else {
						response.setStatus(406);
					}
				} else {
					response.setStatus(406);
				}
			} else {
				response.setStatus(406);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		IoTService service = new IoTService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			String deviceID = attributes.subURLNoNull(0);
			if(deviceID.length() == 36) {
				Authentication auth = new Authentication();
				JwsUser user = auth.verify(request);
				if(user.userType == UserType.Gardener) {
					if(service.setOwner(deviceID, user.userID).booleanValue()) {
						response.setStatus(200);
						response.getWriter().append(JsonBuilder.single("success", true));
					} else {
						response.setStatus(404);
						response.getWriter().append(JsonBuilder.single("success", false));
					}
				} else {
					response.setStatus(403);
				}
			} else {
				response.setStatus(406);
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
		IoTService service = new IoTService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			String deviceID = attributes.subURLNoNull(0);
			if(deviceID.length() == 36) {
				Authentication auth = new Authentication();
				JwsUser user = auth.verify(request);
				if(service.removeOwner(deviceID, user.userID).booleanValue()) {
					response.setStatus(200);
					response.getWriter().append(JsonBuilder.single("success", true));
				} else {
					response.setStatus(404);
					response.getWriter().append(JsonBuilder.single("success", false));
				}
			} else {
				response.setStatus(406);
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
