package chatter.jUnit;

import chatter.xmlParser.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class XMLTest {

	@Test // Test the creation of an xml file
	public void testCreateFile() {
		chatter.jUnit.Auxiliar.deleteTestFile();
		CreateXMLFile.create("users_test.xml");
		assertEquals(CheckXMLFile.checkFileExists("users_test.xml"), true);
	}

	@Test // Test adding an user
	public void testAddUser() {
		AddUserXMLFIle.addUser("users_test.xml", "user1", "pass1");
		AddUserXMLFIle.addUser("users_test.xml", "user2", "pass2");
		assertEquals(CheckXMLFile.userExists("users_test.xml", "user2"), true);
	}

	@Test // Test if user exists
	public void testUser() {
		assertEquals(CheckXMLFile.userExists("users_test.xml", "user1"), true);
	}

	@Test // Test changing the username
	public void testChangeUsername() {
		AddUserXMLFIle.addUser("users_test.xml", "user3", "pass3");
		EditXMLFile.changeUsername("users_test.xml", "user3", "user3_changed");
		assertEquals(CheckXMLFile.userExists("users_test.xml", "user3_changed"), true);
	}

	@Test // Test login function
	public void testLogin() {
		assertEquals(ReadXMLFile.login("users_test.xml", "user2", "pass2"), true);

	}

	@Test // Test changing the password
	public void testChangePassword() {
		AddUserXMLFIle.addUser("users_test.xml", "user4", "pass4");
		EditXMLFile.changePassword("users_test.xml", "user4", "pass4", "new_pass4");
		assertEquals(ReadXMLFile.login("users_test.xml", "user4", "new_pass4"), true);
	}

	@Test
	public void testAddExistingUser() {
		AddUserXMLFIle.addUser("users_test.xml", "user5", "pass5");
		assertEquals(AddUserXMLFIle.addUser("users_test.xml", "user5", "pass5"), false);
	}

}
