package lk.nilla.api;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.ExpiredJwtException;
import lk.nilla.accounts.Admin;
import lk.nilla.accounts.AgriInstructor;
import lk.nilla.accounts.Channel;
import lk.nilla.accounts.Gardener;
import lk.nilla.accounts.Message;
import lk.nilla.accounts.ShopOwner;
import lk.nilla.accounts.User;
import lk.nilla.accounts.UserType;
import lk.nilla.accounts.ZonalAgriHead;
import lk.nilla.auth.AuthException;
import lk.nilla.auth.Authentication;
import lk.nilla.auth.JwsUser;
import lk.nilla.services.UserService;
import lk.nilla.services.VerificationService;
import lk.nilla.services.VerificationType;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.RESTAttributes;

@WebServlet(description = "responds to api/users/user-accounts/#?", urlPatterns = { "/api/users/user-accounts/*" })
public class UserAccounts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		UserService service = new UserService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int userID = attributes.subURLAsIntNoNull(0);
			String query = attributes.parameterNoNull("query");
			
			if(userID > 0) {
				User tempUser = service.getUser(userID);
				if(tempUser != null) {
					if(!(user.userID == userID || user.userType == UserType.Admin)) {
						tempUser.email = null;
					}
					Gson gson = new Gson();
					String output = gson.toJson(tempUser);
					response.setStatus(200);
					response.getWriter().append(output); //write a single user
				} else {
					response.setStatus(404);
				}
			} else {
				int limit = attributes.parameterAsIntNoNull("limit");
				int offset = attributes.parameterAsIntNoNull("offset");
				
				UserType type = null;
				if(attributes.parameter("type") != null) {
					try {
						type = UserType.valueOf(attributes.parameterNoNull("type"));
						ArrayList<User> users = service.getUsers(query, type, limit, offset);

						if(user.userType != UserType.Admin) {
							ArrayList<User> newUsers = new ArrayList<User>(0);
							for(User userItem : users) {
								userItem.email = null;
								newUsers.add(userItem);
							}
							users = newUsers;
						}
						
						Gson gson = new Gson();
						String output = gson.toJson(users);
						response.setStatus(200);
						response.getWriter().append(output); //write users
					} catch (Exception e) {
						response.setStatus(406);
					}
				} else {
					ArrayList<User> users = service.getUsers(query, type, limit, offset);

					if(user.userType != UserType.Admin) {
						ArrayList<User> newUsers = new ArrayList<User>(0);
						for(User userItem : users) {
							userItem.email = null;
							newUsers.add(userItem);
						}
						users = newUsers;
					}
					
					Gson gson = new Gson();
					String output = gson.toJson(users);
					response.setStatus(200);
					response.getWriter().append(output); //write users
				}
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
		
		UserService service = new UserService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		String verification = attributes.parameter("verification");
		
		if(verification != null) {
			//if user id was given, complete registration
			String email = attributes.parameterNoNull("email");
			String password = attributes.parameterNoNull("password");
			
			// check whether userID is similar to the user account of given email and password
			if(!email.isEmpty() && !password.isEmpty() && verification.length() == 8) {
				try {
					Authentication auth = new Authentication();
					User user = auth.authenticate(email, password);
					if(user != null) {
						// if a user exists with given email and password (authentication successful)
						if(VerificationService.verifyCode(user.userID, VerificationType.EMAILVERIFY, verification)) {
							// if verification is success, complete registration
							UserType userType = user.userType;
							//convert request body into string
							String body = attributes.getBody();
							Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
							User newUser = null;
							
							if(userType == UserType.Gardener) {
								newUser = gson.fromJson(body, Gardener.class);
							} else if(userType == UserType.ShopOwner) {
								newUser = gson.fromJson(body, ShopOwner.class);
							} else if(userType == UserType.AgriInstructor) {
								newUser = gson.fromJson(body, AgriInstructor.class);
							} else if(userType == UserType.ZonalAgriHead) {
								newUser = gson.fromJson(body, ZonalAgriHead.class);
							} else if(userType == UserType.Admin) {
								newUser = gson.fromJson(body, Admin.class);
							}

							newUser.userID = user.userID;
							newUser.userType = userType;
							if(service.createUserType(newUser)) {
								response.setStatus(201); //created
								response.getWriter().append("{ \"userID\": " + user.userID + "}");
							}
							else {
								response.setStatus(400); //bad request. body error
								response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.BadPayload)));
							}
						} else {
							// verification failure
							response.setStatus(406);
							response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.VerificationFailure)));
						}
					} else {
						// authentication failure
						response.setStatus(406);
						response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.CredentialError)));
					}
				} catch (Exception e) {
					// TODO -- logs --
					response.setStatus(500); // internal error? -authentication failed to initialize
					e.printStackTrace();
				}
			} else {
				// bad parameters
				response.setStatus(406);
				response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.BadParameters)));
			}
			
		} else {
			//else create new user
			
			String password = attributes.parameter("password");
			int pwStrength = VerificationService.validatePasswordStrength(password);
			
			if(pwStrength > 1) {
				try {
					//convert request body into string
					String body = attributes.getBody();
					Gson gson = new Gson();
					User user = gson.fromJson(body, User.class);
					
					if(user.userType == UserType.Gardener)
						user.profilePicture = "/img/avatars/gardener.png";
					else if(user.userType == UserType.Admin)
						user.profilePicture = "/img/avatars/admin.png";
					else if(user.userType == UserType.AgriInstructor)
						user.profilePicture = "/img/avatars/instructor.png";
					else if(user.userType == UserType.ShopOwner)
						user.profilePicture = "/img/avatars/owner.png";
					
					if(user != null && user.userType != null) {
						if(VerificationService.validateEmail(user.email)) {
							int createdID = service.createUser(user);
							if(createdID > 0) {
								// add credentials
								Authentication auth = new Authentication();
								try {
									auth.createPassword(createdID, password);
									if(VerificationService.createEmailVerification(createdID, user.email, user.firstName) > 0) {
										response.setStatus(201); //created
										response.getWriter().append("{ \"userID\": " + createdID + "}");
									} else {
										service.deleteUser(createdID);
										response.setStatus(500); // internal error?
									}
								} catch (Exception e) {
									// TODO -- logs --
									service.deleteUser(createdID); // internal error?
									e.printStackTrace();
									response.setStatus(500); // internal error?
								}
							} else if(createdID == -2) {
								response.setStatus(406); //bad request. body error?
								response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.EmailAlreadyExists)));
							} else if(createdID == 0) {
								response.setStatus(406); //bad request. body error?
								response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.BadPayload)));
							} else {
								response.setStatus(500); //bad request. db error
							}
						} else {
							response.setStatus(406); //bad request. email format error
							response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.EmailFormatError)));
						}
					} else {
						response.setStatus(406); //bad request. email format error
						response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.UserTypeDoesNotExist)));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatus(500); //bad request. server error?
				}
			} else if (pwStrength == 1 || pwStrength == 0) {
				response.setStatus(406); //bad request. user does not exist
				response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.WeakPassword)));
			} else {
				response.setStatus(406); // password not given
				response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.NoPasswordProvided)));
			}
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		UserService service = new UserService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			UserType userType = UserType.valueOf(attributes.parameter("type"));
			
			//convert request body into string
			String body = attributes.getBody();
			Gson gson = new Gson();
			
			User tempUser = null;
			if(userType == UserType.Gardener) {
				tempUser = gson.fromJson(body, Gardener.class);
			} else if(userType == UserType.ShopOwner) {
				tempUser = gson.fromJson(body, ShopOwner.class);
			} else if(userType == UserType.AgriInstructor) {
				tempUser = gson.fromJson(body, AgriInstructor.class);
			} else if(userType == UserType.ZonalAgriHead) {
				tempUser = gson.fromJson(body, ZonalAgriHead.class);
			} else if(userType == UserType.Admin) {
				tempUser = gson.fromJson(body, Admin.class);
			}
			
			if(user.userID == tempUser.userID || user.userType == UserType.Admin) {
				int status = service.changeUpdateUser(tempUser);
				if(status == -1) {
					response.setStatus(400); //bad request. body error
					response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.BadPayload)));
				} else {
					response.setStatus(200); // ok
				}
			} else {
				response.setStatus(403);
			}

		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			response.setStatus(400); //bad request. userType error
			response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.UserTypeDoesNotExist)));
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserService service = new UserService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			int userID = attributes.subURLAsIntNoNull(0);
			
			if(user.userID == userID || user.userType == UserType.Admin) {
				int status = service.deleteUser(userID);
				if(status == 0)
					response.setStatus(200); //ok
				else
					response.setStatus(400); //bad request. not deleted
			} else {
				response.setStatus(403);
			}
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			response.setStatus(400); //bad request. userType error
			response.getWriter().append(new Gson().toJson(new ErrorReason(ErrorType.UserTypeDoesNotExist)));
		}
	}
}
