package chatter.xmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class CheckXMLFile {

	public static boolean checkFileExists(String file_name) {
		File f = new File(file_name);
		return f.exists();
	}

	public static boolean userExists(String filename, String usernameArg) {
		try {
			String filepath = filename;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);

			// Get the root element
			Node chatter = doc.getFirstChild();

			NodeList userList = chatter.getChildNodes();

			// Iterate Users
			for (int i = 0; i < userList.getLength(); i++) {
				Node tempNode = userList.item(i);
				if (tempNode.getFirstChild().getTextContent().equals(usernameArg))
					return true;
			}

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		return false;
	}
}
