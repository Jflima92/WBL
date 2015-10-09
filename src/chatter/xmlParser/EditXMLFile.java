package chatter.xmlParser;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EditXMLFile {

	public static boolean changePassword(String filename, String usernameArg, String passwordArg, String newpassword) {
		boolean success = false;
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
				if (tempNode.getFirstChild().getTextContent().equals(usernameArg)
						&& tempNode.getFirstChild().getNextSibling().getTextContent().equals(passwordArg)) {
					tempNode.getFirstChild().getNextSibling().setTextContent(newpassword);
					success = true;
					break;
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		return success;
	}

	public static boolean changeUsername(String filename, String usernameArg, String newUsername) {

		boolean success = false;

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
				if (tempNode.getFirstChild().getTextContent().equals(usernameArg)) {
					tempNode.getFirstChild().setTextContent(newUsername);
					success = true;
					break;
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		return success;

	}
}
