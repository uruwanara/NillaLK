package lk.nilla.api;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
import lk.nilla.contentManagement.RequestChangeCont;
import lk.nilla.contentManagement.RequestRegShop;
import lk.nilla.contentManagement.RequestState;
import lk.nilla.contentManagement.RequestPrioritize;
import lk.nilla.contentManagement.RequestNewProduct;
import lk.nilla.contentManagement.RequestType;
import lk.nilla.services.RequestService;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to /requests/#?", urlPatterns = { "api/requests/*" })
public class Requests extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestService service = new RequestService();
		RESTAttributes attribute = new RESTAttributes(request);

		try {
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			// if verification is successful
			try { 
				int requestID = attribute.subURLAsInt(0);
				try {
					Request Req = service.getRequest(requestID);
					System.out.println("H3");
					if(Req != null) {
						if(Req.requestState == RequestState.InReview || Req.requestState == RequestState.Approved || Req.requestState == RequestState.Rejected) {
							if(user.userID == Req.userID || user.userType == UserType.Admin) {
								Gson gson = new Gson();
								String output = gson.toJson(Req);
								response.setContentType("application/json;charset=UTF-8");
								response.getWriter().append(output);
							} else {
								response.setStatus(403);
							}
						} else {
							Gson gson = new Gson();
							String output = gson.toJson(Req);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().append(output);							
						}
					} else {
						System.out.println("H4");
						response.setStatus(404);
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatus(500);
				}
			} catch (Exception e) {
				int userID = attribute.parameterAsIntNoNull("userID"); // 'No Null' returns 0 if not provided in the API request.
				int limit = attribute.parameterAsIntNoNull("limit");
				int offset = attribute.parameterAsIntNoNull("offset");
				String requestState = attribute.parameter("requestState");
				String requestType = attribute.parameter("requestType");
				
				ArrayList<Request> requests = null;
				if(userID > 0) {
					if(requestState == null) {
						if(requestType == null) {
							if(user.userID == userID) {
								requests = service.getRequests(userID, null, null, limit, offset);
							} else {
								requests = null;
							}
						} else if(requestType.equals("NewProduct") || requestType.equals("RegShop") || requestType.equals("Prioritize") || requestType.equals("ChangeCont")) {
							if(user.userID == userID) {
								requests = service.getRequests(userID, requestType, null, limit, offset);
							} else {
								requests = null;
							}
						}
					} else if(requestState.equals("InReview") || requestState.equals("Approved") || requestState.equals("Rejected")) {
						if(requestType == null) {
							if(user.userID == userID) {
								requests = service.getRequests(userID, null, requestState, limit, offset);
							} else {
								requests = null;
							}
						} else if(requestType.equals("NewProduct") || requestType.equals("RegShop") || requestType.equals("Prioritize") || requestType.equals("ChangeCont")) {
							if(user.userID == userID) {
								requests = service.getRequests(userID, requestType, requestState, limit, offset);
							} else {
								requests = null;
							}
						}
					}
				} else if(user.userType == UserType.Admin){
					if(requestState == null) {
						if(requestType == null) {
							requests = null;
						} else if(requestType.equals("NewProduct") || requestType.equals("RegShop") || requestType.equals("Prioritize") || requestType.equals("ChangeCont")) {
							requests = service.getRequests(userID, requestType, null, limit, offset);
						}
					} else if(requestState.equals("InReview") || requestState.equals("Approved") || requestState.equals("Rejected")) {
						if(requestType == null) {
							requests = service.getRequests(userID, null, requestState, limit, offset);
						} else if(requestType.equals("NewProduct") || requestType.equals("RegShop") || requestType.equals("Prioritize") || requestType.equals("ChangeCont")) {
							requests = service.getRequests(userID, requestType, requestState, limit, offset);
						}
					}					
				} else {
					requests = null;					
				}
					
				if(requests != null && requests.size() > 0) {
					Gson gson = new Gson();
					String output = gson.toJson(requests);
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
		
		RequestService service = new RequestService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// if verification is successful
			
			//RequestType requestType = RequestType.valueOf(attributes.parameter("requestType"));
			
			RequestType requestType = RequestType.RegShop;
			
			String body = attributes.getBody();
			Gson gson = new Gson();
			Request Req = null;
			
			if(requestType == RequestType.NewProduct) {
				Req = gson.fromJson(body, RequestNewProduct.class);
			} else if(requestType == RequestType.RegShop) {
				Req = gson.fromJson(body, RequestRegShop.class);
			} else if(requestType == RequestType.Prioritize) {
				Req = gson.fromJson(body, RequestPrioritize.class);
			} else if(requestType == RequestType.ChangeCont) {
				Req = gson.fromJson(body, RequestChangeCont.class);
			}
			
			if(Req != null) {
				
				Req.requestDate = Date.valueOf(LocalDate.now());
				Req.userID = user.userID;
			
				int requestID = service.addRequest(Req);
				if(requestID > 0) {
					response.setStatus(201); // created
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().append(JsonBuilder.single("requestID", requestID)); // send newly created article id
				}
				else if(requestID == 0) {
					response.setStatus(400); //bad request. body error
				} else {
					response.setStatus(500); //internal error. insertion failed and not rolled back
					System.out.println("H1");
				}
				
			} else {
				response.setStatus(406); // body error
			}
	
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(500); // server error
			System.out.println("H2");
		}	
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		RequestService service = new RequestService();
		RESTAttributes attributes = new RESTAttributes(request);
		
		try {
			int requestID = attributes.subURLAsInt(0); // take requestID value through URL

			// Verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// if verification is successful
			
			RequestType requestType = RequestType.valueOf(attributes.parameter("requestType"));
			
			//convert request body into string
			String body = request.getReader().lines().collect(Collectors.joining("\n"));
			Gson gson = new Gson();
			Request Req = null;
			
			if(requestType == RequestType.NewProduct) {
				Req = gson.fromJson(body, RequestNewProduct.class);
			} else if(requestType == RequestType.RegShop) {
				Req = gson.fromJson(body, RequestRegShop.class);
			} else if(requestType == RequestType.Prioritize) {
				Req = gson.fromJson(body, RequestPrioritize.class);
			} else if(requestType == RequestType.ChangeCont) {
				Req = gson.fromJson(body, RequestChangeCont.class);
			}
			
			if(Req != null && requestID > 0) {
				if(user.userType == UserType.Admin) {
					int result = service.changeRequest(Req, 0);
					if(result > 0) {
						response.setStatus(200);
					}
					else if(result == 0) {
						response.setStatus(403);
					}
					else {
						response.setStatus(500);
					}
				} else {
					int result = service.changeRequest(Req, user.userID);
					if(result > 0) {
						response.setStatus(200);
					}
					else if(result == 0) {
						response.setStatus(403);
					}
					else {
						response.setStatus(500);
					}					
				}
			} else {
				response.setStatus(400);
			}
			
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid article ID)
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestService service = new RequestService();
		RESTAttributes attributes = new RESTAttributes(request);

		try {
			int requestID = attributes.subURLAsInt(0);
			
			// verify user
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			// verification successful		
			
			if(requestID > 0) {
				if(user.userType == UserType.Admin) {
					int result = service.deleteRequest(requestID, 0);
					if(result > 0) {
						response.setStatus(200);
					}
					else if(result == 0) {
						response.setStatus(403);
					}
					else {
						response.setStatus(500);
					}
				} else {
					int result = service.deleteRequest(requestID, user.userID);
					if(result > 0) {
						response.setStatus(200);
					}
					else if(result == 0) {
						response.setStatus(403);
					}
					else {
						response.setStatus(500);
					}					
				}
			} else {
				response.setStatus(400);
			}
			
		} catch (AuthException | ExpiredJwtException e) {
			response.setStatus(403); // forbidden
		} catch (Exception e) {
			response.setStatus(400); // bad request (invalid article ID)
		}
	}
}
