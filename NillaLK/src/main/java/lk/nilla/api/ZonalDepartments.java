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

import lk.nilla.contentManagement.ZonalDepartment;
import lk.nilla.services.ZonalDepartmentService;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to /departments/#?", urlPatterns = { "/departments/*" })
public class ZonalDepartments extends HttpServlet  {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ZonalDepartmentService service = new ZonalDepartmentService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		int departmentID;
		
		try {
			departmentID = attributes.subURLAsInt(0);
		} catch (Exception e1) {
			departmentID = -1;
		}
		
		//if request is by departmentID
		if(departmentID != -1) {
			ZonalDepartment department;
			try {
				department = service.getZonalDepartment(departmentID);
				if(department != null) {
					Gson gson = new Gson();
					String output = gson.toJson(department);
					response.getWriter().append(output); //write a single department
				} else {
					response.setStatus(404); //Resource not found. empty results
				}
			} catch (Exception e) {
				response.setStatus(400);
			}

		}
		//if request is by a query 
		else {
			String query = attributes.parameter("query");
			
			//set limit and offset
			int limit, offset;
			try {
				limit = attributes.parameterAsInt("limit");
			} catch (Exception e) {
				limit = 0;
			}
			try {
				offset = attributes.parameterAsInt("offset");
			} catch (Exception e) {
				offset = 0;
			}
			
			//if only query is given
			if(query != null) {
				ArrayList<ZonalDepartment> departments = service.getZonalDepartments(query, limit, offset);
				if(departments != null) {
					Gson gson = new Gson();
					String output = gson.toJson(departments);
					response.getWriter().append(output); //write departments
				} else {
					response.setStatus(404); //Resource not found. empty results
				}
			}
			else {
				response.setStatus(400); //bad request.
			}
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ZonalDepartmentService service = new ZonalDepartmentService();
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			String body = request.getReader().lines().collect(Collectors.joining("\n"));
			Gson gson = new Gson();
			ZonalDepartment department = null;
			
			department = gson.fromJson(body, ZonalDepartment.class);

			int departmentID = service.createZonalDepartment(department);
			
			if(departmentID > 0) {
				response.setStatus(201); // created
				response.getWriter().append("{ \"departmentID\": " + departmentID + "}");
			} else if(departmentID == 0) {
				response.setStatus(400); // bad request (SQL error)
			} else {
				response.setStatus(500); // internal error
			}
		} catch(Exception e) {
			response.setStatus(400);
		}
	}
	

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ZonalDepartmentService service = new ZonalDepartmentService();
		RESTAttributes attribute  = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			ZonalDepartment department = null;
			int departmentID = attribute.subURLAsInt(0);
			
			int status = service.updateZonalDepartment(department, departmentID);
			
			if(status < 0) {
				response.setStatus(400); //bad request (Wrong Query)
			} else if(status == 0) {
				response.setStatus(200); //OK
			}
		} catch (Exception e) {
			response.setStatus(400); // bad Request
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ZonalDepartmentService service = new ZonalDepartmentService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		int departmentID;
		try {
			departmentID = attributes.subURLAsInt(0); 	//check URL value
		} catch (Exception e1) {
			departmentID = -1;
		}
		if(departmentID > 0) {
			int status = service.deleteZonalDepartment(departmentID);
			if(status == 0) {
				response.setStatus(200); //OK
			}else {
				response.setStatus(400); // bad request (Empty departmentID/ wrong SQL syntax)
			}
		} else {
			response.setStatus(400); //bad request (Invalid departmentID)
		}
	}
}

