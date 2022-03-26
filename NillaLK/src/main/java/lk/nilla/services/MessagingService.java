package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import lk.nilla.accounts.Channel;
import lk.nilla.accounts.Message;
import lk.nilla.accounts.MessageStatus;

public class MessagingService {
	
	private DBConnection connection = new DBConnection();
	private static final int messageLimit = 10;
	
	/**
	 * Returns tailing messages of a channel. Limits to {@value #messageLimit}
	 * @param channelID
	 * @param page - starts from 0
	 * @return ArrayList {@literal <}Message{@literal >}
	 */
	public ArrayList<Message> getTail(int channelID, int page) {
		ArrayList<Message> messages = new ArrayList<Message>(0);
		
		try {
			ResultSet rs = new SQLStatement("SELECT", "Message")
					.limit(messageLimit)
					.offset(messageLimit * page)
					.where("channelID", channelID)
					.orderBy("msgTime", "DESC")
					.generate(connection)
					.executeQuery();
			
			if(!rs.next()) {
				return null;
			}
			
			do {
				Message temp = 
						new Message(rs.getInt(1), rs.getInt(5), rs.getInt(6), rs.getString(2), rs.getTimestamp(3), MessageStatus.valueOf(rs.getString(4)));
				messages.add(temp);
			} while(rs.next());
			
			return messages;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Send message
	 * @param message - Message object
	 * @return Boolean
	 */
	public int send(Message message) {
		try {
			PreparedStatement stmt = new SQLStatement("INSERT", "Message")
					.set("msg", message.text)
					.set("userID", message.userID)
					.set("channelID", message.channelID)
					.generate(connection);
			stmt.executeUpdate();
			
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next())
				return rs.getInt(1);
			else
				return 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	
	/**
	 * Returns true if unread messages are available for a channel
	 * @param userID
	 * @param channelID
	 * @return
	 */
	public Boolean hasUnread(int userID, int channelID) {
		try {
			PreparedStatement stmt = connection.prepare("SELECT * FROM Message WHERE msgStatus = 'Sent' AND channelID = ? AND userID != ? LIMIT 1");
			stmt.setInt(1, channelID);
			stmt.setInt(2, userID);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next())
				return true;
			else
				return false;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Set unread messages to read
	 * @param userID
	 * @param channelID
	 * @return
	 */
	public Boolean read(int userID, int channelID) {
		try {
			PreparedStatement stmt = new SQLStatement("UPDATE", "Message")
					.set("msgStatus", "Read")
					.where("channelID", channelID)
					.where("userID", userID)
					.where("msgStatus", "Sent")
					.generate(connection);
			
			stmt.executeUpdate();
			int count = stmt.getUpdateCount();
			
			if(count > 0)
				return true;
			else
				return false;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Returns channel when gardenerID and instructorID is given. Creates a new channel if not found
	 * @param gardID
	 * @param insID
	 * @return Channel instance
	 */
	public Channel getChannel(int gardID, int insID) {
		try {
			ResultSet rs = new SQLStatement("SELECT", "Channel")
					.limit(1)
					.where("gardenerID", gardID)
					.where("instructorID", insID)
					.generate(connection)
					.executeQuery();
			
			if(rs.next()) {
				return new Channel(rs.getInt(1), rs.getTimestamp(2), rs.getInt(3), rs.getInt(4));
			} else {
				if(createChannel(gardID, insID) > 0) {
					return getChannel(gardID, insID); //return created channel
				} else {
					return null;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns channel when channelID is given
	 * @param channelID
	 * @return Channel instance
	 */
	public Channel getChannel(int channelID) {
		try {
			ResultSet rs = new SQLStatement("SELECT", "Channel")
					.limit(1)
					.where("channelID", channelID)
					.generate(connection)
					.executeQuery();
			
			if(rs.next()) {
				return new Channel(rs.getInt(1), rs.getTimestamp(2), rs.getInt(3), rs.getInt(4));
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Returns user's available channels
	 * @param userID
	 * @return ArrayList{@literal <}Channel{@literal >}
	 */
	public ArrayList<Channel> getChannels(int userID) {
		try {
			PreparedStatement stmt = connection.prepare("SELECT * FROM Channel WHERE gardenerID = ? OR instructorID = ? ORDER BY channelID DESC");
			stmt.setInt(1, userID);
			stmt.setInt(2, userID);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				ArrayList<Channel> channels = new ArrayList<Channel>(0);
				do {
					channels.add(new Channel(rs.getInt(1), rs.getTimestamp(2), rs.getInt(3), rs.getInt(4)));
				} while (rs.next());
				return channels;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Creates new Channel
	 * @param gardID
	 * @param insID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	private int createChannel(int gardID, int insID) throws SQLException, Exception {
		PreparedStatement stmt = new SQLStatement("INSERT", "Channel")
				.set("gardenerID", gardID)
				.set("instructorID", insID)
				.generate(connection);
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		
		if(rs.next()) {
			return rs.getInt(1);
		} else {
			return 0;
		}
	}
	
	public void close() {
		connection.close();
	}
	
}
