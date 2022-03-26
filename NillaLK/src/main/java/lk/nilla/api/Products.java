package lk.nilla.api;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import io.jsonwebtoken.ExpiredJwtException;
import lk.nilla.accounts.UserType;
import lk.nilla.auth.AuthException;
import lk.nilla.auth.Authentication;
import lk.nilla.auth.JwsUser;
import lk.nilla.contentManagement.Product;
import lk.nilla.contentManagement.ProductTranslation;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.Language;
import lk.nilla.services.ProductService;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/products/#?", urlPatterns = { "api/products/*" })
public class Products extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int productID = attributes.subURLAsIntNoNull(0);
			String query = attributes.parameter("query");
			
			if(productID > 0) {
				Product product = service.getProduct(productID);
				Gson gson = new Gson();
				String output = gson.toJson(product);
				response.setStatus(200);
				response.getWriter().append(output); //write a single product
			} else if (query != null) {
				int limit = attributes.parameterAsIntNoNull("limit");
				int page = attributes.parameterAsIntNoNull("page");
				
				if(limit <= 0 || page <= 0) {
					limit = 20;
					page = 1;
				}
				
				ArrayList<Product> products = service.getProducts(query, limit, page);
				Gson gson = new Gson();
				String output = gson.toJson(products);
				response.setStatus(200);
				response.getWriter().append(output);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			Gson gson = new Gson();
			int productID = attributes.subURLAsIntNoNull(0);
			String body = attributes.getBody();
			
			if(user.userType == UserType.Admin) {
				if(body != null && !body.isEmpty()) {
					if(productID > 0) { // add translation
						ProductTranslation translation = gson.fromJson(body, ProductTranslation.class);
						if(service.addTranslation(productID, translation)) {
							response.setStatus(200);
							response.getWriter().append(JsonBuilder.single("productID", productID));
						} else {
							response.setStatus(406);
						}
					} else {
						Product product = gson.fromJson(body, Product.class);
						int addedID = service.createProduct(product);
						if(addedID > 0) {
							response.setStatus(200);
							response.getWriter().append(JsonBuilder.single("productID", addedID));
						} else {
							response.setStatus(406);
						}
					}
				} else {
					response.setStatus(406);
				}
			} else {
				response.setStatus(403);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			Gson gson = new Gson();
			int productID = attributes.subURLAsIntNoNull(0);
			String body = attributes.getBody();
			
			if(user.userType == UserType.Admin) {
				if(body != null && !body.isEmpty() && productID > 0) {
					Product product = gson.fromJson(body, Product.class);
					product.productID = productID;
					if(service.updateProduct(product)) {
						response.setStatus(200);
					} else {
						response.setStatus(406);
					}
				} else {
					response.setStatus(406);
				}
			} else {
				response.setStatus(403);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int productID = attributes.subURLAsIntNoNull(0);
			String sub2 = attributes.subURLNoNull(1);

			if(user.userType == UserType.Admin) {
				if(productID > 0) {
					if(sub2.equalsIgnoreCase("translations")) { //delete translation
						Language language = Language.valueOf(attributes.subURLNoNull(2));
						if(service.deleteTranslation(productID, language)) {
							response.setStatus(200);
						} else {
							response.setStatus(406);
						}
					} else { //delete product
						if(service.deleteProduct(productID)) {
							response.setStatus(200);
						} else {
							response.setStatus(406);
						}
					}
				} else {
					response.setStatus(406);
				}
			} else {
				response.setStatus(403);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
		
	}

}