package chatter.xmlParser;

import java.io.IOException;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

public class AddUserXMLFIle {

	public static boolean addUser(String filename, String arg_username, String arg_password) {

		if (CheckXMLFile.userExists(filename, arg_username))
			return false;

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(filename);
			Element root = document.getDocumentElement();

			Collection<User> users = new ArrayList<User>();
			users.add(new User());

			Element newUser = document.createElement("user");

			Element name = document.createElement("username");
			name.appendChild(document.createTextNode(arg_username));
			newUser.appendChild(name);

			Element password = document.createElement("password");
			password.appendChild(document.createTextNode(arg_password));
			newUser.appendChild(password);

			root.appendChild(newUser);

			DOMSource source = new DOMSource(document);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(filename);
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}
