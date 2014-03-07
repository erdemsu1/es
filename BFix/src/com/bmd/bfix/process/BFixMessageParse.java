package com.bmd.bfix.process;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bmd.bfix.model.BFixMessage;
public class BFixMessageParse {
	
	public ArrayList<BFixMessage> FixMessageParse(String messages) {

		ArrayList<BFixMessage> messageParseList=new ArrayList<BFixMessage>();
		String[] spaceTokens = messages.split(" ");
		for (String t : spaceTokens){
			  String[] equalTokens=t.split("=",2);
			  BFixMessage fixMessages = new BFixMessage(Integer.parseInt(equalTokens[0]),equalTokens[1],XMLParse(equalTokens[0]));
			  messageParseList.add(fixMessages);   
		}	
		return messageParseList;
	
	}
	public String FixMessageParseByString(String messages) {
		StringBuilder strbld = new StringBuilder();
		String[] spaceTokens = messages.split("");
		for (String t : spaceTokens){
			  String[] equalTokens=t.split("=",2);
			  BFixMessage fixMessages = new BFixMessage(Integer.parseInt(equalTokens[0]),equalTokens[1],XMLParse(equalTokens[0]));
			  strbld.append(fixMessages.getMean());
			  strbld.append(" : ");
			  strbld.append(fixMessages.getValue());
			  strbld.append("\n");
			  
		}	
		return strbld.toString();
	}
	private String XMLParse(String ID) {
		try {
			File fXmlFile = new File("order.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile); 
			doc.getDocumentElement().normalize();//"/n" ifadelerini yok sayar...
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("DATA_RECORD"); 			 
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//System.out.println("Staff id : " + eElement.getAttribute("id"));
					if (eElement.getElementsByTagName("ID").item(0).getTextContent().equals(ID))
						return eElement.getElementsByTagName("VALUE").item(0).getTextContent();
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return ID+" nolu mesaj tipi XML içinde tanımlı değildir...";
	}
}
