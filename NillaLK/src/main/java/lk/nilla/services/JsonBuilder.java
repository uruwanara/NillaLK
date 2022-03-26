package lk.nilla.services;

import java.util.HashMap;
import java.util.Map;

/** Chaining class to build JSON string from multiple name:value pairs.
 * <p>Calling <b>single(String name, Object value)</b> will return a JSON string with single name:value pair
 */
public class JsonBuilder {

	private Map<String, Object> pairs = new HashMap<String, Object>(0);
	
	/** Adds a name:value pair to the JSON string. Call multiple times (by chaining) to add multiple pairs.
	 * @param name
	 * @param value
	 * @return this
	 */
	public JsonBuilder pair(String name, Object value) {
		pairs.put(name, value);
		return this;
	}
	
	/** Returns a finalized JSON string
	 * @return finalized JSON string
	 */
	public String build() {
		String str = "{";
		boolean first = true;
		for(Map.Entry<String, Object> entry : pairs.entrySet()) {
			if(first) {
				str = str + "\"" + entry.getKey() + "\": \""  + entry.getValue() + "\"";
				first = false;
			} else {
				str = str + ",\"" + entry.getKey() + "\": \""  + entry.getValue() + "\"";
			}
		}
		return str+"}";
	}
	
	/** Returns a JSON string with single name:value pair
	 * @param name
	 * @param value
	 * @return JSON String
	 */
	public static String single(String name, Object value) {
		return "{"
				+ "\"" + name + "\": "  + value
				+ "}";
	}
	
	public static String single(String name, String value) {
		return "{"
				+ "\"" + name + "\": \""  + value + "\""
				+ "}";
	}

}
