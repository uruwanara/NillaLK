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
import lk.nilla.accounts.Channel;
import lk.nilla.accounts.Message;
import lk.nilla.accounts.UserType;
import lk.nilla.auth.AuthException;
import lk.nilla.auth.Authentication;
import lk.nilla.auth.JwsUser;
import lk.nilla.services.JsonBuilder;
import lk.nilla.services.MessagingService;
import lk.nilla.services.RESTAttributes;

/**
 * Servlet implementation class Channels
 */
@WebServlet(description = "Messaging API endpoint", urlPatterns = { "/api/channels/*" })
public class Channels extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MessagingService service = new MessagingService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			if(user.userType == UserType.Gardener || user.userType == UserType.AgriInstructor) {
				int channelID = attributes.subURLAsIntNoNull(0);
				String subURL2 = attributes.subURLNoNull(1);
				String unread = attributes.parameterNoNull("unread");
				int user2 = attributes.parameterAsIntNoNull("user2");
				
				if(channelID > 0 && unread.equalsIgnoreCase("true")) { //has unread messages in channel 
					Channel channel = service.getChannel(channelID);
					if(channel != null) {
						if(channel.gardenerID == user.userID || channel.instructorID == user.userID) {
							Boolean hasUnread = service.hasUnread(user.userID, channelID);
							String output = JsonBuilder.single("hasUnread", hasUnread);
							response.setStatus(200);
							response.getWriter().append(output);
						} else {
							response.setStatus(403); //forbidden
						}
					} else {
						response.setStatus(404); //forbidden
					}
				} else if(channelID > 0 && subURL2.equalsIgnoreCase("messages")) { //get tail from channel
					Channel channel = service.getChannel(channelID);
					if(channel != null) {
						if(channel.gardenerID == user.userID || channel.instructorID == user.userID) {
							int page = attributes.parameterAsIntNoNull("page");
							ArrayList<Message> messages = service.getTail(channelID, page);
							service.read(user.userID, channelID);
							Gson gson = new Gson();
							String output = gson.toJson(messages);
							response.setStatus(200);
							response.getWriter().append(output);
						} else {
							response.setStatus(403); //forbidden
						}
					} else {
						response.setStatus(404); //not found
					}
					
				} else if(channelID > 0) { //get channel info
					Channel channel = service.getChannel(channelID);
					if(channel != null) {
						if(channel.gardenerID == user.userID || channel.instructorID == user.userID) {
							Gson gson = new Gson();
							String output = gson.toJson(channel);
							response.setStatus(200);
							response.getWriter().append(output);
						} else {
							response.setStatus(403); //forbidden
						}
					} else {
						response.setStatus(404); //not found
					}
					
				} else if(user2 > 0) { //get or create channel between two users
					Channel channel;
					if(user.userType == UserType.AgriInstructor ) {
						channel = service.getChannel(user2, user.userID);
					} else {
						channel = service.getChannel(user.userID, user2);
					}
					Gson gson = new Gson();
					String output = gson.toJson(channel);
					response.setStatus(200);
					response.getWriter().append(output);
					
				} else { //get channels
					ArrayList<Channel> channels = service.getChannels(user.userID);
					Gson gson = new Gson();
					String output = gson.toJson(channels);
					response.setStatus(200);
					response.getWriter().append(output);
				}
			} else {
				response.setStatus(403); //forbidden
			}
			
		} catch (ExpiredJwtException | AuthException e) {
			response.setStatus(403);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(500);
		}
		
		//close connection
		service.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MessagingService service = new MessagingService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			if(user.userType == UserType.Gardener || user.userType == UserType.AgriInstructor) {
				int channelID = attributes.subURLAsIntNoNull(0);
				String text = attributes.getBody();
				if(channelID > 0 && !text.isEmpty()) { //send message
					int id = service.send(new Message(user.userID, channelID, text));
					if(id > 0) {
						String output = JsonBuilder.single("id", id);
						response.setStatus(200);
						response.getWriter().append(output);
					} else {
						response.setStatus(400);
					}
				} else {
					response.setStatus(400); //bad request
				}
			} else {
				response.setStatus(403); //forbidden
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
		
		//close connection
		service.close();
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MessagingService service = new MessagingService();
		RESTAttributes attributes = new RESTAttributes(request);
		response.setContentType("application/json;charset=UTF-8");
		
		try {
			Authentication auth = new Authentication();
			JwsUser user = auth.verify(request);
			
			if(user.userType == UserType.Gardener || user.userType == UserType.AgriInstructor) {
				int channelID = attributes.subURLAsIntNoNull(0);
				String status = attributes.parameterNoNull("status");
				if(channelID > 0 && status.equalsIgnoreCase("read")) { //set message to read
					Boolean success = service.read(user.userID, channelID);
					String output = JsonBuilder.single("success", success);
					response.setStatus(200);
					response.getWriter().append(output);
					
				} else { //get channels
					response.setStatus(400); //bad request
				}
			} else {
				response.setStatus(403); //forbidden
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
		
		//close connection
		service.close();
	}
}
