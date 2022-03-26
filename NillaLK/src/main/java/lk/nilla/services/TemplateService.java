package lk.nilla.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class TemplateService {

	public String templated;
	
	public TemplateService(String path, Map<String, String> parameters) throws IOException, PatternSyntaxException {
		String temp = readFile(path);
		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			temp = temp.replace("$"+entry.getKey(), entry.getValue());
		}
		templated = temp;
}
	
	static String readFile(String path) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, "UTF-8");
	}

}
