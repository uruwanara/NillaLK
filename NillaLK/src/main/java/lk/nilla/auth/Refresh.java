package lk.nilla.auth;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lk.nilla.accounts.UserType;
import lk.nilla.services.DBConnection;


@WebServlet("/auth/refresh")
public class Refresh extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			Authentication auth = new Authentication();
			Cookie crjws = auth.getCookie(request.getCookies(), "rjws");
			
			if(crjws != null) {
				Jws<Claims> claims = auth.verify(crjws.getValue());
				if(claims != null) {
					
					// is refresh token and nonce equal
					if(claims.getHeader().get("irt").equals("true") && claims.getBody().get("non").equals(request.getHeader("nonce"))) {
						
						/* reject refreshing tokens when, 
						 * 		* refresh token is revoked
						 * 		* all older tokens are set to be unacceptable (revoke all)
						 * */
						
						String jti = (String) claims.getBody().get("jti");
						Timestamp exp = new Timestamp(((Number)claims.getBody().get("iat")).longValue() * 1000);
						
						int uid = Integer.parseInt((String) claims.getBody().get("uid"));
						
						DBConnection connection = new DBConnection();
						PreparedStatement statementSelect = connection.prepare("SELECT id FROM RevokedToken WHERE jti = ? "
																				+ "UNION "
																				+ "SELECT UserID AS id FROM RevokedUserToken "
																					+ "WHERE userID = ? AND ? < `before`");
						statementSelect.setString(1, jti);
						statementSelect.setInt(2, uid);
						statementSelect.setTimestamp(3, exp);
						ResultSet rs = statementSelect.executeQuery();
						
						if(!rs.next()) {
							
							UserType utp = UserType.valueOf((String) claims.getBody().get("utp"));
							
							String nonce = Integer.toString(new Random().nextInt(1000));
							int jwsExp = 5;
							int rjwsExp = 60*24*7;
							
							String jws = auth.generateKey(uid, utp, jwsExp, null, nonce, false);
							String rjws = auth.generateKey(uid, utp, rjwsExp, jti, nonce, true);
							
							String jwsString = auth.createCookie("jws", jws, jwsExp, "/");
							String rjwsString = auth.createCookie("rjws", rjws, rjwsExp, "/auth");
							
							response.setHeader("Set-Cookie", jwsString);
							response.addHeader("Set-Cookie", rjwsString);
							
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().append("{"
									+ "\"jws-expire\": \"" + jwsString.split("[=;]")[5] + "\","
									+ "\"rjws-expire\": \"" + rjwsString.split("[=;]")[5] + "\","
									+ "\"userID\": \"" + uid + "\","
									+ "\"userType\": \"" + utp.toString() + "\","
									+ "\"nonce\": \"" + nonce + "\""
									+ "}");
							
							response.setStatus(200); // OK
						} else {
							response.setStatus(403); // revoked
						}
					} else {
						response.setStatus(403); // not a refresh token or nonce verification failed
					}
				} else {
					response.setStatus(403); // no claims / signature failure
				}
			} else {
				response.setStatus(403); // no refresh token
			}
		} catch (ExpiredJwtException e) {
			response.setStatus(403); // refresh token is expired
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500); //server error
		}
		
	}

}
