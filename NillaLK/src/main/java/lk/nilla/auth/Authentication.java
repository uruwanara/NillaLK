package lk.nilla.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lk.nilla.accounts.User;
import lk.nilla.accounts.UserType;
import lk.nilla.services.DBConnection;

import java.security.Key;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;


/* TODO
 * ***update password with only email (for password reset)
 * */

public class Authentication {

	static private SecretKey jwsSecret;

	// if jwsSecret is not set, set
	public Authentication() throws Exception {

		if (jwsSecret == null) {
			jwsSecret = Keys.hmacShaKeyFor(System.getenv("JWSSigningKey").getBytes("UTF-8"));
		}
		
	}

	/** Generates JWS with given parameters
	 * @param userID
	 * @param type - userType
	 * @param duration - in minutes
	 * @param sjti - JWS id
	 * @param nonce
	 * @param isRefresh
	 * @return
	 */
	public String generateKey(int userID, UserType type, long duration, String sjti, String nonce, Boolean isRefresh) {
		
		Date iat = new Date();
		Date exp = new Date(iat.getTime() + duration * 60 * 1000);
		
		String jws;
		if(isRefresh) {
			jws = Jwts.builder().setIssuer("auth.nilla").setHeaderParam("irt", Boolean.toString(isRefresh))
					.setIssuedAt(iat).setExpiration(exp).setId(sjti).claim("non", nonce)
					.claim("uid", Integer.toString(userID)).claim("utp", type.toString())
					.signWith(jwsSecret).compact();
		} else {
			jws = Jwts.builder().setIssuer("auth.nilla")
					.setIssuedAt(iat).setExpiration(exp).claim("non", nonce)
					.claim("uid", Integer.toString(userID)).claim("utp", type.toString())
					.signWith(jwsSecret).compact();
		}
		
		return jws;
	}
	
	/** A quick one call verification method. Extracts cookie, nonce from request header and calls <b>verify(Cookie cookie, String nonce)</b>
	 * @param request - API request
	 * @return <b>lk.nilla.auth.JwsUser</b> object with userID and userType
	 * @throws ExpiredJwtException
	 * @throws AuthException for any other exception
	 */
	public JwsUser verify(HttpServletRequest request) throws ExpiredJwtException, AuthException {
		
		Cookie cookie = getCookie(request.getCookies(), "jws");
		String nonce = request.getHeader("nonce");
		
		return verify(cookie, nonce);
	}
	
	/** Verify using cookie and nonce
	 * @param cookie - JWS cookie
	 * @param nonce
	 * @return <b>lk.nilla.auth.JwsUser</b> object with userID and userType
	 * @throws ExpiredJwtException
	 * @throws AuthException for any other exception
	 */
	public JwsUser verify(Cookie cookie, String nonce) throws ExpiredJwtException, AuthException {
		
		if(cookie != null) {

			Jws<Claims> claims = verify(cookie.getValue());
			String non = (String) claims.getBody().get("non");
			int uid = Integer.parseInt((String) claims.getBody().get("uid"));
			UserType utp = UserType.valueOf((String) claims.getBody().get("utp"));
			
			if(non.equals(nonce)) {
				return new JwsUser(uid, utp);
			} else {
				throw new AuthException("Nonce not equal");
			}
		} else {
			throw new AuthException("No Cookies");
		}
	}
	
	/**verify and returns claims
	 * @param jwsString
	 * @return - A JWT Claims set.
	 * @throws ExpiredJwtException
	 * @throws AuthException - for any other exception
	 */
	public Jws<Claims> verify(String jwsString) throws ExpiredJwtException, AuthException {

		Jws<Claims> jws;
		try {
			jws = Jwts.parserBuilder().setSigningKey((Key) jwsSecret).build().parseClaimsJws(jwsString);
			return jws;
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			throw new AuthException(e);
		}
	}

	/** creates or updates user password
	 * @param userID
	 * @param password
	 * @throws Exception
	 */
	public void createPassword(int userID, String password) throws Exception {

		try {
			DBConnection connection = new DBConnection();
			PreparedStatement statementCreateVerification = connection
					.prepare("INSERT INTO Credential (userID, salt, hash) " + "VALUES (?,?,?)"
							+ "ON DUPLICATE KEY UPDATE salt = VALUES(salt), hash = VALUES(hash)");

			byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
			mac.init(secretKeySpec);
			byte[] hmacSha256 = mac.doFinal(password.getBytes());

			statementCreateVerification.setInt(1, userID);
			statementCreateVerification.setString(2, Hex.encodeHexString(key));
			statementCreateVerification.setString(3, Hex.encodeHexString(hmacSha256));
			statementCreateVerification.executeUpdate();
			statementCreateVerification.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
			// TODO -- logs --
			throw e;
		}
	}

	/** Returns a User object if email and password is correct. Null if failed
	 * @param email
	 * @param password
	 * @return lk.nilla.accounts.User
	 */
	public User authenticate(String email, String password) {
		
		try {
			String salt = getSalt(email);
			if(salt == null)
				return null;
			
			byte[] byteSalt = Keys.hmacShaKeyFor(Hex.decodeHex(salt)).getEncoded();
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(byteSalt, "HmacSHA256");
			mac.init(secretKeySpec);
			byte[] hmacPass = mac.doFinal(password.getBytes());
			
			DBConnection connection = new DBConnection();
			PreparedStatement statementAuthenticate = connection
					.prepare("SELECT u.* FROM User u, Credential c WHERE c.salt = ? AND c.hash = ? AND u.userID = c.userID");

			statementAuthenticate.setString(1, salt);
			statementAuthenticate.setString(2, Hex.encodeHexString(hmacPass));
			ResultSet rs = statementAuthenticate.executeQuery();
			
			if(rs.next()) {
				User user = new User();
				user.userID = rs.getInt(1);
				user.registered = rs.getTimestamp(2);
				user.email = rs.getString(3);
				user.firstName = rs.getString(4);
				user.lastName = rs.getString(5);
				user.userType = UserType.valueOf(rs.getString(6));
				
				rs.close();
				statementAuthenticate.close(); 
				connection.close();
				return user;
			}
			
			rs.close();
			statementAuthenticate.close(); 
			connection.close();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		}

	/** Returns the salt of the user with given email
	 * @param email
	 * @return String containing salt
	 */
	private String getSalt(String email) {

		try {
			DBConnection connection = new DBConnection();
			PreparedStatement statementGetSalt = connection
					.prepare("SELECT c.salt FROM User u, Credential c WHERE u.userID = c.userID AND u.email = ?");

			statementGetSalt.setString(1, email);
			ResultSet rs = statementGetSalt.executeQuery();

			while (rs.next()) {
				String salt = rs.getString(1);
				rs.close();
				statementGetSalt.close();
				connection.close();
				return salt;
			}

			rs.close();
			statementGetSalt.close();
			connection.close();
			return null;
		} catch (Exception e) {
			// TODO -- logs --
			e.printStackTrace();
			return null;
		}
	}
	
	/** Creates a cookie for JWS verification
	 * @param key
	 * @param val
	 * @param expire
	 * @param path
	 * @return
	 */
	public String createCookie(String key, String val, int expire, String path) {
		
		try {
			Date expdate = new Date(new Date().getTime() + expire * 1000 * 60);
			DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.ROOT);
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			return String.format("%s=%s; Path=%s; Expires=%s; SameSite=strict; HttpOnly", key, val, path, df.format(expdate));
		} catch (Exception e) {
			return null;
		}
		
	}
	
	/** Gets cookie with given name
	 * @param cookies
	 * @param name
	 * @return Cookie
	 */
	public Cookie getCookie(Cookie[] cookies, String name) {
		if(cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if(cookies[i].getName().equals(name)) {
					return cookies[i];
				}
			}
			return null;
		} else {
			return null;
		}
	}
}