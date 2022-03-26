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
import lk.nilla.contentManagement.Shop;
import lk.nilla.contentManagement.ShopProduct;
import lk.nilla.contentManagement.ShopState;
import lk.nilla.services.RESTAttributes;
import lk.nilla.services.ShopProductService;
import lk.nilla.services.ShopService;

@WebServlet(description = "responds to api/shopproducts/#?", urlPatterns = { "/api/shopproducts/*" })

public class ShopProducts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShopProductService service = new ShopProductService();
		ShopService shopService = new ShopService();
		RESTAttributes attribute = new RESTAttributes(request);

		try {
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			try { // try by articleID
				int shopID = attribute.subURLAsInt(0); // 'No Null' returns 0 if not provided in the API request.
				int productID = attribute.subURLAsInt(1);
				try {
					ShopProduct shopProduct = service.getShopProduct(shopID, productID);
					Shop shop = shopService.getShop(shopID);
					
					if(shopProduct != null) {
						if(shop.shopState == ShopState.Private || shop.shopState == ShopState.InReview) {
							if(user.userID == shop.ShopOwnerID || user.userType == UserType.Admin) { // if owner or admin
								Gson gson = new Gson();
								String output = gson.toJson(shopProduct);
								response.setContentType("application/json;charset=UTF-8");
								response.getWriter().append(output);
							} else {
								response.setStatus(403); // forbidden
							}
						} else {
							Gson gson = new Gson();
							String output = gson.toJson(shopProduct);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().append(output); // Return a single article
						}
					} else {
						response.setStatus(404); // Resource not found. Empty results
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatus(500); // Server error
				}
			} catch (Exception e) { // else
				
				int shopID = attribute.parameterAsIntNoNull("shopID"); // 'No Null' returns 0 if not provided in the API request.
				int productID = attribute.parameterAsIntNoNull("productID");
				int limit = attribute.parameterAsIntNoNull("limit");
				int offset = attribute.parameterAsIntNoNull("offset");				

				ArrayList<ShopProduct> shopProducts = null;

				if(shopID > 0) {
					if(productID <= 0) { // if pestID not given
						shopProducts = service.getShopProducts(shopID, 0, limit, offset);
					} else {
						shopProducts = service.getShopProducts(shopID, productID, limit, offset);
					}
				} else { //if no user id is given
					if(productID > 0) { // if pestID given
						shopProducts = service.getShopProducts(0, productID, limit, offset);
					} else {
						response.setStatus(404); //No resources found
					}
				}				
				
				if(shopProducts != null && shopProducts.size() > 0) {
					Gson gson = new Gson();
					String output = gson.toJson(shopProducts);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().append(output);
				} else {
					response.setStatus(404); //No resources found
				}
			}
		} catch (AuthException | ExpiredJwtException e1) {
			e1.printStackTrace();
			response.setStatus(403); // forbidden
		} catch (Exception e1) {
			e1.printStackTrace();
			response.setStatus(500); // forbidden
		}				
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShopProductService service = new ShopProductService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			String body = attributes.getBody();
			Gson gson = new Gson();
			ShopProduct shopProduct = null;
			shopProduct = gson.fromJson(body, ShopProduct.class);
			
			if(shopProduct != null && user.userType == UserType.ShopOwner) {
								
				int index = service.createShopProduct(shopProduct);
				System.out.println(index);
				
				if(index == 1) {
					response.setStatus(201); // created
					response.setContentType("application/json;charset=UTF-8");
//					response.getWriter().append(JsonBuilder.single("articleID", articleID)); // send newly created article id
				} else if(index == 0) {
					response.setStatus(406); // bad request (failed to create a shop product. Check body)
				} else {
					response.setStatus(500);
				}
				
			} else {
				response.setStatus(406); // body error
			}
	
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(500); // server error
		}	
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShopProductService service = new ShopProductService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			ShopProduct shopProduct = new Gson().fromJson(attributes.getBody(), ShopProduct.class);
			int shopID = attributes.subURLAsInt(0); // take articleID value through URL
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(shopProduct != null && shopID > 0) {
				if(user.userType == UserType.ShopOwner) { // if 
					int result = service.updateShopProduct(shopProduct, shopID); // 0 to skip checking against userID
					if(result > 0)
						response.setStatus(200); // OK
					else if(result == 0)
						response.setStatus(400); // body, parameter or id error
					else
						response.setStatus(500); // server error
/*				} else {
					int result = service.updateArticle(article, user.userID); // 0 to skip checking against userID
					if(result > 0)
						response.setStatus(200); // OK
					else if(result == 0)
						response.setStatus(403); // forbidden (not owner)
					else
						response.setStatus(500); // server error
*/				}
			} else {
				response.setStatus(400); // body, parameter error
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request
		}
	}	
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShopProductService service = new ShopProductService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			int shopID = attributes.subURLAsInt(0); // take shopID value through URL
			int productID = attributes.subURLAsInt(1); // take productID value through URL
			
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful
			
			if(productID > 0 && shopID > 0) {
				if(user.userType == UserType.ShopOwner) { // if 
					int result = service.deleteShopProduct(productID, shopID); // 0 to skip checking against userID
					if(result > 0)
						response.setStatus(200); // OK
					else if(result == 0)
						response.setStatus(400); // body, parameter or id error
					else
						response.setStatus(500); // server error
/*				} else {
					int result = service.updateArticle(article, user.userID); // 0 to skip checking against userID
					if(result > 0)
						response.setStatus(200); // OK
					else if(result == 0)
						response.setStatus(403); // forbidden (not owner)
					else
						response.setStatus(500); // server error
*/				}
			} else {
				response.setStatus(400); // body, parameter error
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request
		}
	}		
}
