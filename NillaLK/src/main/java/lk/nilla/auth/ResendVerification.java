package lk.nilla.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lk.nilla.accounts.User;
import lk.nilla.services.RESTAttributes;
import lk.nilla.services.VerificationService;

/**
 * Resends verification code
 */
@WebServlet("/auth/resend")
public class ResendVerification extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			RESTAttributes attributes = new RESTAttributes(request);
			String email = attributes.parameter("email");
			String password = attributes.parameter("password");
			
			if(email != null && password != null) {
				Authentication auth = new Authentication();
				User user = auth.authenticate(email, password);
				
				if(user != null) {
					if(VerificationService.createEmailVerification(user.userID, user.email, user.firstName) > 0) {
						response.setStatus(201); //created
					} else {
						response.setStatus(500); //server error
					}
				} else {
					response.setStatus(403); // no user
				}
			} else {
				response.setStatus(406); // parameter error
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500); //server error
		}
	}

}
