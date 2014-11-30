package aic.bigdata.enrichment;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
import aic.bigdata.server.ServerConfig;

import com.google.gson.Gson;

/**
 * Adds some custom ads and their corresponding topics to MongoDB, 
 * as well as topics to the graph database.
 */
public class AdsTopicsToDatabaseFiller {
	private MongoDatabase mongodb;
	private Gson gson;
	private String xmlFilename = "ads.xml";
	private GraphDatabase neo4j;

	public AdsTopicsToDatabaseFiller(ServerConfig config, GraphDatabase neo) {
		this.mongodb = new MongoDatabase(config);
		this.gson = new Gson();
		this.neo4j = neo;
	}

	/**
	 * Fill MongoDB with sample data from an xml file.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void fillDatabase() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(getClass().getClassLoader().getResourceAsStream(xmlFilename));
        doc.getDocumentElement().normalize();
        
        for(int i=0; i<doc.getChildNodes().item(0).getChildNodes().getLength(); i++) {
        	if(doc.getChildNodes().item(0).getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
        		this.addToDatabase(doc.getChildNodes().item(0).getChildNodes().item(i));
        	}
        	
        }
        System.out.println("Sample ads and topics added to MongoDB and Neo4J.");
        mongodb.readAllTopicsInLowercase();
	}
	
	/**
	 * Add Ads and Topics to DB.
	 * 
	 * @param node
	 * @throws UnknownHostException
	 */
	private void addToDatabase(Node node) throws UnknownHostException {
		AdObject ad = this.convertAd(node);
		String json = gson.toJson(ad);

		if(ad!= null && ad.getTopics() != null) {
			for(int i=0; i<ad.getTopics().size(); i++) {
				this.addAdToTopic(ad.getTopics().get(i), ad.getId());
			}
		}
		
		if(json != null) {
			mongodb.writeAd(json);
		}
	}
	
	/**
	 * Add or modify topic to add reference id to ad.
	 * 
	 * @param topic
	 * @param adId
	 */
	private void addAdToTopic(String topic, int adId) {
		try {
			if(mongodb.checkTopicExists(topic)) {
				mongodb.updateTopicAd(topic, adId, true);
			} else {
				ArrayList<Integer> adList = new ArrayList<>();
				adList.add(adId);
				TopicObject topicObject = new TopicObject(topic, adList);
				mongodb.writeTopic(gson.toJson(topicObject));
				neo4j.addTopic(topic);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convert XML node to an ad object, so it can be dealt with easier.
	 * 
	 * @param node
	 * @return
	 */
	private AdObject convertAd(Node node) {
		AdObject ad = null;
		
		int id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
		
		try {
			if(!mongodb.checkAdExists(id)) {
				JAXBContext jc = JAXBContext.newInstance(AdObject.class);
				Unmarshaller u = jc.createUnmarshaller();
				
				ad = (AdObject)u.unmarshal(node);

			}
		} catch (UnknownHostException | DOMException | JAXBException e) {
			e.printStackTrace();
		}

		return ad;
	}	
}
