package chatter.xmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ReadXMLFile {

	public static boolean login(String filename, String arg_user, String arg_pass) {
		try {
			File users = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(users);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("user");
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String xmlUserName =  eElement.getElementsByTagName("username").item(0).getTextContent();
					String xmlPassword = eElement.getElementsByTagName("password").item(0).getTextContent();

					
					if ( arg_user.equals(xmlUserName) && arg_pass.equals(xmlPassword)){
						return true;
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
