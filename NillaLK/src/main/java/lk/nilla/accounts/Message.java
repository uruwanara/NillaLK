package lk.nilla.accounts;

import java.sql.Timestamp;

public class Message {
	public int msgID;
	public int userID;
	public int channelID;
	public String text;
	public Timestamp timestamp; 
	public MessageStatus status;
	
	public Message(int msgID, int userID, int channelID, String text, Timestamp timestamp, MessageStatus status) {
		this.msgID = msgID;
		this.userID = userID;
		this.channelID = channelID;
		this.text = text;
		this.timestamp = timestamp;
		this.status = status;
	}
	
	public Message(int userID, int channelID, String text) {
		this.msgID = 0;
		this.userID = userID;
		this.channelID = channelID;
		this.text = text;
		this.timestamp = null;
		this.status = null;
	}
}
