package lk.nilla.test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class Query {

	//returns given ResultSet as a string
	public static String toString(ResultSet result) {
		try {
			String arr = "";
			ResultSetMetaData rsmd = result.getMetaData();
			try {
				while (result.next()) {
					String col = "";
					for(int i = 1; i <= rsmd.getColumnCount(); i++) {
						col += result.getString(i)  + ", ";
					}
					if (!col.isEmpty())
						arr += col.substring(0, col.length()-2) + "\n";
				}
				if(!arr.isEmpty())
					return arr.substring(0, arr.length()-1);
				else
					return "empty set";
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			return e.toString();
		}
	}
}
