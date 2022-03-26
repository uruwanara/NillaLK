package lk.nilla.fileUpload;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import io.jsonwebtoken.ExpiredJwtException;
import lk.nilla.auth.AuthException;
import lk.nilla.auth.Authentication;
import lk.nilla.auth.JwsUser;
import lk.nilla.services.FileService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;

/**
 * Servlet implementation class Upload
 */
@WebServlet(description = "uploads files", urlPatterns = { "/upload" })
@MultipartConfig
public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			String scope = request.getParameter("scope");
			String type = request.getParameter("type");
			Part filePart = request.getPart("file");
			
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			System.out.println("Uploading...");
			System.out.println(scope);
			System.out.println(type);
			
			if(scope != null) {
				
				String path;
				if(scope.equals("public")) {
					path = "content/public/";
				} else if(scope.equals("private")) {
					path = "content/private/" + user.userID + "/";
				} else {
					path = "content/temp/";
				}

				if(type != null) {
					
					String name;
					if(type.equals("profile")) {
						name = "profile";
					} else {
						name = UUID.randomUUID().toString();
					}
					
					String ext = FilenameUtils.getExtension(Paths.get(filePart.getSubmittedFileName()).getFileName().toString());
				    byte[] fileBytes = IOUtils.toByteArray(filePart.getInputStream());
					String fileName = path+name+"."+ext;
					String mimeType = URLConnection.guessContentTypeFromName(fileName);
				    
				    try {
						FileService.uploadObject(fileName, fileBytes, mimeType);
						response.setContentType("application/json;charset=UTF-8");
					    response.setStatus(200); //ok
					    response.getWriter().append(JsonBuilder.single("path", "/files/content" + fileName.substring(7)));
					    
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						response.setStatus(500); //ok
					}
				} else {
					response.setStatus(400); // bad request
				}
			}
		} catch (ExpiredJwtException | AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
	}
}
