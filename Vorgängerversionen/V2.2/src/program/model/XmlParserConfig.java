package program.model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

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

import program.data.UserInputData;

/**
 * @author Daniela Gerz, Alexander Behm
 * This class parses XML files for GUI settings and default settings
 */
public class XmlParserConfig
{
	/**
	 * The possible tags for UserInputData
	 */
	private enum uidTag{
		selectedTab, selectedUserlevel, groupadress,
		networkInterface, port, ttl, packetrate,
		packetlength,activeButton, selectedRows,
		columnOrderString, columnVisibilityString,
		isAutoSaveEnabled, DefaultL3GroupAddress, DefaultL3InterfaceAddress,
		DefaultL2GroupAddress, DefaultL2InterfaceAddress,
		DefaultL3UdpPort , DefaultL3TTL,DefaultL3PacketRate,
		DefaultL3PacketLength, DefaultL2PacketRate,DefaultL2PacketLength,
		Language, Date, Host
	}
	
	/**
	 * Load configuration file
	 * @param pfad The path to the file of the configuration
	 * @param v2
	 * @param v3
	 * @param v4
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws WrongConfigurationException
	 */
	public void loadConfig(String pfad, UserInputData v3) throws SAXException, FileNotFoundException, IOException
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
		if(v3 != null){
			File temp = new File(pfad);
			v3.setFilename(temp.getName());
			loadUserInputData(doc,v3);
		}
		//if(v3 != null){
		//	loadPathData(doc, v4);
		//}

	}

	/**
	 * Loads the UserInputData from doc and sets all found variables in uid
	 * @param doc the document from which the date is loaded
	 * @param uid the UserInputData object to that the information is written
	 * @throws SAXException If the xml is not well formatted
	 */
	private void loadUserInputData(Document doc, UserInputData uid) throws SAXException
	{
		//Suche den Tag UserInputData
	    NodeList userinputdata = doc.getElementsByTagName("UserInputData"); //$NON-NLS-1$

	    NodeList uidList = userinputdata.item(0).getChildNodes();

	    Node configNode;
	    Element configValue;
	    //Iteration SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
	    for(int i=0; i<uidList.getLength(); i++)
	    {
	    	configNode=uidList.item(i);
		    if(configNode.getNodeType()== Node.ELEMENT_NODE)
		    {
		    	configValue = (Element)configNode;
		    	String val = configValue.getTextContent();
		    	String stag = configValue.getTagName();
		    	uidTag tag = uidTag.valueOf(stag);
			    //F�ge den Inhalt des Elementes dem uid-Objekt hinzu
			    switch(tag)
			    {
			    	case selectedTab: uid.setSelectedTab(val); break;
//			    	case selectedUserlevel: uid.setSelectedUserlevel(val); break;
			    	case groupadress: uid.setGroupadress(val); break;
			    	case networkInterface: uid.setNetworkInterface(val); break;
			    	case port: uid.setPort(val); break;
			    	case ttl: uid.setTtl(val); break;
			    	case packetrate: uid.setPacketrate(val); break;
			    	case packetlength: uid.setPacketlength(val); break;
			    	case activeButton: uid.setActiveButton(val); break;
			    	case selectedRows: uid.setSelectedRows(val); break;
//			    	case columnOrderString: uid.setColumnOrderString(val); break;
	//		    	case columnVisibilityString: uid.setColumnVisibilityString(val); break;
			    	case isAutoSaveEnabled: uid.setIsAutoSaveEnabled(val); break;
			    	case DefaultL2GroupAddress: uid.setDefaultL2GroupAddress(val); break;
			    	case DefaultL2InterfaceAddress: uid.setDefaultL2InterfaceAddress(val); break;
			    	case DefaultL2PacketLength: uid.setDefaultL2PacketLength(Integer.parseInt(val)); break;
			    	case DefaultL2PacketRate: uid.setDefaultL2PacketRate(Integer.parseInt(val)); break;
			    	case DefaultL3GroupAddress: uid.setDefaultL3GroupAddress(val); break;
			    	case DefaultL3InterfaceAddress: uid.setDefaultL3InterfaceAddress(val); break;
			    	case DefaultL3PacketLength: uid.setDefaultL3PacketLength(Integer.parseInt(val)); break;
			    	case DefaultL3PacketRate: uid.setDefaultL3PacketRate(Integer.parseInt(val)); break;
			    	case DefaultL3TTL: uid.setDefaultL3TTL(Integer.parseInt(val)); break;
			    	case DefaultL3UdpPort: uid.setDefaultL3UdpPort(Integer.parseInt(val)); break;
			    	case Language: uid.setLanguage(val); break;
			    	case Date: break;
			    	case Host: break;
			    }
			}//end of if
			
	    }
	}

	/**
	 * Save the important information from UserInputData to XML File at pfad
	 * @param pfad The path to save the xml to
	 * @param v3 The object from that the information will be taken
	 * @throws IOException if the file can not be written
	 */
	public void saveConfig(String pfad, UserInputData v3) throws IOException
	{
			//Erzeuge ein neues XML Dokument
			Document doc = createDocument();

	        //Erzeuge das Root Element des XML Dokumentes, "MultiCastor".
	        Element multiCastor = doc.createElement("MultiCastor"); //$NON-NLS-1$
	        doc.appendChild(multiCastor);

	        /*
	         * Ab hier wird der XML Baum erstellt und Daten hinzugef�gt.
	         * Die Erstellung erfolgt in folgender Reihenfolge:
	         * 1. MultiCastData ( Daten aus Vektor v1 )
	         * 2. UserLevelData ( Daten aus Vektor v2 )
	         * 3. UserInputData ( Daten aus Vektor v3 )
	         * 3. Pfade  ( Daten aus Vektor v4 )
	         */

	         /* We dont save Multicast Data in config anymore
	      	 if(v1!=null) {
	        	 saveMulticastData(doc, multiCastor, v1);
	         }
	         */
	         if(v3 != null){
	        	 saveUserInputData(doc, multiCastor, v3);
	         }

	         
	       //##### XML Ausgabe #####

	         //Erzeuge einen Transformer
		     Transformer transformer = setupTransformer();

	         //Erstelle einen String aus dem XML Baum
		     String xmlString = XMLtoString(doc, transformer);

	         //print xml to console
//	         System.out.println("Here's the xml:\n\n" + xmlString); //$NON-NLS-1$

	 		File xmlfile= new File(pfad);
		 	BufferedWriter writer = null;
	 		writer = new BufferedWriter(new FileWriter(xmlfile));
	 		String prolog="<?xml version=\"1.0\"?>\n"; //$NON-NLS-1$
	 		writer.write(prolog+xmlString);
	 		writer.close();
	}

	/**
	 * Save the UserInputData to documnet doc with root-node root
	 * @param doc the document to append the information to
	 * @param root the root element of doc
	 * @param UID the information source
	 */
	private void saveUserInputData(Document doc, Element root, UserInputData UID)
	{
		//Create root Element for Date
        Element date = doc.createElement("Date"); //$NON-NLS-1$
        
        //Add our root Element to the XML file
        root.appendChild(date);
        
        
        Element UIDElement = doc.createElement("Date"); //$NON-NLS-1$
        Text text = doc.createTextNode(new Date().toString());
        date.appendChild(text);
        
        //Get Hostname if possible
        String hostname;
        try {
        	java.net.InetAddress localMachine =	java.net.InetAddress.getLocalHost();
        		hostname = localMachine.getHostName();
        	}
        	catch (java.net.UnknownHostException uhe) {
        		hostname = "unknown";
        	}

        
        //Create root Element for host
        Element Host = doc.createElement("Host"); //$NON-NLS-1$
        
        //Add our root Element to the XML file
        root.appendChild(Host);
        
        
        UIDElement = doc.createElement("Host"); //$NON-NLS-1$
        text = doc.createTextNode(hostname);
        Host.appendChild(text);


		 //Erzeugt Root Element f�r die MultiCast Konfigurationen
        Element userinputdata = doc.createElement("UserInputData"); //$NON-NLS-1$
        root.appendChild(userinputdata);

        UIDElement = doc.createElement("Date"); //$NON-NLS-1$
        text = doc.createTextNode(new Date().toString());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL3GroupAddress"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL3GroupAddress());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL3InterfaceAddress"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL3InterfaceAddress());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL2GroupAddress"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL2GroupAddress());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL2InterfaceAddress"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL2InterfaceAddress());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL3UdpPort"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL3UdpPort().toString());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL3TTL"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL3TTL().toString());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL3PacketRate"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL3PacketRate().toString());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL3PacketLength"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL3PacketLength().toString());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL2PacketRate"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL2PacketRate().toString());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("DefaultL2PacketLength"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getDefaultL2PacketLength().toString());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
        
        UIDElement = doc.createElement("Language"); //$NON-NLS-1$
        text = doc.createTextNode(UID.getLanguage());
        UIDElement.appendChild(text);
        userinputdata.appendChild(UIDElement);
	}
	
	/**
	 * Creates a new and empty document by using DocumentBuilderFactory
	 * @return new empty document
	 */
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

	/**
	 * Parses the document in pfad and returns the parsed xml document
	 * @param pfad The path to the XML document to parse
	 * @return The parsed document
	 * @throws SAXException if the document is not well formatted
	 * @throws IOException if the file does not exist or is not accessible
	 */
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

	/**
	 * sets a transformer for xml up. This is used to convert the xml to string
	 * @return the set up transformer
	 */
	private Transformer setupTransformer()
	{

		 TransformerFactory transFactory = TransformerFactory.newInstance();
		 transFactory.setAttribute("indent-number", 4); //$NON-NLS-1$
         Transformer transformer = null;
         try{
        	 transformer = transFactory.newTransformer();
         }catch(TransformerConfigurationException e){
        	 e.printStackTrace();
         }
         transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
         transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

         return transformer;
	}

	/**
	 * converts an xml node with all of its childs into a string. This can then be used to write an xml file
	 * @param doc the document to transform to string
	 * @param transformer the transformer that should be used
	 * @return the xml as string
	 */
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

}
