package lk.nilla.auth;

import java.io.IOException;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lk.nilla.accounts.User;
import lk.nilla.services.DBConnection;
import lk.nilla.services.RESTAttributes;
import lk.nilla.services.UserService;

@WebServlet("/auth/revoke/all")
public class RevokeAll extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			RESTAttributes attributes =  new RESTAttributes(request);
			
			int userID = attributes.parameterAsInt("userID");
			String password = attributes.parameterNoNull("password");
			
			if(!password.isEmpty() && userID > 0) {
				
				UserService userService = new UserService();
				User user = userService.getUser(userID);
				
				if(user != null) {
					Authentication auth = new Authentication();
					if(auth.authenticate(user.email, password) != null) {
						
						DBConnection connection = new DBConnection();
						PreparedStatement statementInsert = connection.prepare("INSERT INTO RevokedUserToken (userID) VALUES (?) ON DUPLICATE KEY UPDATE `before` = NOW()");
						statementInsert.setInt(1, userID);
						statementInsert.executeUpdate();
						
						response.setHeader("Set-Cookie", "");
						
						response.setStatus(200); // OK
					} else {
						response.setStatus(403); // forbidden
					}
				} else {
					response.setStatus(404); // no user found
				}
			} else {
				throw new Exception("parameters unacceptable");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(406); // unacceptable
		}
		
	}

}
