package lk.nilla.test;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lk.nilla.accounts.UserType;
import lk.nilla.auth.Authentication;
import lk.nilla.services.RESTAttributes;
import lk.nilla.services.VerificationService;
import lk.nilla.services.VerificationType;

@WebServlet("/test")
public class TestServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// assign the return value
		String res = "none";
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			Authentication auth = new Authentication();
			String password = attributes.parameter("password");
			res = Integer.toString(VerificationService.validatePasswordStrength(password)) + " " + password;
			
			response.getWriter().append(res);
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().append(e.toString());
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// assign the return value
		String res = "";
		
		response.getWriter().append(res);
	}

}
