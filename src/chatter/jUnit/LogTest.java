package chatter.jUnit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;
import chatter.xmlParser.CheckXMLFile;

public class LogTest {
	
	
	@Test // Test creation of log file
	public void createLogTest() {
		chatter.messageLogs.Logs.createRoomFile("testLog");
		assertEquals(CheckXMLFile.checkFileExists("testLog.txt"), true);
	}
	
	@Test // Test adding a line to a log file
	public void addLineTest() {
		Date d = new Date();
		chatter.messageLogs.Logs.addLine("testLog", "user_1", "test string", d);
		chatter.messageLogs.Logs.addLine("testLog", "user_2", "test response", d);
		try {
			assertEquals(Auxiliar.countLines("testLog.txt"), 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
