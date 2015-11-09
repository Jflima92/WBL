package chatter.messageLogs;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs {
	
	static FileWriter writer;
	
	
	/*
	 *	Create a new file for a room 
	 */
	public static boolean createRoomFile(String roomName) {
		try {
			writer = new FileWriter(roomName + ".txt");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 * Add lines to log file
	 */
	public static boolean addLine(String room, String user, String line, Date date) {
		try {
			// If log doesn't exist, create it
			File f = new File(room + ".txt");
			if (!f.exists())
				createRoomFile(room);
			// Format log string
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy/MM/dd '-' hh:mm:ss");
			String log = "[" + ft.format(date) + "] " + user + ": " + line;
			// Open file, write the log and close it
			writer = new FileWriter(room + ".txt", true);
			writer.write(log + "\r\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
