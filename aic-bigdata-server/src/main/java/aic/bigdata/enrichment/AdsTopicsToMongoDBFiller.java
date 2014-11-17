package aic.bigdata.enrichment;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

import aic.bigdata.extraction.MongoDatabase;

public class AdsTopicsToMongoDBFiller {
	private MongoDatabase mongodb;

	public AdsTopicsToMongoDBFiller(MongoDatabase topicMongodb) {
		mongodb = topicMongodb;
	}

	public void fillDatabase() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(getClass().getClassLoader().getResourceAsStream("ads.xml"));
        doc.getDocumentElement().normalize();
        
        for(int i=0; i<doc.getChildNodes().item(0).getChildNodes().getLength(); i++) {
        	if(doc.getChildNodes().item(0).getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
        		String json = this.buildJSON(doc.getChildNodes().item(0).getChildNodes().item(i));
        		//System.out.println("json: "+json);
        		mongodb.writeAd(json);
        	}
        	
        }
	}
	
	private String buildJSON(Node node) {
		String json = "{";
		json += "\"id\": " + node.getAttributes().getNamedItem("id").getNodeValue();
		
		for(int i=0; i<node.getChildNodes().getLength(); i++) {
			if(node.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE && !node.getChildNodes().item(i).getTextContent().isEmpty()) {
				json += ", \""+node.getChildNodes().item(i).getNodeName()+"\": \""+node.getChildNodes().item(i).getTextContent()+"\"";
			}
			
		}
		
		json += "}";

		return json;
	}
}
