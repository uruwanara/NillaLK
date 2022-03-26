package lk.nilla.services;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

/*
 * This class is responsible for providing sub URLs and validating/escaping request parameters/body
 * 
 * ESCAPING AND SANATIZING ARE NOT YET DONE!!!!!!!!!!!!!!!!!!!!!!!
 */

public class RESTAttributes {
	
	Map<String, String[]> parameters;
	ArrayList<String> subURLs = new ArrayList<String>(0);
	String body;

	public RESTAttributes(HttpServletRequest request) {
		try {
			
			body = request.getReader().lines().collect(Collectors.joining("\n"));
			
			String path = request.getPathInfo();
			if(path != null) {
				String urls[] = path.substring(1).split("/");
				for(int i = 0; i < urls.length; i++) {
					subURLs.add(urls[i]);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		parameters = request.getParameterMap();
		
	}
	
	public static String getSubURL(String path, int index) {
		
		if(path != null) {
			String urls[] = path.substring(1).split("/");
					
			if(urls.length > index) {
				return urls[index];
			} else {
				return null;
			}
		} else {
			return null;
		}
		
	}
	
	public static String getSubURLNoNull(String path, int index) {
		
		if(path != null) {
			String urls[] = path.substring(1).split("/");
					
			if(urls.length > index) {
				return urls[index];
			} else {
				return "";
			}
		} else {
			return "";
		}

	}
	
	//returns parameter as a string. Null if does not exist
	public String parameter(String name) {
		try {
			return parameters.get(name)[0];
		} catch (Exception e) {
			return null;
		}
	}
	
	//returns parameter as a string. Null if does not exist
	public String parameterNoNull(String name) {
			try {
				return parameters.get(name)[0];
			} catch (Exception e) {
				return "";
			}
		}
	
	//returns parameter as an Integer.
	public int parameterAsInt(String name) throws NumberFormatException {
		try {
			return Integer.parseInt(parameter(name));
		} catch (Exception e) {
			throw e;
		}
	}
	
	//returns parameter as an Integer.
	public int parameterAsIntNoNull(String name){
		try {
			return Integer.parseInt(parameter(name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	//returns subURL as a string. Null if does not exist
	public String subURL(int index) {
		try {
			return subURLs.get(index);
		} catch (Exception e) {
			return null;
		}
	}
	
	//returns subURL as a string. Null if does not exist
	public String subURLNoNull(int index) {
		try {
			return subURLs.get(index);
		} catch (Exception e) {
			return "";
		}
	}
	
	//returns subURL as an Integer. Throws exception at failure
	public int subURLAsInt(int index) throws Exception{
		try {
			return Integer.parseInt(subURL(index));
		} catch (Exception e) {
			throw e;
		}
	}
	
	//returns subURL as an Integer. Throws exception at failure
		public int subURLAsIntNoNull(int index) {
			try {
				return Integer.parseInt(subURL(index));
			} catch (Exception e) {
				return 0;
			}
		}
	
	public String getBody() {
		return body;
	}

}
