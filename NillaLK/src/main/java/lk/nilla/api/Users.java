 package lk.nilla.api;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/users/# and api/users/#/devices/#", urlPatterns = "/api/users/*")
public class Users extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json;charset=UTF-8");
		
		String deviceURI = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 1); // devices?
		if(deviceURI.equals("devices")) {
			// if users/#/devices forward to users/iot-devices
			String deviceID = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 2);
			String userID = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 0);
			
			if(deviceID.isEmpty()) { // users/#/devices
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/iot-devices?userID=" + userID);
				rd.forward(request, response);
			} else { // users/#/devices/#
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/iot-devices/" + deviceID + "?userID=" + userID);
				rd.forward(request, response);
			}
		} else if(deviceURI.isEmpty() ) {
			// if users forward to users/user-accounts
			try {
				int userID = Integer.parseInt(RESTAttributes.getSubURLNoNull(request.getPathInfo(), 0));
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/user-accounts/" + userID);
				rd.forward(request, response);
			} catch (Exception e) {
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/user-accounts");
				rd.forward(request, response);
			}
		} else {
			// return 404
			response.setStatus(404);
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json;charset=UTF-8");
		
		String deviceURI = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 1); // devices?
		if(deviceURI.equals("devices")) {
			// if users/#/devices forward to users/iot-devices
			String deviceID = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 2);
			String userID = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 0);
			
			if(deviceID.isEmpty()) { // users/#/devices
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/iot-devices?userID=" + userID);
				rd.forward(request, response);
			} else { // users/#/devices/#
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/iot-devices/" + deviceID + "?userID=" + userID);
				rd.forward(request, response);
			}
		} else if(deviceURI.isEmpty() ) {
			// if users forward to users/user-accounts
			try {
				int userID = Integer.parseInt(RESTAttributes.getSubURLNoNull(request.getPathInfo(), 0));
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/user-accounts/" + userID);
				rd.forward(request, response);
			} catch (Exception e) {
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/user-accounts");
				rd.forward(request, response);
			}
		} else {
			// return 404
			response.setStatus(404);
		}
	}


	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json;charset=UTF-8");
		
		String deviceURI = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 1); // devices?
		if(deviceURI.equals("devices")) {
			// if users/#/devices forward to users/iot-devices
			String deviceID = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 2);
			String userID = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 0);
			
			if(deviceID.isEmpty()) { // users/#/devices
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/iot-devices?userID=" + userID);
				rd.forward(request, response);
			} else { // users/#/devices/#
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/iot-devices/" + deviceID + "?userID=" + userID);
				rd.forward(request, response);
			}
		} else if(deviceURI.isEmpty() ) {
			// if users forward to users/user-accounts
			try {
				int userID = Integer.parseInt(RESTAttributes.getSubURLNoNull(request.getPathInfo(), 0));
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/user-accounts/" + userID);
				rd.forward(request, response);
			} catch (Exception e) {
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/user-accounts");
				rd.forward(request, response);
			}
		} else {
			// return 404
			response.setStatus(404);
		}
	}


	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json;charset=UTF-8");
		
		String deviceURI = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 1); // devices?
		if(deviceURI.equals("devices")) {
			// if users/#/devices forward to users/iot-devices
			String deviceID = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 2);
			String userID = RESTAttributes.getSubURLNoNull(request.getPathInfo(), 0);
			
			if(deviceID.isEmpty()) { // users/#/devices
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/iot-devices?userID=" + userID);
				rd.forward(request, response);
			} else { // users/#/devices/#
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/iot-devices/" + deviceID + "?userID=" + userID);
				rd.forward(request, response);
			}
		} else if(deviceURI.isEmpty() ) {
			// if users forward to users/user-accounts
			try {
				int userID = Integer.parseInt(RESTAttributes.getSubURLNoNull(request.getPathInfo(), 0));
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/user-accounts/" + userID);
				rd.forward(request, response);
			} catch (Exception e) {
				RequestDispatcher rd = request.getRequestDispatcher("/api/users/user-accounts");
				rd.forward(request, response);
			}
		} else {
			// return 404
			response.setStatus(404);
		}
		
	}
	
}
