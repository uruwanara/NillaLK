package lk.nilla.api;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class NotFound
 * Simply returns 404
 */
@WebServlet(description = "Direct URIs that are not found here (404 helper)", urlPatterns = { "/404" })
public class NotFound extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setStatus(404);
	}

}
