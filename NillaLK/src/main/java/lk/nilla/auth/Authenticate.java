package lk.nilla.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import lk.nilla.accounts.User;
import lk.nilla.api.ErrorReason;
import lk.nilla.api.ErrorReasonWithUserType;
import lk.nilla.api.ErrorType;
import lk.nilla.services.RESTAttributes;
import lk.nilla.services.VerificationService;
import lk.nilla.services.VerificationType;

// send user credentials here and get a tooooooooooooooooooooken
@WebServlet("/auth")
public class Authenticate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RESTAttributes attributes = new RESTAttributes(request);
		String email = attributes.parameterNoNull("email");
		String password = attributes.parameterNoNull("password");
		response.setContentType("application/json;charset=UTF-8");
		
		if(!email.isEmpty() && !password.isEmpty()) {
			try {
				Authentication auth = new Authentication();
				User user = auth.authenticate(email, password);
				if(user != null) {
					
					ArrayList<VerificationType> verifications = VerificationService.hasVerifications(user.userID);
					
					if(!verifications.contains(VerificationType.EMAILVERIFY)) {
						
						String nonce = Integer.toString(new Random().nextInt(1000));
						String jti = UUID.randomUUID().toString();
						int jwsExp = 5;
						int rjwsExp = 60*24*7;
						
						String jws = auth.generateKey(user.userID, user.userType, jwsExp, null, nonce, false);
						String rjws = auth.generateKey(user.userID, user.userType, rjwsExp, jti, nonce, true);
						
						String jwsString = auth.createCookie("jws", jws, jwsExp, "/");
						String rjwsString = auth.createCookie("rjws", rjws, rjwsExp, "/auth");
						
						// delete existing cookies
						Cookie[] cookies = request.getCookies();
						if(cookies != null) {
							for (Cookie cookie : cookies) {
					            cookie.setMaxAge(0);
					            response.addCookie(cookie);
							}
						}
						
						response.setHeader("Set-Cookie", jwsString);
						response.addHeader("Set-Cookie", rjwsString);
						
						response.setContentType("application/json;charset=UTF-8");
						response.getWriter().append("{"
								+ "\"jws-expire\": \"" + jwsString.split("[=;]")[5] + "\","
								+ "\"rjws-expire\": \"" + rjwsString.split("[=;]")[5] + "\","
								+ "\"userID\": \"" + user.userID + "\","
								+ "\"userType\": \"" + user.userType.toString() + "\","
								+ "\"nonce\": \"" + nonce + "\""
								+ "}");
						
						response.setStatus(200); // OK
						
					} else {
						response.setStatus(403); // forbidden, verify email
						response.getWriter().append(new Gson().toJson(new ErrorReasonWithUserType(ErrorType.EmailNotVerified, user.userType)));
					}
				} else {
					response.setStatus(403); // forbidden, credential error
					response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.CredentialError)));
				}
			} catch (Exception e) {
				response.setStatus(500); // server error, signing key acquiring failed 
			}
		} else {
			response.setStatus(406); // parameter error
			response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.BadParameters)));
		}
	}

}
