package zisko.multicastor.program.model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.UserInputData;
import zisko.multicastor.program.data.UserlevelData;

/**
 * @author Daniela Gerz
 *
 */

public class xmlParser implements zisko.multicastor.program.interfaces.XMLParserInterface
{
	private Logger logger;
	private enum mcdTag{
		active, groupIp, sourceIp, udpPort, packetLength, ttl,
		packetRateDesired, typ,
	}
	private enum uldTag{
		startButton, stopButton, newButton, selectAllButton,
		deselectAllButton, deleteButton, statusBar, controlPanel, 
		configPanel, groupIpField, sourceIpField, portField,
		packetLengthField, ttlField, packetRateField, activeField,
		enterField, saveConfigDialog, loadConfigDialog, 
		userLevelRadioGrp, autoSaveCheckbox, snakeGame, 
		popupsEnabled, startStopCheckBox, graph, 
		graphTyp, console, typ, userlevel
	}
	private enum uidTag{
		selectedTab, selectedUserlevel, groupadress,
		networkInterface, port, ttl, packetrate, 
		packetlength,activeButton, selectedRows,
		columnOrderString, columnVisibilityString,
		isAutoSaveEnabled
	}
	
	public xmlParser(Logger logger){
		this.logger = logger;
	}
	
	public void loadConfig(String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2, Vector<UserInputData> v3, Vector<String> v4) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException
	{
		Document doc = parseDocument(pfad);
			    
		/*
		 * Ab hier die Daten aus dem XML 
		 * ausgelesen und in Vektor Objekte gespeichert.
		 * Die Daten werden in folgender Reihe ausgelesen
		 * 1. MultiCastData (<mc> im XML ) in den Vektor v1
		 * 2. UserLevelData (<ud> im XML ) in den Vektor v2
		 * 3. Pfade  ( <Paths> im XML ) in den Vektor v3
		 */
		
		if(v1 != null){
			loadMulticastData(doc, v1);
		}
		if(v2 != null){
			loadUserlevelData(doc, v2);
		}
		if(v3 != null){
			loadUserInputData(doc,v3);
		}
		if(v3 != null){
			loadPathData(doc, v4);
		}
		
	}
	
	public void loadConfig( String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2 ) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException
	{
		loadConfig(pfad, v1, v2, null, null);
	}
	
	private void loadMulticastData(Document doc, Vector<MulticastData> v1) throws WrongConfigurationException
	{    
			    //********************************************************
			    // Lese die MultiCast Konfigurationsdaten aus dem XML
			    //********************************************************
		
			    //Suche den Tag Multicasts
			    NodeList multicasts = doc.getElementsByTagName("Multicasts");
			    
			    //Der Tag "Multicasts" ist nur 1 Mal vorhanden,
			    //wenn das XML korrekt ist
			    if(multicasts.getLength()==1)
			    {
			    	//Lösche bisherige Einstellungen
				    v1.clear();
				    
			    	//Erstelle neues MulticastData Objekt
			    	MulticastData mcd;
			    	
			    	//Finde alle ChildNodes von Multicasts
			    	//Diese sind SENDER_V4, RECEIVER_V4, SENDER_V6, RECEIVER_V6 oder UNDEFINED
			    	NodeList mcList = multicasts.item(0).getChildNodes();
			    	
			    	//Zähle alle Multicast Tags
			    	int mcNummer = 0;
			    	
			    	//Iteration SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
			    	for(int i=0; i<mcList.getLength(); i++)
			    	{   
			    		//Evaluiere nur SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
			    		if(mcList.item(i).getNodeName().equals("#text")){
			    			//System.out.println("raus:"+mcList.item(i).getNodeName());
			    			continue;
			    		}
			    		mcNummer++;
			    		
			    		//System.out.println("NODENAME:"+mcList.item(i).getNodeName());
			    		//System.out.println("NR"+(mcNummer));
			    		
			    		mcd = new MulticastData();
			    		Node configNode;
				    	Element configValue;
				    	
			    		//Finde alle ChildNodes des momentanen SENDER/RECEIVER Knoten
			    		NodeList configList=mcList.item(i).getChildNodes();
			    		//Iteration über alle Child Nodes des momentanen SENDER/RECEIVER Knoten
			    		for(int j=0; j<configList.getLength(); j++)
			    		{
				    		configNode=configList.item(j);

				    		if(configNode.getNodeType()== Node.ELEMENT_NODE)
					    	{
					    		configValue = (Element)configNode;
					    		String val = configValue.getTextContent();
					    		String stag = configValue.getTagName();
					    		mcdTag tag = mcdTag.valueOf(stag);
					    			
					    		switch(tag)
					    		{
					    			case active: if(val.equals("true"))
					    					{
					    						mcd.setActive(true);
					    						break;
					    					}
					    					else
					    					{
					    						mcd.setActive(false);
					    						break;
					    					}
					    			case groupIp: if(!val.isEmpty()) {
							   					if(InputValidator.checkMC_IPv4(val) != null )
							   					{	
							   						Inet4Address adr = ( Inet4Address ) InputValidator.checkMC_IPv4(val);
							   						mcd.setGroupIp(adr);
							   						break;
							   					}
							   					else if(InputValidator.checkMC_IPv6(val) != null )
							   					{
							   						Inet6Address adr = ( Inet6Address ) InputValidator.checkMC_IPv6(val);
							   						mcd.setGroupIp(adr);
							   						break;
							   					}
							   					else{
							   						throwWrongContentException(stag,val,mcNummer);
						    					}
					    					}
					    					else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    						throwEmptyContentException(stag, val, mcNummer);
					    					}
					    			case sourceIp: if(!val.isEmpty()){
							   					if(InputValidator.checkIPv4(val) != null )
							   					{
							   						Inet4Address adr = ( Inet4Address ) InputValidator.checkIPv4(val);
							   						if(InputValidator.checkv4Adapters(adr)==true){
							   							mcd.setSourceIp(adr);
							   						}
							   						else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
							   							logger.log(Level.WARNING, "Invalid Network Adapter. Network Interface has been set to localhost.");
							   							adr = ( Inet4Address ) InputValidator.checkIPv4("127.0.0.1");
							   							mcd.setSourceIp(adr);
							   						}
							   						break;
							   					}
							   					else if(InputValidator.checkIPv6(val) != null )
							   					{
							   						Inet6Address adr = ( Inet6Address ) InputValidator.checkIPv6(val);
							   						if(InputValidator.checkv6Adapters(adr)==true){
							   							mcd.setSourceIp(adr);
							   						}
							   						else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
							   							logger.log(Level.WARNING,"Invalid Network Adapter. Network Interface has been set to localhost.");
							   							adr = ( Inet6Address ) InputValidator.checkIPv6("::1");
							   							mcd.setSourceIp(adr);
							   						}
							   						break;
							   					}
							   					else{
							   						throwWrongContentException(stag, val, mcNummer);
						    					}
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case udpPort: if(!val.isEmpty()) {
					    						if(InputValidator.checkPort(val)>0)
					    							mcd.setUdpPort(Integer.parseInt(val)); 
					    						else{
					    							throwWrongContentException(stag, val, mcNummer);
						    					}
					    						break;
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case packetLength: if(!val.isEmpty()){
					    				if(InputValidator.checkIPv4PacketLength(val) > 0 || InputValidator.checkIPv6PacketLength(val) > 0)
					    					mcd.setPacketLength(Integer.parseInt(val)); 
					    				else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    					throwWrongContentException(stag, val, mcNummer);
				    					}
					    				break;
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case ttl: if(!val.isEmpty()){
					    				if(InputValidator.checkTimeToLive(val)>0)
					    						mcd.setTtl(Integer.parseInt(val)); 
					    				else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    					throwWrongContentException(stag, val, mcNummer);
				    					}
					    				break;
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case packetRateDesired: if(!val.isEmpty()){
					    				if(InputValidator.checkPacketRate(val)>0)
					    						mcd.setPacketRateDesired(Integer.parseInt(val)); 
					    				else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    					throwWrongContentException(stag, val, mcNummer);
				    					}
					    				break;
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case typ: if(!val.isEmpty()) 
					    				{
					    					MulticastData.Typ typ = MulticastData.Typ.valueOf(val);
					    					if(typ != null){
					    						mcd.setTyp(typ);
					    					}
					    					else{
					    						throwWrongContentException(stag, val, mcNummer);
					    					}
					    					break;
					    				}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){
					    					throwEmptyContentException(stag, val, mcNummer);
				    					}
					    		}
					    	} //end of if
			    		}//end of for
			    		v1.add(mcd);
			    	}//end of for
			    }//end of if
	}	


	private void loadUserlevelData(Document doc, Vector<UserlevelData> v2)
	{
			    //********************************************************
			    // Lese die Userlevel Konfigurationsdaten aus dem XML
			    //********************************************************
			    
		
			    //Wenn Userlevel Konfigurationen vorhanden
			    NodeList list = doc.getElementsByTagName("UserlevelData");
		
			    if(list.getLength()==1)
			    {
			    	//Lösche bisherige Einstellungen
				    v2.clear();
				    
			    	//Erstelle neues Userlevedata Objekt
			    	UserlevelData uld;
				    	
				    //Finde alle ChildNodes von UserlevelData
				   	//Diese sind SENDER_V4, RECEIVER_V4, SENDER_V6, RECEIVER_V6 oder UNDEFINED
				    NodeList uldList = list.item(0).getChildNodes();
				    
					//Iteration SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
			    	for(int i=0; i<uldList.getLength(); i++)
			    	{   
			    		//Evaluiere nur SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
			    		if(uldList.item(i).getNodeName()=="#text"){
			    			continue;
			    		}

			    		uld = new UserlevelData();
			    		Node configNode;
				    	Element configValue;
				    	
			    		//Finde alle ChildNodes des momentanen SENDER/RECEIVER Knoten
			    		NodeList configList=uldList.item(i).getChildNodes();
			    		//Iteration über alle Child Nodes des momentanen SENDER/RECEIVER Knoten
			    		for(int j=0; j<configList.getLength(); j++)
			    		{
			    			
			    			configNode=configList.item(j);

				    		if(configNode.getNodeType()== Node.ELEMENT_NODE)
					    	{
					    		configValue = (Element)configNode;
					    		String val = configValue.getTextContent();
					    		String stag = configValue.getTagName();
					    		uldTag tag = uldTag.valueOf(stag);
				    			Boolean b;
				    			
				    			if(val.equals("true"))
				    			{
				    				b = true;
				    			}
				    			else
				    			{
				    				b = false;
				    			}
				    			
				    			switch(tag)
				    			{
				    				case startButton: uld.setStartButton(b); break;
				    				case stopButton: uld.setStopButton(b); break;
				    				case newButton: uld.setNewButton(b); break;
				    				case selectAllButton: uld.setSelectAllButton(b); break;
				    				case deselectAllButton: uld.setDeselectAllButton(b); break;
				    				case deleteButton: uld.setDeleteButton(b); break;
				    				case statusBar: uld.setStatusBar(b); break;
				    				case controlPanel: uld.setControlPanel(b); break;
				    				case configPanel: uld.setConfigPanel(b); break;
				    				case groupIpField: uld.setGroupIpField(b); break;
				    				case sourceIpField: uld.setSourceIpField(b); break;
				    				case portField: uld.setPortField(b); break;
				    				case packetLengthField: uld.setPacketLengthField(b); break;
				    				case ttlField: uld.setTtlField(b); break;
				    				case packetRateField: uld.setPacketRateField(b); break;
				    				case activeField: uld.setActiveField(b); break;
				    				case enterField: uld.setEnterField(b); break;
				    				case saveConfigDialog: uld.setSaveConfigDialog(b); break;
				    				case loadConfigDialog: uld.setLoadConfigDialog(b); break;
				    				case userLevelRadioGrp: uld.setUserLevelRadioGrp(b); break;
				    				case autoSaveCheckbox: uld.setAutoSaveCheckbox(b); break;
				    				case snakeGame: uld.setSnakeGame(b); break;
				    				case popupsEnabled: uld.setPopupsEnabled(b); break;
				    				case startStopCheckBox: uld.setStartStopCheckBox(b); break;
				    				case graph: uld.setGraph(b); break;
				    				case graphTyp: if(!val.isEmpty()) {
				    						UserlevelData.Graph typ = UserlevelData.Graph.valueOf(val);
				    						uld.setGraphTyp(typ);
				    						}
				    						break;
				    				case console: uld.setConsole(b); break;
				    				case typ: if(!val.isEmpty()) {
				    						MulticastData.Typ typ = MulticastData.Typ.valueOf(val);
				    						uld.setTyp(typ);
			    							}
				    						break;
				    				case userlevel: if(!val.isEmpty()) {
		    								UserlevelData.Userlevel userlevel = UserlevelData.Userlevel.valueOf(val);
		    								uld.setUserlevel(userlevel);
	    								}
				    					break;
				    			}//Switch Ende
				    		}//End of if
			    		}//End of inner for
			    		v2.add(uld);
			    	}//End of outer for
			    }
			 }		 
	
	private void loadUserInputData(Document doc, Vector<UserInputData> v3) throws SAXException, FileNotFoundException, IOException
	{
		
		//Suche den Tag Multicasts
	    NodeList userinputdata = doc.getElementsByTagName("UserInputData");
	    
	    //Der Tag "Multicasts" ist nur 1 Mal vorhanden,
	    //wenn das XML korrekt ist
	    if(userinputdata.getLength()==1)
	    {
	    	//Lösche bisherige Einstellungen
		    v3.clear();
		    
	    	//Erstelle neues MulticastData Objekt
	    	UserInputData uid;
	    	
	    	//Finde alle ChildNodes von Multicasts
	    	//Diese sind SENDER_V4, RECEIVER_V4, SENDER_V6, RECEIVER_V6 oder UNDEFINED
	    	NodeList uidList = userinputdata.item(0).getChildNodes();
	    	
	    	
	    	//Iteration SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
	    	for(int i=0; i<uidList.getLength(); i++)
	    	{   
	    		//Evaluiere nur SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
	    		if(uidList.item(i).getNodeName()=="#text"){
	    			continue;
	    		}
	    		
	    		uid = new UserInputData();
	    		Node configNode;
		    	Element configValue;
		    	
	    		//Finde alle ChildNodes des momentanen SENDER/RECEIVER Knoten
	    		NodeList configList=uidList.item(i).getChildNodes();
	    		
	    		//Iteration über alle Child Nodes des momentanen SENDER/RECEIVER Knoten
	    		for(int j=0; j<configList.getLength(); j++)
	    		{
		    		configNode=configList.item(j);

		    		if(configNode.getNodeType()== Node.ELEMENT_NODE)
			    	{
			    		configValue = (Element)configNode;
			    		String val = configValue.getTextContent();
			    		String stag = configValue.getTagName();
			    		uidTag tag = uidTag.valueOf(stag);
				    			//Füge den Inhalt des Elementes dem uid-Objekt hinzu
				    			switch(tag)
				    			{
				    				case selectedTab: uid.setSelectedTab(val); break;
				    				case selectedUserlevel: uid.setSelectedUserlevel(val); break;
				    				case groupadress: uid.setGroupadress(val); break;
				    				case networkInterface: uid.setNetworkInterface(val); break;
				    				case port: uid.setPort(val); break;
				    				case ttl: uid.setTtl(val); break;
				    				case packetrate: uid.setPacketrate(val); break;
				    				case packetlength: uid.setPacketlength(val); break;
				    				case activeButton: uid.setActiveButton(val); break;
				    				case selectedRows: uid.setSelectedRows(val); break;
				    				case columnOrderString: uid.setColumnOrderString(val); break;
				    				case columnVisibilityString: uid.setColumnVisibilityString(val);
				    				case isAutoSaveEnabled: uid.setIsAutoSaveEnabled(val); break;
				    			}
				    }//end of if
				   
			    }//end of for
	    		 //Füge das uid-Objekt dem Vektor v3 hinzu
			     v3.add(uid);
		    }
		}
	}
	
	private void loadPathData(Document doc, Vector<String> v3)
	{
			NodeList list = doc.getElementsByTagName("PathData");
			 if(list.getLength()==1)
			    {
			    	//Lösche bisherige Einstellungen
				    v3.clear();
				    
				    //Variablen
				    Node configNode;
				    Element configValue;
			    	
			    	//Lese die Werte der Konfigurationen aus
				    NodeList configList = list.item(0).getChildNodes();
		
			    		for(int i=0; i<configList.getLength(); i++)
			    		{
			    			 //Evaluiere nur gültige Nodes
				    		if(configList.item(i).getNodeName()=="#text"){
				    			continue;
				    		}
				    		//System.out.println(configList.item(i).getNodeName());
			    			configNode=configList.item(i);
			    			if(configNode.getNodeType()== Node.ELEMENT_NODE)
				    		{
				    			configValue = (Element)configNode;
				    			String val = configValue.getTextContent();
				    			v3.add(val);
				    		}
			    		}
			    }
	}
	
	public void saveConfig(String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2, Vector<UserInputData> v3, Vector<String> v4) throws IOException
	{
 
			//Erzeuge ein neues XML Dokument
			Document doc = createDocument();
	         
	        //Erzeuge das Root Element des XML Dokumentes, "MultiCastor".
	        Element multiCastor = doc.createElement("MultiCastor");
	        doc.appendChild(multiCastor);
	        
	        /*
	         * Ab hier wird der XML Baum erstellt und Daten hinzugefügt.
	         * Die Erstellung erfolgt in folgender Reihenfolge:
	         * 1. MultiCastData ( Daten aus Vektor v1 )
	         * 2. UserLevelData ( Daten aus Vektor v2 )
	         * 3. UserInputData ( Daten aus Vektor v3 )
	         * 3. Pfade  ( Daten aus Vektor v4 )
	         */
	         if(v1 != null){
	        	 saveMulticastData(doc, multiCastor, v1);
	         }
	         if(v2 != null){
	        	 saveUserlevelData(doc, multiCastor, v2);
	         }
	         if(v3 != null){
	        	 saveUserInputData(doc, multiCastor, v3);
	         }
	         if(v4 != null){
	        	 savePathData(doc, multiCastor, v4);
	         }
   
	       //##### XML Ausgabe #####

	         //Erzeuge einen Transformer
		     Transformer transformer = setupTransformer();

	         //Erstelle einen String aus dem XML Baum
		     String xmlString = XMLtoString(doc, transformer);
	         
	         //print xml to console
	         //System.out.println("Here's the xml:\n\n" + xmlString);
	         
	 		File xmlfile= new File(pfad);
		 	BufferedWriter writer = null;
	 		writer = new BufferedWriter(new FileWriter(xmlfile));
	 		String prolog="<?xml version=\"1.0\"?>\n";
	 		writer.write(prolog+xmlString);
	 		writer.close();
	 	
	}
	
	public void saveConfig(String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2) throws IOException
	{
		saveConfig(pfad, v1, v2, null, null);
	}
	
	private void saveMulticastData(Document doc, Element root, Vector<MulticastData> v1)
	{	//********************************************************
	    // Schreibe die MultiCast Konfigurationsdaten in das XML
	    //********************************************************

		 //Erzeugt Root Element für die MultiCast Konfigurationen
         Element multicasts = doc.createElement("Multicasts");
         root.appendChild(multicasts);
         
        //Für alle verschiedenen Konfigurationen
	         for(int count=0; count<v1.size();count++)
	         {
	        	 //Ermittle den Typ ( (IPv4|IPv6)(Sender|Receiver) )
	        	 //Füge dementsprechend ein Kind Element hinzu
	        	 MulticastData.Typ typ = v1.get(count).getTyp();
	        	 Element mcdTyp = doc.createElement(typ.toString());
	        	 multicasts.appendChild(mcdTyp);
	        	 
		         //Für alle vorhandenen Einstellungen
	        	 for(mcdTag tag : mcdTag.values())
		         {
		        	 Element mcdElement = doc.createElement(tag.toString());
		        	 Text text =doc.createTextNode("");
		             Integer converter = new Integer(0);
		             
		        	 switch(tag)
		        	 {
		        	 	case active: Boolean b = new Boolean(v1.get(count).isActive());
		        	 			text = doc.createTextNode(b.toString());
		        	 			break;
		        	 	case groupIp: if(v1.get(count).getGroupIp()!=null)
		        	 				text = doc.createTextNode(v1.get(count).getGroupIp().getHostAddress());
		        	 			break;
		        	 	case sourceIp: if(v1.get(count).getSourceIp()!=null)
		        	 				text = doc.createTextNode(v1.get(count).getSourceIp().getHostAddress());
		        	 			break;
		        	 	case udpPort: converter = v1.get(count).getUdpPort();
		        	 			text = doc.createTextNode(converter.toString());
		        	 			break;
		        	 	case packetLength: converter = v1.get(count).getPacketLength();
		        	 			text = doc.createTextNode(converter.toString());
		        	 			break;
		        	 	case ttl: converter = v1.get(count).getTtl();
		        	 			text = doc.createTextNode(converter.toString());
		        	 			break;
		        	 	case packetRateDesired: converter = v1.get(count).getPacketRateDesired();
		        	 			text = doc.createTextNode(converter.toString());
		        	 			break;
		        	 	case typ: text = doc.createTextNode(v1.get(count).getTyp().toString());
		        	 			break;
		        	 }
		        	 mcdElement.appendChild(text);
		        	 mcdTyp.appendChild(mcdElement);
		        	 
		         }
	        	 
	         }
	}
	
	private void saveUserlevelData(Document doc, Element root, Vector<UserlevelData> v2)
	{	 //********************************************************
		 // Schreibe die UserLevel Konfigurationsdaten in das XML
		 //********************************************************  
		
		//Erzeugt Root Element für die UserLevel Konfigurationsdaten
        Element userLevelData = doc.createElement("UserlevelData");
        root.appendChild(userLevelData);
        
       //Für alle verschiedenen Konfigurationen
	         for(int count=0; count<v2.size(); count++)
	         {
	        	 //Ermittle den Typ ( (IPv4|IPv6)(Sender|Receiver) )
	        	 //Füge dementsprechend ein Kind Element hinzu
	        	 MulticastData.Typ typ = v2.get(count).getTyp();
	        	 Element uldTyp = doc.createElement(typ.toString());
	        	 userLevelData.appendChild(uldTyp);
  
		         //Für alle vorhandenen Einstellungen
	        	 for(uldTag tag: uldTag.values())
		         {
		        	 //Benötigte Variablen
		        	 Element uldElement = doc.createElement(tag.toString());
		        	 Text text = doc.createTextNode("");
		             Boolean b = false;
	
		        	 switch(tag)
		        	 {
		        	 	case startButton: b = v2.get(count).isStartButton(); text = doc.createTextNode(b.toString()); break;
		        	 	case stopButton: b = v2.get(count).isStopButton(); text = doc.createTextNode(b.toString()); break;
		        	 	case newButton: b = v2.get(count).isNewButton(); text = doc.createTextNode(b.toString()); break;
		        	 	case selectAllButton: b = v2.get(count).isSelectAllButton(); text = doc.createTextNode(b.toString()); break;
		        	 	case deselectAllButton: b = v2.get(count).isDeselectAllButton(); text = doc.createTextNode(b.toString()); break;
		        	 	case deleteButton: b = v2.get(count).isDeleteButton(); text = doc.createTextNode(b.toString()); break;
		        	 	case statusBar: b=v2.get(count).isStatusBar();text = doc.createTextNode(b.toString()); break;
		        	 	case controlPanel: b=v2.get(count).isControlPanel();text = doc.createTextNode(b.toString()); break;
		        	 	case configPanel: b=v2.get(count).isConfigPanel();text = doc.createTextNode(b.toString()); break;
		        	 	case groupIpField: b = v2.get(count).isGroupIpField(); text = doc.createTextNode(b.toString()); break;
		        	 	case sourceIpField: b = v2.get(count).isSourceIpField(); text = doc.createTextNode(b.toString()); break;
		        	 	case portField: b = v2.get(count).isPortField(); text = doc.createTextNode(b.toString()); break;
		        	 	case packetLengthField:b = v2.get(count).isPacketLengthField(); text = doc.createTextNode(b.toString()); break;
		        	 	case ttlField:b = v2.get(count).isTtlField(); text = doc.createTextNode(b.toString()); break;
		        	 	case packetRateField:b = v2.get(count).isPacketRateField(); text = doc.createTextNode(b.toString()); break;
		        	 	case activeField:b = v2.get(count).isActiveField(); text = doc.createTextNode(b.toString()); break;
		        	 	case enterField:b = v2.get(count).isEnterField(); text = doc.createTextNode(b.toString()); break;
		        	 	case saveConfigDialog: v2.get(count).isSaveConfigDialog();text = doc.createTextNode(b.toString()); break;
		        	 	case loadConfigDialog: v2.get(count).isLoadConfigDialog();text = doc.createTextNode(b.toString()); break;
		        	 	case userLevelRadioGrp: v2.get(count).isUserLevelRadioGrp();text = doc.createTextNode(b.toString()); break;
		        	 	case autoSaveCheckbox: v2.get(count).isAutoSaveCheckbox();text = doc.createTextNode(b.toString()); break;
		        	 	case snakeGame: v2.get(count).isSnakeGame();text = doc.createTextNode(b.toString()); break;
		        	 	case popupsEnabled: v2.get(count).isPopupsEnabled();text = doc.createTextNode(b.toString()); break;
		        	 	case startStopCheckBox: v2.get(count).isStartStopCheckBox();text = doc.createTextNode(b.toString()); break;
		        		case graph:b = v2.get(count).isGraph(); text = doc.createTextNode(b.toString()); break;
		        		case graphTyp: UserlevelData.Graph graph = v2.get(count).getGraphTyp();
		        				 switch(graph)
		        				 {
		        				 	case UNDEFINED: text = doc.createTextNode("UNDEFINED"); break;
		        				 	case JITTER: text = doc.createTextNode("JITTER"); break;
		        				 	case LOST_PACKETS: text = doc.createTextNode("LOST_PACKETS"); break;
		        				 	case MEASURED_PACKET_RATE: text = doc.createTextNode("MEASURED_PACKET_RATE"); break;
		        				 }
		        				 break;
		        		case console:b = v2.get(count).isConsole();text = doc.createTextNode(b.toString());  break;
		        		case userlevel: UserlevelData.Userlevel userlevel = v2.get(count).getUserlevel();
		        				switch(userlevel)
		        				{
		        					case UNDEFINED: text = doc.createTextNode("UNDEFINED"); break;
		        					case BEGINNER: text = doc.createTextNode("BEGINNER"); break;
		        					case EXPERT: text = doc.createTextNode("EXPERT"); break;
		        					case CUSTOM: text = doc.createTextNode("CUSTOM"); break;
		        				}
		        				break;
		        	 }	 
		        	 
		        	 if(!text.equals("")) //Wenn Tag-Inhalt vorhanden
		        	 {
		        		//Füge Tag + Inhalt ins XML ein
		        		 uldElement.appendChild(text);
			        	 uldTyp.appendChild(uldElement);
		        		 
		        	 }
		        	 
		         }
	         }
	}
	/* Speichert momentanen Status des GUI inklusive Inhalt aller Eingabefelder in einer XML-Datei ab.
	 */
	
	private void saveUserInputData(Document doc, Element root, Vector<UserInputData> v3) throws IOException
	{
		 //Erzeugt Root Element für die MultiCast Konfigurationen
        Element userinputdata = doc.createElement("UserInputData");
        root.appendChild(userinputdata);
        
       //Für alle verschiedenen Konfigurationen
	         for(int count=0; count<v3.size();count++)
	         {
	        	 UserInputData uid = v3.get(count);
	        	 
	        	 //Ermittle den Typ ( (IPv4|IPv6)(Sender|Receiver) )
	        	 //Füge dementsprechend ein Kind Element hinzu
	        	 String typ = uid.getSelectedTab();
	        	 Element uidTyp = doc.createElement(typ);
	        	 userinputdata.appendChild(uidTyp);
	        	 
		         //Für alle vorhandenen Einstellungen
	        	 for(uidTag tag : uidTag.values())
		         {
		        	 Element uidElement = doc.createElement(tag.toString());
		        	 Text text =doc.createTextNode("");
		             
		        		 switch(tag)
		        		 {
		        		 	case selectedTab: text = doc.createTextNode(uid.getSelectedTab()); break;
		        		 	case selectedUserlevel: text = doc.createTextNode(uid.getSelectedUserlevel()); break;
		        		 	case groupadress: text = doc.createTextNode(uid.getGroupadress()); break;
		        		 	case networkInterface: text = doc.createTextNode(uid.getNetworkInterface()); break;
		        		 	case port: text = doc.createTextNode(uid.getPort()); break;
		        		 	case ttl: text = doc.createTextNode(uid.getTtl()); break;
		        		 	case packetrate: text = doc.createTextNode(uid.getPacketrate()); break;
		        		 	case packetlength: text = doc.createTextNode(uid.getPacketlength()); break;
		        		 	case activeButton: text = doc.createTextNode(uid.getActiveButton()); break;
		        		 	case selectedRows: text = doc.createTextNode(uid.getSelectedRows()); break;
		        		 	case columnOrderString: text = doc.createTextNode(uid.getColumnOrderString()); break;
		        		 	case columnVisibilityString: text = doc.createTextNode(uid.getColumnVisibilityString()); break;
		        		 	case isAutoSaveEnabled: text=doc.createTextNode(uid.getIsAutoSaveEnabled()); break;
		        		 }
		        		 uidElement.appendChild(text);
			        	 uidTyp.appendChild(uidElement);
		        	 }
			        
			      }	
	}
	

	private void savePathData(Document doc, Element root, Vector<String> v3)
	{	//********************************************************
		// Schreibe Speicherpfad Konfigurationsdaten in das XML
	    //********************************************************  
			
		/*
		 * Hier entsteht ein XML der Form
		 * <PathData>
		 * 	<RecentFile1></RecentFile1>
		 * 	<RecentFile2></RecentFile2>
		 * 	<RecentFile3></RecentFile3>
		 * </PathData>
		 */
		
		//Fügt das oberste Element, "PathData", hinzu.
		Element pfade = doc.createElement("PathData");
        root.appendChild(pfade);      
        
        Text pfadValue;  
        int size = v3.size();
        Element pfad; 
        
        //Fügt die 3 zuletzt benutzten Konfigurationsdateien dem XML hinzu.
        for(int count=0;count<v3.size();count++)
        {
        	if(size>=count)
        	{
        		pfad = doc.createElement("RecentFile"+count);
            	pfade.appendChild(pfad);
            	pfadValue= doc.createTextNode(v3.get(count).toString());
                pfad.appendChild(pfadValue);
        	}
        }
	}
	

	
	/** Liest die automatisch gespeicherte Datei ein
	 * @param v1
	 * @param v2
	 */
	
	public void loadDefaultULD(Vector<MulticastData> v1, Vector<UserlevelData> v2) throws IOException, SAXException, WrongConfigurationException
	{	
		
		Document doc = parseDocumentFromJAR("/zisko/multicastor/resources/configuration/userlevel.xml");
		if(doc != null){	
			loadMulticastData(doc, v1);
			loadUserlevelData(doc, v2);	
		}
		else{
			throw new SAXException();
		}
	}
	
	
	private Document createDocument()
	{	
		//Erzeuge ein neues XML Dokument
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try{
	        documentBuilder = dbFactory.newDocumentBuilder();
		}
		catch( ParserConfigurationException e){
			e.printStackTrace();
		}
		Document doc = documentBuilder.newDocument();
		return doc;
	}
	
	private Document parseDocument(String pfad) throws SAXException, IOException
	{	
		//Erzeuge ein neues XML Dokument
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		Document doc = null;
		
		try
		{
				documentBuilder = dbFactory.newDocumentBuilder();
		        doc = documentBuilder.parse(pfad);
			
		}
		catch( ParserConfigurationException e){
			e.printStackTrace();
		}
		
		return doc;
	}
	
	private Document parseDocumentFromJAR(String pfad) throws SAXException, IOException
	{
		InputStream is = getClass( ).getResourceAsStream(pfad);
		
		//Erzeuge ein neues XML Dokument
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		Document doc = null;
		
		try
		{
			documentBuilder = dbFactory.newDocumentBuilder();
		    doc = documentBuilder.parse(is);
		}catch( ParserConfigurationException e){
			e.printStackTrace();
		}
		
		return doc;
		
	}

	private Transformer setupTransformer()
	{

		 TransformerFactory transFactory = TransformerFactory.newInstance();
		 transFactory.setAttribute("indent-number", 4);
         Transformer transformer = null;
         try{
        	 transformer = transFactory.newTransformer();
         }catch(TransformerConfigurationException e){
        	 e.printStackTrace();
         }
         transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         
         return transformer;
	}

	private String XMLtoString(Document doc, Transformer transformer)
	{
		StringWriter sWriter = new StringWriter();
        StreamResult result = new StreamResult(sWriter);
        DOMSource source = new DOMSource(doc);
        try{
        	transformer.transform(source, result);
        }
        catch(TransformerException e){
        	e.printStackTrace();
        }
        
        String xmlString = sWriter.toString();
        
        return xmlString;
	}
	
	private void throwWrongContentException(String tag, String value, int multicast) throws WrongConfigurationException {
		WrongConfigurationException ex = new WrongConfigurationException();
		ex.setErrorMessage("Error in configuration file: Value \""+value+"\" of "+tag+" in Multicast "+(multicast+1)+" is wrong.");
		throw ex;
	}
	private void throwEmptyContentException(String tag, String value, int multicast) throws WrongConfigurationException {
		WrongConfigurationException ex = new WrongConfigurationException();
		ex.setErrorMessage("Error in configuration file: Value \""+value+"\" of "+tag+" in Multicast "+(multicast+1)+" is empty.");
		throw ex;
	}
	
}
