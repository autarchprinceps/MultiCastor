package testcases.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import program.data.UserInputData;
import program.model.XmlParserConfig;


public class XmlParserTest extends TestCase
{	
	private Logger logger;
	private XmlParserConfig xmlParser;
	
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
	
	@Test
	public void testLoadConfig_1() {
		logger = Logger.getLogger("zisko.multicastor.testcases.model");

		xmlParser = new XmlParserConfig();

		UserInputData userInputData = new UserInputData();
		//String path = "C:\\Users\\development\\Desktop\\testdata\\config\\load\\MultiCastor_1.xml";
		String path = "testdata\\config\\load\\MultiCastor_1.xml";
		
		try {
			xmlParser.loadConfig(path, userInputData);
		} catch (FileNotFoundException e) {
			fail();
			e.printStackTrace();
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		} 
		
		assertEquals("224.0.0.112", userInputData.getDefaultL3GroupAddress());
		assertEquals("192.168.16.128", userInputData.getDefaultL3InterfaceAddress());
		assertEquals("01:00:5E:00:00:01", userInputData.getDefaultL2GroupAddress());
		assertEquals("00:0c:29:23:48:3b", userInputData.getDefaultL2InterfaceAddress());
		assertEquals("4711", userInputData.getDefaultL3UdpPort().toString());
		assertEquals("50", userInputData.getDefaultL3TTL().toString());
		assertEquals("112", userInputData.getDefaultL3PacketRate().toString());
		assertEquals("1024", userInputData.getDefaultL3PacketLength().toString());
		assertEquals("112", userInputData.getDefaultL2PacketRate().toString());
		assertEquals("1024", userInputData.getDefaultL2PacketLength().toString());
		assertEquals("de", userInputData.getLanguage().toString());
	}

	@Test
	public void testSaveConfig_1() throws SAXException, IOException {
		logger = Logger.getLogger("zisko.multicastor.testcases.model");
		
		xmlParser = new XmlParserConfig();
		
		//String path = "C:\\Users\\development\\Desktop\\testdata\\config\\save\\MultiCastor_1.xml";
		String path = "testdata\\config\\save\\MultiCastor_1.xml";
		UserInputData userInputData = generateUserInputData();
		
		saveConfig(userInputData, path);
		
		Document doc = parseDocument(path);
		
		validateLoadUserInputData(doc, userInputData);
	}
		
	private void saveConfig(UserInputData userInputData, String path) {
		try {
			xmlParser.saveConfig(path, userInputData);
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
	}

	private UserInputData generateUserInputData() {
		UserInputData userInputData = new UserInputData();
		
		userInputData.setDefaultL2GroupAddress("01:00:5e:7F:FF:FF");
		userInputData.setDefaultL2InterfaceAddress("00:0c:29:23:48:3b");
		userInputData.setDefaultL2PacketRate(10);
		userInputData.setDefaultL2PacketRate(112);
		userInputData.setDefaultL3GroupAddress("ff00::1");
		userInputData.setDefaultL3InterfaceAddress("fe80::b84e:bd6e:6cbb:3ed7");
		userInputData.setDefaultL3PacketLength(1024);
		userInputData.setDefaultL3PacketRate(20);
		userInputData.setDefaultL3TTL(2);
		userInputData.setDefaultL3UdpPort(4711);
		
		return userInputData;
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

	private void validateLoadUserInputData(Document doc, UserInputData resultData) throws SAXException, FileNotFoundException, IOException
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
			    	case selectedTab: assertEquals(resultData.getSelectedTab(), val); break;
//			    	case selectedUserlevel: assertEquals(resultData.getSelectedUserlevel(), val); break;
			    	case groupadress: assertEquals(resultData.getGroupadress(), val); break;
			    	case networkInterface: assertEquals(resultData.getNetworkInterface(), val); break;
			    	case port: assertEquals(resultData.getPort(), val); break;
			    	case ttl: assertEquals(resultData.getTtl(), val); break;
			    	case packetrate: assertEquals(resultData.getPacketrate(), val); break;
			    	case packetlength: assertEquals(resultData.getPacketlength(), val); break;
			    	case activeButton: assertEquals(resultData.getActiveButton(), val); break;
			    	case selectedRows: assertEquals(resultData.getSelectedRows(), val); break;
//			    	case columnOrderString: assertEquals(resultData.getColumnOrderString(), val); break;
	//		    	case columnVisibilityString: assertEquals(resultData.getColumnVisibilityString(), val); break;
			    	case isAutoSaveEnabled: assertEquals(resultData.getIsAutoSaveEnabled(), val); break;
			    	case DefaultL2GroupAddress: assertEquals(resultData.getDefaultL2GroupAddress(), val); break;
			    	case DefaultL2InterfaceAddress: assertEquals(resultData.getDefaultL2InterfaceAddress(), val); break;
			    	case DefaultL2PacketLength: assertEquals(resultData.getDefaultL2PacketLength().toString(), val); break;
			    	case DefaultL2PacketRate: assertEquals(resultData.getDefaultL2PacketRate().toString(), val); break;
			    	case DefaultL3GroupAddress: assertEquals(resultData.getDefaultL3GroupAddress(), val); break;
			    	case DefaultL3InterfaceAddress: assertEquals(resultData.getDefaultL3InterfaceAddress(), val); break;
			    	case DefaultL3PacketLength: assertEquals(resultData.getDefaultL3PacketLength().toString(), val); break;
			    	case DefaultL3PacketRate: assertEquals(resultData.getDefaultL3PacketRate().toString(), val); break;
			    	case DefaultL3TTL: assertEquals(resultData.getDefaultL3TTL().toString(), val); break;
			    	case DefaultL3UdpPort: assertEquals(resultData.getDefaultL3UdpPort().toString(), val); break;
			    	case Language: assertEquals(resultData.getLanguage(), val); break;
			    	case Date: break;
			    	case Host: break;
			    }
			}//end of if
	    }
	}
}