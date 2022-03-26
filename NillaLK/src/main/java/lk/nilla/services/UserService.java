package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import lk.nilla.accounts.Admin;
import lk.nilla.accounts.AgriInstructor;
import lk.nilla.accounts.Gardener;
import lk.nilla.accounts.ShopOwner;
import lk.nilla.accounts.User;
import lk.nilla.accounts.UserType;
import lk.nilla.accounts.ZonalAgriHead;
import lk.nilla.contentManagement.Address;

public class UserService {

	private DBConnection connection = new DBConnection();
	
	public User getUser(int userID) {
		User user;
		// Get details in user table
		try {
			PreparedStatement statementGetUser = connection.prepare("SELECT * FROM User WHERE UserID = ?");
			statementGetUser.setInt(1, userID);
			ResultSet rs = statementGetUser.executeQuery();
			if(!rs.next()) {
				rs.close();
				statementGetUser.close();
				return null;
			}
			user = new User(rs.getInt(1), rs.getTimestamp(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(7), UserType.valueOf(rs.getString(6)));
			rs.close();
			statementGetUser.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// Get Details in UserType table
		try {
			PreparedStatement statementGetUserType = connection.prepare("SELECT * FROM " + user.userType + " WHERE userID = ?");
			statementGetUserType.setInt(1, userID);
			ResultSet rs = statementGetUserType.executeQuery();
			
			if(!rs.next()) {
				rs.close();
				statementGetUserType.close();
				return null;
			}
			
			if(user.userType == UserType.Gardener) {
				Gardener temp = new Gardener();
				temp.userID = user.userID;
				temp.email = user.email;
				temp.firstName = user.firstName;
				temp.lastName = user.lastName;
				temp.profilePicture = user.profilePicture;
				temp.userType = user.userType;
				temp.contactNum = rs.getString(2);
				temp.dob = rs.getDate(3);
				temp.address = new Address(rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7));
				user = temp;
			} else if(user.userType == UserType.ShopOwner) {
				ShopOwner temp = new ShopOwner();
				temp.userID = user.userID;
				temp.email = user.email;
				temp.firstName = user.firstName;
				temp.lastName = user.lastName;
				temp.profilePicture = user.profilePicture;
				temp.userType = user.userType;
				temp.contactNum = rs.getString(2);
				user = temp;
			} else if(user.userType == UserType.AgriInstructor) {
				AgriInstructor temp = new AgriInstructor();
				temp.userID = user.userID;
				temp.email = user.email;
				temp.firstName = user.firstName;
				temp.lastName = user.lastName;
				temp.profilePicture = user.profilePicture;
				temp.userType = user.userType;
				temp.nic = rs.getString(2);
				temp.contactNum = rs.getString(3);
				temp.freeSlots = rs.getString(4);
				user = temp;
			} else if(user.userType == UserType.ZonalAgriHead) {
				ZonalAgriHead temp = new ZonalAgriHead();
				temp.userID = user.userID;
				temp.email = user.email;
				temp.firstName = user.firstName;
				temp.lastName = user.lastName;
				temp.profilePicture = user.profilePicture;
				temp.userType = user.userType;
				temp.nic = rs.getString(2);
				temp.contactNum = rs.getString(3);
				temp.departmentID = rs.getInt(4);
				user = temp;
			} else if(user.userType == UserType.Admin) {
				Admin temp = new Admin();
				temp.userID = user.userID;
				temp.email = user.email;
				temp.firstName = user.firstName;
				temp.lastName = user.lastName;
				temp.profilePicture = user.profilePicture;
				temp.userType = user.userType;
				temp.description = rs.getString(2);
				user = temp;
			} else {
				rs.close();
				statementGetUserType.close();
				throw new Exception("Invalid User Type");
			}
			rs.close();
			statementGetUserType.close();
			return user;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// get user details through name (Single and many)
	public ArrayList<User> getUsers(String query, UserType userType, int limit, int offset){
		ArrayList<User> users = new ArrayList<User>(0);
		
		String statementQuery;
		statementQuery = "SELECT * FROM User WHERE concat(fname,\" \",lname,\" \",email) LIKE ?";
		if(userType != null)
			statementQuery = statementQuery + " AND userType=?";
		if(limit > 0)
			statementQuery = statementQuery + " LIMIT ? OFFSET ?";

		try {
			PreparedStatement statementGetUsers = connection.prepare(statementQuery);
			statementGetUsers.setString(1, "%"+query+"%");
			if(userType != null) {
				statementGetUsers.setString(2, userType.toString());
				if(limit > 0) {
					statementGetUsers.setInt(3, limit);
					statementGetUsers.setInt(4, offset);
				}
			} else if(limit > 0) {
				statementGetUsers.setInt(2, limit);
				statementGetUsers.setInt(3, offset);
			}
			
			ResultSet rs = statementGetUsers.executeQuery();
			while(rs.next()) {
				User temp = new User(rs.getInt(1), 
						rs.getTimestamp(2), 
						rs.getString(3), 
						rs.getString(4), 
						rs.getString(5),
						rs.getString(7),
						UserType.valueOf(rs.getString(6)));
				users.add(temp);
			}
			return users;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO -- logs --
			return null;
		}
		
	}

	// Create new user and return email verification key for the user
	public int createUser(User user) {
		
		try {
			//create user in User table
			PreparedStatement statementCreateUser = connection.prepare("INSERT INTO User (email, fname, lname, profilePicture, userType) VALUES (?,?,?,?,?)");
			statementCreateUser.setString(1, user.email);
			statementCreateUser.setString(2, user.firstName);
			statementCreateUser.setString(3, user.lastName);
			statementCreateUser.setString(4, user.profilePicture);
			statementCreateUser.setString(5, user.userType.toString());
			statementCreateUser.executeUpdate();
			
			int userIndex = 0;
			ResultSet rs = statementCreateUser.getGeneratedKeys();
			
			if (rs.next()) {
				userIndex = rs.getInt(1);				
			}
			rs.close();
			statementCreateUser.close();
			return userIndex;

		} catch (SQLException e) {
			// TODO -- write to log --
			e.printStackTrace();
			if (e.getErrorCode() == 1062)
				return -2;
			else
				return -1;
		} catch (Exception e) {
			// TODO -- write to log --
			e.printStackTrace();
			return -1;
		}
	}
	
	// Insert data into UserType table for other details
	public Boolean createUserType(User user){
		try {
			PreparedStatement statementUserType;
			if(user.userType == UserType.Gardener) {
				statementUserType = connection.prepare("INSERT INTO Gardener (contactNum, dob, addLine1, addLine2, city, district, userID) "
						+ "VALUES (?,?,?,?,?,?,?)");
				statementUserType.setString(1, ((Gardener)user).contactNum);
				statementUserType.setDate(2, ((Gardener)user).dob);
				statementUserType.setString(3, ((Gardener)user).address.addLine1);
				statementUserType.setString(4, ((Gardener)user).address.addLine2);
				statementUserType.setString(5, ((Gardener)user).address.city);
				statementUserType.setString(6, ((Gardener)user).address.district);
				statementUserType.setInt(7, user.userID);
			} else if(user.userType == UserType.ShopOwner) {
				statementUserType = connection.prepare("INSERT INTO ShopOwner (contactNum, userID) "
						+ "VALUES (?,?)");
				statementUserType.setString(1, ((ShopOwner)user).contactNum);
				statementUserType.setInt(2, user.userID);
			} else if(user.userType == UserType.AgriInstructor) {
				statementUserType = connection.prepare("INSERT INTO AgriInstructor (nic, contactNum, freeslots, userID) "
						+ "VALUES (?,?,?,?)");
				statementUserType.setString(1, ((AgriInstructor)user).nic);
				statementUserType.setString(2, ((AgriInstructor)user).contactNum);
				statementUserType.setString(3, ((AgriInstructor)user).freeSlots);
				statementUserType.setInt(4, user.userID);
			} else if(user.userType == UserType.ZonalAgriHead) {
				statementUserType = connection.prepare("INSERT INTO ZonalAgriHead (nic, contactNum, departmentID, userID) "
						+ "VALUES (?,?,?,?)");
				statementUserType.setString(1, ((ZonalAgriHead)user).nic);
				statementUserType.setString(2, ((ZonalAgriHead)user).contactNum);
				statementUserType.setInt(3, ((ZonalAgriHead)user).departmentID);
				statementUserType.setInt(4, user.userID);
			} else if(user.userType == UserType.Admin) {
				statementUserType = connection.prepare("INSERT INTO Admin (description, userID) "
						+ "VALUES (?,?)");
				statementUserType.setString(1, ((Admin)user).description);
				statementUserType.setInt(2, user.userID);
			} else {
				throw new Exception("Unknown User Type");
			}
			statementUserType.executeUpdate();
			statementUserType.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//change record of User and UserType at once
	public int changeUpdateUser(User user) {

		try {
			updateUser(user);
		} catch(Exception e) {
			return -1;
		}
		
		try {
			updateUserType(user);
		} catch(Exception e) {
			return -1;
		}		
		return 0;
	}
	
	// update user table only
	public void updateUser(User user){
		try {
			PreparedStatement statementUpdate = connection.prepare("UPDATE User SET email = ?, fname = ?, lname = ?, profilePicture = ? WHERE userID = ?");
			statementUpdate.setString(1, user.email);
			statementUpdate.setString(2, user.firstName);
			statementUpdate.setString(3, user.lastName);
			statementUpdate.setString(4, user.profilePicture);
			statementUpdate.setInt(5, user.userID);
			statementUpdate.executeUpdate();
			statementUpdate.close();
		} catch (Exception e) {
			return;
		}
	}
	
	// update user types table only
	public void updateUserType(User user) throws Exception{
		try {
			PreparedStatement statementUserType;
			if(user.userType == UserType.Gardener) {
				statementUserType = connection.prepare("UPDATE Gardener SET contactNum = ?, dob = ?, addLine1 = ?, addLine2 = ?, city = ?, district = ? WHERE userID = ?");
				statementUserType.setString(1, ((Gardener)user).contactNum);
				statementUserType.setDate(2, ((Gardener)user).dob);
				statementUserType.setString(3, ((Gardener)user).address.addLine1);
				statementUserType.setString(4, ((Gardener)user).address.addLine2);
				statementUserType.setString(5, ((Gardener)user).address.city);
				statementUserType.setString(6, ((Gardener)user).address.district);
				statementUserType.setInt(7, user.userID);
			} else if(user.userType == UserType.ShopOwner) {
				statementUserType = connection.prepare("UPDATE ShopOwner SET contactNum = ? WHERE userID = ?");
				statementUserType.setString(1, ((ShopOwner)user).contactNum);
				statementUserType.setInt(2, user.userID);
			} else if(user.userType == UserType.AgriInstructor) {
				statementUserType = connection.prepare("UPDATE ShopOwner SET nic = ?, contactNum = ?, freeslots = ? WHERE userID = ?");
				statementUserType.setString(1, ((AgriInstructor)user).nic);
				statementUserType.setString(2, ((AgriInstructor)user).contactNum);
				statementUserType.setString(3, ((AgriInstructor)user).freeSlots);
				statementUserType.setInt(4, user.userID);
			} else if(user.userType == UserType.ZonalAgriHead) {
				statementUserType = connection.prepare("UPDATE ShopOwner SET nic = ?, contactNum = ?, departmentID = ? WHERE userID = ?");
				statementUserType.setString(1, ((ZonalAgriHead)user).nic);
				statementUserType.setString(2, ((ZonalAgriHead)user).contactNum);
				statementUserType.setInt(3, ((ZonalAgriHead)user).departmentID);
				statementUserType.setInt(4, user.userID);
			} else if(user.userType == UserType.Admin) {
				statementUserType = connection.prepare("UPDATE ShopOwner SET description = ? WHERE userID = ?");
				statementUserType.setString(1, ((Admin)user).description);
				statementUserType.setInt(2, user.userID);
			} else {
				throw new Exception("Unknown User Type");
			}
			try {
				statementUserType.executeUpdate();
				statementUserType.close();				
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	// delete details in user table and user type table
	public int deleteUser(int userID){
		try {
			PreparedStatement statementDelete = connection.prepare("DELETE * FROM User WHERE UserID = ?");
			statementDelete.setInt(1, userID);
			statementDelete.executeUpdate();
			statementDelete.close();
			return 0;
		} catch (Exception e) {
			return 1;
		}
	}

	public void close() {
		connection.close();
	}
	
}
