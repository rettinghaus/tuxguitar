package app.tuxguitar.app.tools.browser.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import app.tuxguitar.app.tools.browser.TGBrowserCollection;
import app.tuxguitar.app.tools.browser.TGBrowserManager;
import app.tuxguitar.app.tools.browser.base.TGBrowserSettings;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TGBrowserReader {
	private static final String ITEM_TAG = "browser-collection";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_TITLE = "title";
	private static final String ATTRIBUTE_DATA = "data";

	public void loadCollections(TGBrowserManager manager,File file){
		if (file.exists()){
			try {
				Document doc = getDocument(file);
				loadCollections(manager,doc.getFirstChild());
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}

	private static void loadCollections(TGBrowserManager manager,Node node){
		NodeList listNode = node.getChildNodes();
		for (int i = 0; i < listNode.getLength(); i++) {
			Node child = listNode.item(i);
			String nameNode = child.getNodeName();
			if (nameNode.equals(ITEM_TAG)) {
				NamedNodeMap params = child.getAttributes();

				String type = params.getNamedItem(ATTRIBUTE_TYPE).getNodeValue();
				String title = params.getNamedItem(ATTRIBUTE_TITLE).getNodeValue();
				String data = params.getNamedItem(ATTRIBUTE_DATA).getNodeValue();
				if(type != null){
					TGBrowserCollection collection = new TGBrowserCollection();
					collection.setType(type);
					collection.setData(new TGBrowserSettings());
					collection.getData().setTitle(title);
					collection.getData().setData(data);
					manager.addCollection(collection);
				}
			}
		}
	}

	private static Document getDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// CVE-2020-14940
		try {
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setXIncludeAware(false);
		} catch (Throwable throwable) {
		}
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(file);

		return document;
	}

}
