package aic.bigdata.enrichment;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import aic.bigdata.extraction.MongoDatabase;

/**
 * Adds some custom ads and their corresponding topics to MongoDB.
 * 
 * TODO
 * 1. cleaner way to build JSON strings
 * 2. handle multiple topic fields in ad
 */
public class AdsTopicsToMongoDBFiller {
	private MongoDatabase mongodb;

	public AdsTopicsToMongoDBFiller(MongoDatabase mongodb) {
		this.mongodb = mongodb;
	}

	public void fillDatabase() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(getClass().getClassLoader().getResourceAsStream("ads.xml"));
        doc.getDocumentElement().normalize();
        
        for(int i=0; i<doc.getChildNodes().item(0).getChildNodes().getLength(); i++) {
        	if(doc.getChildNodes().item(0).getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
        		this.addAdToDatabase(doc.getChildNodes().item(0).getChildNodes().item(i));
        	}
        	
        }
	}
	
	public void addAdToDatabase(Node node) throws UnknownHostException {
		String json = this.buildAdJSON(node);
		if(json != null) {
			mongodb.writeAd(json);
		}
	}
	
	private void addAdToTopic(String topic, int adId) {
		try {
			if(mongodb.checkTopicExists(topic)) {
				//TODO check if ad if is already in list, if not add ad id to "ads:" property
				//list: propertyname: [ "Turing machine", "Turing test", "Turingery" ],
			} else {
				
				String json = "{\"id\": \"" + topic + "\", \"ads\": ["+adId+"]}";
				System.out.println(json);
				mongodb.writeTopic(json);
				System.out.println("WROTE");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private String buildAdJSON(Node node) {
		String json = null;
		
		int id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
		
		try {
			if(!mongodb.checkAdExists(id)) {
				json = "{\"id\": " + id;
				
				for(int i=0; i<node.getChildNodes().getLength(); i++) {
					if(node.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE && !node.getChildNodes().item(i).getTextContent().isEmpty()) {
						if(node.getChildNodes().item(i).getNodeName().toLowerCase().equals("topic")) {
							this.addAdToTopic(node.getChildNodes().item(i).getTextContent().toLowerCase(), id);
						}
						json += ", \""+node.getChildNodes().item(i).getNodeName()+"\": \""+node.getChildNodes().item(i).getTextContent()+"\"";
					}
					
				}
				
				json += "}";

				System.out.println("ad: "+json);
			}
		} catch (UnknownHostException | DOMException e) {
			e.printStackTrace();
		}
		
		
		return json;
	}	
}
