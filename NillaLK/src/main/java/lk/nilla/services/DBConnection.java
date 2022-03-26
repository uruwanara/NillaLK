package lk.nilla.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class DBConnection {

	//stores DB connection parameters
	final private String host = "localhost:3306";
	final private String user = "app-engine";
	final private String password = "app-engine-password";
	final private String database = "nillalk";
	
	//stores connection, statement and result
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
		
	//setups DB connection at the initialization of this object
	public DBConnection(){
		try {
			connect();
		} catch (Exception e) {}
	}
	
	//initialize connection
	private void connect() throws Exception {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?" + "user=" + user + "&password=" + password);
		} catch (Exception e) {
			throw e;
		}
	}
	
	//returns a prepared statement from the given query to execute separately; returns Generated Keys
	public PreparedStatement prepare(String query) throws Exception{
		try {
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			return preparedStatement;
		} catch (Exception e) {
			throw e;
		}
	}
	
	//executes the passed query on DB server (read only)
	public ResultSet executeQuery(String query) throws Exception {
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			return resultSet;
		} catch (Exception e) {
			throw e;
		}
	}
	
	//executes the passed query on DB server (create, update and delete only)
	public int executeUpdate(String query) throws Exception {
		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);
			resultSet = statement.getGeneratedKeys();
			
			int result = 0;
			if(resultSet.next()) {
				result = resultSet.getInt(1);
			}
			
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
	
	//close DB connection, statement and result set
	public void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
