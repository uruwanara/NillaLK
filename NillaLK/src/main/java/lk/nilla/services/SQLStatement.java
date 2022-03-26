package lk.nilla.services;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**This is a chaining class which generates prepared SQL statements.
 * Method calling order does not matter except for the <b>generate()</b> which needs to be called at last.
 * <p>Calling <b>generate()</b> will finish chaining methods and return a prepared statement with values being set.
 * <p>A method and a table has to be provided in the <b>constructor</b> or using separate methods, <b>method()</b> and <b>table()</b>
 *  in order to generate the prepared statement.
 *  <hr>
 *  Global object methods (required)<ul>
 *  <dt>constructors<dd>SQLStatement()<dd>SQLStatement(String method, String table)
 *  <dt>method(String method) - does not require if set in the constructor
 *  <dt>table(String table) - does not require if set in the constructor
 *  <dt>generate(DBConnection connection)
 *  </ul>Object methods available for SQL method types:<ul>
 *  <dt>SELECT<dd>where(String column, Object value)<dd>like(String column, String value)<dd>orderBy(String column) - defaults to ASC
 *  			<dd>orderBy(String column, String order)<dd>limit(int limit)<dd>offset(int offset)
 *  <dt>INSERT<dd>set(String column, Object value)
 *  <dt>UPDATE<dd>set(String column, Object value)<dd>where(String column, Object value)<dd>like(String column, String value)
 *  <dt>DELETE<dd>where(String column, Object value)<dd>like(String column, String value)
 *  </ul>Calling a method that is not supported with the given SQL method will not break the execution and is omitted in <b>generate().
 */
public class SQLStatement {
	
	private String method = null;
	private String table = null;
	private Map<String, Object> equalMap = new HashMap<String, Object>(0);
	private Map<String, String> likeMap = new HashMap<String, String>(0);
	private Map<String, Object> setMap = new HashMap<String, Object>(0);
	private String[] orderBy = null;
	private int limit = 0;
	private int offset = 0;
	private int index = 0;
	private PreparedStatement stmt = null;
	
	/** Initializes a new SQLStatement.
	 */
	public SQLStatement() {}
	
	/** Initializes a new SQLStatement with provided method and table.
	 * @param method - name of the method. Supports: SELECT, UPDATE, INSERT, DELETE
	 * @param table - name of the table statement is for
	 */
	public SQLStatement(String method, String table) {
		this.method = method;
		this.table = "`"+table+"`";
	}
	
	/** Sets statement type.
	 * @param method - name of the method. Supports: SELECT, UPDATE, INSERT, DELETE
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement method(String method) {
		this.method = method;
		return this;
	}
	
	/** Sets table the statement uses.
	 * @param table - name of the table statement is for
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement table(String table) {
		this.table = "`"+table+"`";
		return this;
	}
	
	/** Adds an element in the where clause 
	 * @param column - name of the column
	 * @param value - value of the column
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement where(String column, Object value) {
		equalMap.put("`"+column+"`", value);
		return this;
	}
	
	/** Adds an element in the where clause as like
	 * @param column - name of the column
	 * @param value - value of the column in like format. eg: %Ni%
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement like(String column, String value) {
		likeMap.put("`"+column+"`", value);
		return this;
	}
	
	/** Sets the value for the given column in INSERT and UPDATE queries
	 * @param column - name of the column
	 * @param value - value of the column
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement set(String column, Object value) {
		setMap.put("`"+column+"`", value);
		return this;
	}
	
	/** Creates an order by clause for given column and order
	 * @param column - name of the column
	 * @param order - either "ASC" or "DESC"
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement orderBy(String column, String order) {
		String[] temp = {"`"+column+"`", order};
		orderBy = temp;
		return this;
	}
	
	/** Creates an order by clause for given column. Defaults to "ASC"
	 * @param column - name of the column
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement orderBy(String column) {
		String[] temp = {"`"+column+"`", "ASC"};
		orderBy = temp;
		return this;
	}
	
	/** Adds a limit clause with given limit
	 * @param limit - amount to limit
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement limit(int limit) {
		this.limit = limit;
		return this;
	}
	
	/** Adds a offset clause with given offset. <b>Omitted if no limit is given</b>
	 * @param offset - amount to offset
	 * @return this (lk.nilla.services.SQLStatement)
	 */
	public SQLStatement offset(int offset) {
		this.offset = offset;
		return this;
	}
	
	// Statement generators
	
	private String generateWhere() {
		String substr = " WHERE true";
		if(equalMap.size() > 0) {
			for(String item : equalMap.keySet()) {
				substr = substr + " AND " + item + " = ?";
			}
		}
		if(likeMap.size() > 0) {
			for(String item : likeMap.keySet()) {
				substr = substr + " AND " + item + " LIKE ?";
			}
		}
		return substr;
	}
	
	private String generateInsert() {
		String substr = "";
		if(setMap.size() > 0) {
			substr = " (";
			
			boolean first = true;
			for(String item : setMap.keySet()) {
				if(first)
					substr = substr + item;
				else
					substr = substr + ", " + item;
				first = false;
			}
			substr = substr + ") VALUES (";
			
			first = true;
			for(int i=0; i < setMap.size(); i++) {
				if(first)
					substr = substr + "?";
				else
					substr = substr + ", ?";
				first = false;
			}
			substr = substr + ")";
		}
		return substr;
	}
	
	private String generateUpdate() {
		String substr = " SET";
		if(setMap.size() > 0) {
			boolean first = true;
			for(String item : setMap.keySet()) {
				if(first)
					substr = substr + " " + item + " = ?";
				else
					substr = substr + ", " + item + " = ?";
				first = false;
			}
		}
		return substr;
	}
	
	private String generateOrderBy() {
		if(orderBy != null) {
			return " ORDER BY " + orderBy[0] + " " + orderBy[1];
		}
		return "";
	}
	
	private String generateLimit() {
		if(limit > 0) {
			return " LIMIT ? OFFSET ?";
		}
		return "";
	}
	
	// Statement setters
	
	private void setWhere() throws Exception {
		if(equalMap.size() > 0) {
			for(Object item : equalMap.values()) {
				setValue(item);
			}
		}
		if(likeMap.size() > 0) {
			for(Object item : likeMap.values()) {
				setValue(item);
			}
		}
	}
	
	private void setValues() throws Exception {
		if(setMap.size() > 0) {
			for(Object item : setMap.values()) {
				setValue(item);
			}
		}
	}
	
	private void setLimit() throws Exception {
		if(limit > 0) {
			setValue(limit);
			setValue(offset);
		}
	}
	
	// sets prepared statement values with the right object type
	private void setValue(Object item) throws Exception {
		index++;
		if(item instanceof Integer) {
			stmt.setInt(index, ((Integer) item).intValue());
		} else if(item instanceof Float) {
			stmt.setFloat(index, ((Integer) item).floatValue());
		} else if(item instanceof Double) {
			stmt.setDouble(index, ((Double) item).doubleValue());
		} else if(item instanceof Byte) {
			stmt.setByte(index, ((Byte) item).byteValue());
		} else if(item instanceof String) {
			stmt.setString(index, ((String) item));
		} else if(item instanceof Date) {
			stmt.setDate(index, ((Date) item));
		} else if(item instanceof Timestamp) {
			stmt.setTimestamp(index, ((Timestamp) item));
		}
	}
	
	/** Generates a prepared statement
	 * @param connection - DBConnection instance to use
	 * @return java.sql.PreparedStatement
	 */
	public PreparedStatement generate(DBConnection connection) throws Exception {
		
		String statement = null;
		if(method != null && table != null) {
			if(method.equalsIgnoreCase("SELECT")) {
				statement = "SELECT * FROM " + table + generateWhere() + generateOrderBy() + generateLimit();
				stmt = connection.prepare(statement);
				setWhere();
				setLimit();
				return stmt;
			} else if(method.equalsIgnoreCase("INSERT")) {
				statement = "INSERT INTO " + table + generateInsert();
				stmt = connection.prepare(statement);
				setValues();
				return stmt;
			} else if(method.equalsIgnoreCase("UPDATE")) {
				statement = "UPDATE " + table + generateUpdate() + generateWhere();
				stmt = connection.prepare(statement);
				setValues();
				setWhere();
				return stmt;
			} else if(method.equalsIgnoreCase("DELETE")) {
				statement = "DELETE FROM " + table + generateWhere();
				stmt = connection.prepare(statement);
				setWhere();
				return stmt;
			} else {
				throw new Exception("Method " + method + " not allowed");
			}
		} else {
			throw new Exception("Either method or table is not given");
		}
		
	}
	
}
