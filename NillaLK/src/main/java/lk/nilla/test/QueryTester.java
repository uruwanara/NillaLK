package lk.nilla.test;

import java.io.IOException;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lk.nilla.services.DBConnection;
import lk.nilla.services.SQLStatement;
/**
 * Servlet implementation class QueryTester
 */
@WebServlet("/QueryTester")
public class QueryTester extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String resp = "";
		try {
			
			DBConnection conn = new DBConnection();
			
			new SQLStatement().method("DELETE").table("User")
					.where("lName", "lName")
			.generate(conn).executeUpdate();
			
			//resp = Query.toString(rs);
			
		} catch (Exception e) {
			resp = e.toString();
		}
		response.getWriter().append(resp);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String resp = "";
		try {
			DBConnection db = new DBConnection();
			try {
				int ret = db.executeUpdate(request.getParameter("query"));
				if (ret > 0)
					resp = "Successful";
				else
					resp = "Failure";
			} catch (Exception e) {
				throw e;			}
		} catch (Exception e) {
			resp = e.toString();
		}
		response.getWriter().append(resp);
	}

}
