package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import lk.nilla.contentManagement.ZonalDepartment;

public class ZonalDepartmentService {

	private DBConnection connection = new DBConnection();
	
	//display all departments under one article	
	public ZonalDepartment getZonalDepartment(int departmentID) throws Exception{
		
		ZonalDepartment department;
		try {
			PreparedStatement statementGetDepartment = connection.prepare("SELECT * FROM ZonalDepartment WHERE departmentID = ?");
			statementGetDepartment.setInt(1, departmentID);
			statementGetDepartment.executeUpdate();
			ResultSet rs = statementGetDepartment.executeQuery();

			if(!rs.next()) {
				return null;				
			}
			
			department = new ZonalDepartment(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
			rs.close();
			statementGetDepartment.close();
			return department;
		} catch (Exception e) {
			return null;
		}
	}
	
	// get department details through name (Single and many)
	public ArrayList<ZonalDepartment> getZonalDepartments(String zonalname, int limit, int offset){
		ArrayList<ZonalDepartment> departments = new ArrayList<ZonalDepartment>(0);
		
		String query;
		if(limit == 0) {
			query = "SELECT departmentID FROM ZonalDepartment WHERE zonalName LIKE ?";
		} else {
			query = "SELECT departmentID FROM ZonalDepartment WHERE zonalName LIKE ? LIMIT ? OFFSET ?";	
		}

		try {
			PreparedStatement statementGetDepartments = connection.prepare(query);
			statementGetDepartments.setString(1, "%"+zonalname+"%");
			if(limit != 0) {
				statementGetDepartments.setInt(2, limit);
				statementGetDepartments.setInt(3, offset);
			}
			
			ResultSet rs = statementGetDepartments.executeQuery();
			
			while(rs.next()) {
				departments.add(getZonalDepartment(rs.getInt(1)));
			}
			return departments;	
	
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//create new department
	public int createZonalDepartment(ZonalDepartment department) throws Exception{
		try {
			PreparedStatement statementCreate = connection.prepare("INSERT INTO ZonalDepartment (zonalName, contactNum, district) VALUES (?,?,?)");
			statementCreate.setString(1, department.zonalName);
			statementCreate.setString(2, department.contactNum);
			statementCreate.setString(3, department.district);
			statementCreate.executeUpdate();
			
			int index = 0;
			ResultSet rs = statementCreate.getGeneratedKeys();
			if(rs.next()) {
				index = rs.getInt(1);
			}
			rs.close();
			statementCreate.close();
			return index;
			
		} catch (Exception e) {
			return -1;
		}
	}
	
	//update ZonalDepartment table
	public int updateZonalDepartment(ZonalDepartment department, int departmentID) throws Exception{
		try {
			PreparedStatement statementUpdate = connection.prepare("UPDATE ZonalDepartment SET contactNum = ? district = ? WHERE departmentID = ?");
			statementUpdate.setString(1, department.contactNum);
			statementUpdate.setString(2, department.district);
			statementUpdate.setInt(3, departmentID);
			statementUpdate.executeUpdate();
			statementUpdate.close();
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	//delete selected department form ZonalDepartment table 
	public int deleteZonalDepartment(int departmentID) {
		try {
			PreparedStatement statementDelete = connection.prepare("DELETE FROM ZonalDepartment WHERE departmentID= ?");
			statementDelete.setInt(1, departmentID);
			statementDelete.executeUpdate();
			statementDelete.close();
			return 0;
		} catch (Exception e) {
			return 1;
		}
	}
}
