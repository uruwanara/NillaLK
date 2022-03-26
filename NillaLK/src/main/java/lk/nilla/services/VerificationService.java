package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class VerificationService {
	
	//create a verification key and send email (overwrites previous key)
	public static int createEmailVerification(int userID, String recipient, String name){
		try {
			DBConnection connection = new DBConnection();
			String key = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
			PreparedStatement statementCreateVerification = connection.prepare("INSERT INTO VerificationKey (userID, type, genKey) "
																+ "VALUES (?,?,?)"
																+ "ON DUPLICATE KEY UPDATE genKey = VALUES(genKey)");
			
			statementCreateVerification.setInt(1, userID);
			statementCreateVerification.setString(2, VerificationType.EMAILVERIFY.toString());
			statementCreateVerification.setString(3, key);
			statementCreateVerification.executeUpdate();
			statementCreateVerification.close();
			connection.close();
			
			EmailService emailService = new EmailService();
			Map<String, String> params = new HashMap<String, String>();
			params.put("name", name);
			params.put("code", key);
			if (emailService.sendMail(recipient, "Verify your email", "EmailVerification", params))
				return 1;
			else
				return 0;
			
		} catch (Exception e) {
			e.printStackTrace();
			//TODO -- write to log --
			return -1;
		}
	}
	
	// verify key
	public static Boolean verifyCode(int userID, VerificationType verifyType, String code) {
		try {
			DBConnection connection = new DBConnection();
			PreparedStatement statementCreateVerification = connection.prepare("DELETE FROM VerificationKey "
																+ "WHERE userID = ? AND type = ? AND genKey = ?");
			
			// DELETE FROM VerificationKey WHERE userID = ? AND type = ? AND genKey = ?
			statementCreateVerification.setInt(1, userID);
			statementCreateVerification.setString(2, VerificationType.EMAILVERIFY.toString());
			statementCreateVerification.setString(3, code);
			statementCreateVerification.executeUpdate();
			int rows = statementCreateVerification.getUpdateCount();
			statementCreateVerification.close();
			connection.close();
			
			if(rows == 0)
				return false;
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	// 
	public static ArrayList<VerificationType> hasVerifications(int userID) throws Exception {
		try {
			DBConnection connection = new DBConnection();
			PreparedStatement statementCreateVerification = connection.prepare("SELECT type FROM VerificationKey "
																+ "WHERE userID = ?");
			statementCreateVerification.setInt(1, userID);
			ResultSet rs = statementCreateVerification.executeQuery();
			
			ArrayList<VerificationType> veriTypes = new ArrayList<VerificationType>(0);
			while(rs.next()) {
				veriTypes.add(VerificationType.valueOf(rs.getString(1)));
			}
			
			statementCreateVerification.close();
			connection.close();
			
			return veriTypes;
		} catch (Exception e) {
			// TODO -- logs --
			e.printStackTrace();
			throw e;
		}
	}

	public static int validatePasswordStrength(String password) {
		
		if(password == null)
			return -1;
		
		if (password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&-+=()])(?=\\S+$).{12,}$")){ //strong
			return 3;
		} else if (password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&-+=()])(?=\\S+$).{8,}$")){ //medium
			return 2;
		} else if (password.matches("^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$")){ //weak
			return 1;
		} else {
		    return 0; // not recommended
		}
	}
	
	public static Boolean validateEmail(String email) {
		try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		      return true;
		} catch (AddressException ex) {
		      return false;
		}
	}
}
