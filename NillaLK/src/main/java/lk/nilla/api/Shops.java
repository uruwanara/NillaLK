package lk.nilla.api;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
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
import lk.nilla.contentManagement.Request;
import lk.nilla.contentManagement.Shop;
import lk.nilla.contentManagement.ShopState;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;
import lk.nilla.services.RequestService;
import lk.nilla.services.ShopService;

@WebServlet(description = "responds to api/shops/#?", urlPatterns = { "api/shops/*" })
public class Shops extends HttpServlet  {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShopService service = new ShopService();
		RESTAttributes attribute = new RESTAttributes(request);

		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			try {
				int shopID = attribute.subURLAsInt(0);
				try {
					Shop shop = service.getShop(shopID);
					if(shop != null) {
						if(shop.shopState == ShopState.InReview || shop.shopState == ShopState.Private) { // if shop is InReview State or Private State
							if(user.userID == shop.ShopOwnerID || user.userType == UserType.Admin) { // both shop owner and admin can view these shops
								Gson gson = new Gson();
								String output = gson.toJson(shop);
								response.setContentType("application/json;charset=UTF-8");
								response.getWriter().append(output); //write a single shop
							} else {
								response.setStatus(403); // forbidden
							}
						} else {
							Gson gson = new Gson();
							String output = gson.toJson(shop);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().append(output); //write a single shop							
						}
					} else {
						response.setStatus(404); //Resource not found. empty results
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatus(500); // Server error
				}
			} catch (Exception e) {
				String shopName = attribute.parameterNoNull("shopName"); // 'No Null' returns an empty string if not provided in the API request.
				if(!shopName.isEmpty()) {
					shopName = "%"+shopName+"%"; // prepare for 'like' queries
				} else {
					shopName = null; // do not filter by shop name
				}
				int shopOwnerID = attribute.parameterAsIntNoNull("shopOwnerID"); // 'No Null' returns 0 if not provided in the API request.
				int limit = attribute.parameterAsIntNoNull("limit");
				int offset = attribute.parameterAsIntNoNull("offset");
				String shopState = attribute.parameter("shopState");
				
				ArrayList<Shop> shops = null;
				if(shopOwnerID > 0) {
					if(shopState == null) { //selections without shop state
						if(user.userID == shopOwnerID || user.userType == UserType.Admin) { // select shops related to one shop owner 
							shops = service.getShops(shopName, shopOwnerID, null, limit, offset);
						} else { // view shop by others
							shops = service.getShops(shopName, shopOwnerID, "Public", limit, offset);
						}
					} else if(shopState.equals("InReview") || shopState.equals("Private")) {
						if(user.userID == shopOwnerID || user.userType == UserType.Admin) { // shops in InReview and Private State
							shops = service.getShops(shopName, shopOwnerID, shopState, limit, offset);
						} else { // others cannot see these states
							shops = null;
						}						
					} else if(shopState.equals("Public")) { // View all Public State shops by others
						shops = service.getShops(shopName, shopOwnerID, "Public", limit, offset);						
					}
				} else { // user who is not log to our system
					shops = service.getShops(shopName, 0, "Public", limit, offset);
				}
				
				if(shops != null && shops.size() > 0) {
					Gson gson = new Gson();
					String output = gson.toJson(shops);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().append(output); //write list of shops						
				} else {
					response.setStatus(404); // No resource found
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
		ShopService service = new ShopService();
		RequestService requestService = new RequestService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			String body = attributes.getBody();
			Gson gson = new Gson();
			Shop shop = null;
			shop = gson.fromJson(body, Shop.class);
			Request regRequest = null;
			regRequest = gson.fromJson(body, Request.class);

			if(shop != null) {

				shop.regDate = Date.valueOf(LocalDate.now()); // set current date
				shop.ShopOwnerID = user.userID; // set shop owner to requesting user

				int shopID = service.createShop(shop);				
				
				if(shopID > 0) {
					response.setStatus(201); // created
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().append(JsonBuilder.single("shopID", shopID));
					int requestID = requestService.addRequest(regRequest);
				} else if(shopID == 0) {
					response.setStatus(400); // bad request (SQL error)
				} else {
					response.setStatus(500); // internal error
				}				
			} else {
				response.setStatus(406);
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(500); // server error
			System.out.println("Here");
		}	
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShopService service = new ShopService();
		RESTAttributes attributes = new RESTAttributes(request);

		try {
			Shop shop = new Gson().fromJson(attributes.getBody(), Shop.class);
			int shopID = attributes.subURLAsInt(0);
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);

			if(shop != null && shopID > 0) {
				if(user.userType == UserType.Admin) {
					int result = service.updateShop(shop, 0);
					if(result > 0) {
						response.setStatus(200);
					} else if(result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}
				} else if(user.userType == UserType.ShopOwner) {
					int result = service.updateShop(shop, user.userID);
					if(result > 0) {
						response.setStatus(200);
					} else if(result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}					
				}
			} else {
				response.setStatus(400);
			}
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid shop ID)
		}
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShopService service = new ShopService();
		RESTAttributes attribute = new RESTAttributes(request);
		
		try {
			int shopID = attribute.subURLAsInt(0);
			
			//verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
		
			if(shopID > 0) {
				if(user.userType == UserType.Admin) {
					int result = service.deleteShop(shopID, 0);
					if(result > 0) {
						response.setStatus(200);
					} else if(result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}
				} else if(user.userType == UserType.ShopOwner) {
					int result = service.deleteShop(shopID, user.userID);
					if(result > 0) {
						response.setStatus(200);
					} else if(result == 0) {
						response.setStatus(400);
					} else {
						response.setStatus(500);
					}					
				}
			} else {
				response.setStatus(400); //bad request (Invalid shopID)
			}
			
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid shop ID)
		}		
	}
	
}
