package lk.nilla.auth;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lk.nilla.services.DBConnection;

@WebServlet("/auth/revoke")
public class Revoke extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			Authentication auth = new Authentication();
			Cookie crjws = auth.getCookie(request.getCookies(), "rjws");
			
			Jws<Claims> claims = auth.verify(crjws.getValue());
			if(claims != null) {
				
				if(claims.getHeader().get("irt") != null) {
					
					String jti = (String) claims.getBody().get("jti");
					Timestamp exp = new Timestamp(((Number)claims.getBody().get("exp")).longValue() * 1000);
					
					DBConnection connection = new DBConnection();
					PreparedStatement statementInsert = connection.prepare("INSERT INTO RevokedToken (jti, exp) VALUES (?,?)");
					statementInsert.setString(1, jti);
					statementInsert.setTimestamp(2, exp);
					statementInsert.executeUpdate();
					
					response.setHeader("Set-Cookie", "");
					
					response.setStatus(200); // OK
				}
			} else {
				response.setStatus(403); // Not allowed to (the irony)
			}
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			response.setStatus(403); // Not allowed to (the irony. again?)
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500); // server error
		}
	}

}
