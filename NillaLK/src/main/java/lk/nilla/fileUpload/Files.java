package lk.nilla.fileUpload;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lk.nilla.services.FileService;
/**
 * Servlet implementation class Upload
 */
@WebServlet(description = "returns files", urlPatterns = { "/files/*" })
public class Files extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		URL fileURL = FileService.getObjectURI(request.getPathInfo().substring(1));
		response.sendRedirect(fileURL.toString());
	
	}

}