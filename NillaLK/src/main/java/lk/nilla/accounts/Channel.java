package lk.nilla.accounts;

import java.sql.Timestamp;

public class Channel {
	public int channelID;
	public Timestamp startTimestamp;
	public int gardenerID;
	public int instructorID;
	
	public Channel(int channelID, Timestamp startTimestamp, int gardenerID, int instructorID) {
		this.channelID = channelID;
		this.startTimestamp = startTimestamp;
		this.gardenerID = gardenerID;
		this.instructorID = instructorID;
	}
}
