package testcases.model;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import program.controller.Messages;
import program.data.MulticastData;
import program.data.MulticastData.Typ;
import program.model.InputValidator;
import program.model.NetworkAdapter;
import program.model.XmlParserStream;

public class XmlParserProfileTest extends TestCase {
	private Logger logger;
	private XmlParserStream xmlParserProfile;
	
	private Vector<MulticastData> multicastDataList;
	Vector<PcapIf> mmrpInterfaces = new Vector<PcapIf>();
	
	private enum mcdTag{
		active, groupAddress, sourceAddress, udpPort, packetLength, ttl,
		packetRateDesired, typ, Date, Host
	}
	
	@Test
	public void testSaveStream_1() {
		logger = Logger.getLogger("zisko.multicastor.testcases.model");
		
		multicastDataList = new Vector<MulticastData>();
		//String path = "C:\\Users\\development\\Desktop\\testdata\\stream\\save\\streams_1.xml";
		String path = "testdata\\stream\\save\\streams_1.xml";
		
		xmlParserProfile = new XmlParserStream(logger);
		
		try {
			setTestDataReceiver();
			setTestDataSender();
			xmlParserProfile.saveConfig(path, multicastDataList);
		} catch (UnknownHostException e) {
			fail();
			e.printStackTrace();
		}catch (IOException e) {
			fail();
			e.printStackTrace();
		}
		
		try {
			Document doc = parseDocument(path);
			loadMulticastData(doc, multicastDataList);
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * Validates right loading of Stream Profiles
	 */
	@Test
	public void testLoadStream_1() {
		logger = Logger.getLogger("zisko.multicastor.testcases.model");
		
		multicastDataList = new Vector<MulticastData>();
		//String path = "C:\\Users\\development\\Desktop\\testdata\\stream\\load\\streams_1.xml";
		String path = "testdata\\stream\\load\\streams_1.xml";
		
		xmlParserProfile = new XmlParserStream(logger);
		
		try {
			xmlParserProfile.loadConfig(path, multicastDataList);
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		} 
		Document doc;
		try {
			doc = parseDocument(path);
			loadMulticastData(doc, multicastDataList);
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		} 
	}

	/**
	 * Validates error loading of Stream Profiles (wrong data)
	 */
	@Test
	public void testLoadStream_2() {
		logger = Logger.getLogger("zisko.multicastor.testcases.model");
		
		multicastDataList = new Vector<MulticastData>();
		//String path = "C:\\Users\\development\\Desktop\\testdata\\stream\\load\\streams_2.xml";
		String path = "testdata\\stream\\load\\streams_2.xml";
		
		xmlParserProfile = new XmlParserStream(logger);
		
		try {
			xmlParserProfile.loadConfig(path, multicastDataList);
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		
		} catch (Exception e) {
			assertEquals(true, true);
			e.printStackTrace();
		}
		
		Document doc;
		try {
			doc = parseDocument(path);
			if(!multicastDataList.isEmpty()) {
				loadMulticastData(doc, multicastDataList);
			}
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		} 
		
	}

	/**
	 * Validates error loading of Stream Profiles (data out of bound)
	 */
	@Test
	public void testLoadStream_3() {
		logger = Logger.getLogger("zisko.multicastor.testcases.model");
		
		multicastDataList = new Vector<MulticastData>();
		//String path = "C:\\Users\\development\\Desktop\\testdata\\stream\\load\\streams_3.xml";
		String path = "testdata\\stream\\load\\streams_3.xml";
		
		xmlParserProfile = new XmlParserStream(logger);
		
		try {
			xmlParserProfile.loadConfig(path, multicastDataList);
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		} 
		
		Document doc;
		try {
			doc = parseDocument(path);
			if(!multicastDataList.isEmpty()) {
				loadMulticastData(doc, multicastDataList);
			}
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		} 
		
	}
	
	private void setTestDataSender() throws UnknownHostException {
		Vector<InetAddress> sourceIPv4 = NetworkAdapter.ipv4Interfaces;
		multicastDataList.add( new MulticastData(InetAddress.getByName("224.0.0.1"), sourceIPv4.elementAt(0), 112, 748, 10, 180, true, Typ.SENDER_V4));
		multicastDataList.add(new MulticastData(InetAddress.getByName("ff00::1"), InetAddress.getByName("fe80::b84e:bd6e:6cbb:3ed7"), 112, 1024, 32, 100, false, Typ.SENDER_V6));
		
		MulticastData multicastData = new MulticastData();
		multicastData.setGroupMac(new byte[] {1, 0, 94, 0, 0, 1});
		multicastData.setSourceMac(new byte[] {0, 12, 41, 35, 72, 59});
		multicastData.setTyp(Typ.SENDER_MMRP);
		//pcap for mmrp
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int r = Pcap.findAllDevs(mmrpInterfaces, errbuf);
		multicastData.setDevice(mmrpInterfaces.elementAt(0));
		multicastData.setPacketLength(1024);
		multicastData.setPacketRateDesired(64);
		multicastDataList.add(multicastData);
	}
	
	private void setTestDataReceiver() throws UnknownHostException {
		Vector<InetAddress> sourceIPv4 = NetworkAdapter.ipv4Interfaces;
		multicastDataList.add( new MulticastData(InetAddress.getByName("224.0.0.1"), sourceIPv4.elementAt(0), 112, true, Typ.RECEIVER_V4));
		multicastDataList.add(new MulticastData(InetAddress.getByName("ff00::1"), InetAddress.getByName("fe80::b84e:bd6e:6cbb:3ed7"), 18, false, Typ.RECEIVER_V6));
		
		MulticastData multicastData = new MulticastData();
		multicastData.setGroupMac(new byte[] {1, 0, 94, 0, 0, 1});
		multicastData.setSourceMac(new byte[] {0, 12, 41, 35, 72, 59});
		multicastData.setTyp(Typ.RECEIVER_MMRP);
		//pcap for mmrp
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int r = Pcap.findAllDevs(mmrpInterfaces, errbuf);
		multicastData.setDevice(mmrpInterfaces.elementAt(0));
		multicastDataList.add(multicastData);
	}

	private Document parseDocument(String pfad) throws SAXException, IOException
	{
		//Create a new XML Document and parse it with DocumentBuilder
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
	
	private void loadMulticastData(Document doc, Vector<MulticastData> v1) throws IOException
	{
			    //********************************************************
			    // Lese die MultiCast Konfigurationsdaten aus dem XML
			    //********************************************************

			    //Suche den Tag Multicasts
			    NodeList multicasts = doc.getElementsByTagName("Multicasts"); //$NON-NLS-1$
			    int vectorIndex = 0;
			    MulticastData nextMulticastData = v1.elementAt(vectorIndex);
			    
			    //Der Tag "Multicasts" ist nur 1 Mal vorhanden,
			    //wenn das XML korrekt ist
			    if(multicasts.getLength()==1)
			    {
			    	//Erstelle neues MulticastData Objekt
			    	MulticastData mcd;
			    				    	
			    	//Finde alle ChildNodes von Multicasts
			    	//Diese sind SENDER_V4, RECEIVER_V4, SENDER_V6, RECEIVER_V6 oder UNDEFINED
			    	NodeList mcList = multicasts.item(0).getChildNodes();

			    	//Z�hle alle Multicast Tags
			    	int mcNummer = 0;

			    	//Iteration SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
			    	for(int i=0; i<mcList.getLength(); i++)
			    	{
			    		//Evaluiere nur SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
			    		if(mcList.item(i).getNodeName().equals("#text")){ //$NON-NLS-1$
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
			    		//Iteration �ber alle Child Nodes des momentanen SENDER/RECEIVER Knoten
			    		for(int j=0; j<configList.getLength(); j++)
			    		{
				    		configNode=configList.item(j);

				    		if(j!=0 && j%20 == 0) {
				    			vectorIndex++;
				    			nextMulticastData = v1.elementAt(vectorIndex);
				    		}
				    		if(configNode.getNodeType()== Node.ELEMENT_NODE)
					    	{
					    		configValue = (Element)configNode;
					    		String val = configValue.getTextContent();
					    		String stag = configValue.getTagName();
					    		mcdTag tag = mcdTag.valueOf(stag);

					    		switch(tag)
					    		{
					    			case active: assertEquals(new Boolean(nextMulticastData.isActive()).toString(), val); break;
					    			case groupAddress: if(!val.isEmpty()) {
							   					if(InputValidator.checkMC_IPv4(val) != null )
							   					{
							   						assertEquals(nextMulticastData.getGroupIp().toString().replace("/", ""), val);
							   						break;
							   					}
							   					else if(InputValidator.checkMC_IPv6(val) != null )
							   					{
							   						assertEquals(nextMulticastData.getGroupIp().toString().replace("/", ""), val);
							   						break;
							   					}
							   					else if(InputValidator.checkMC_MMRP(val))
							   					{
							   						String[] mac = val.split(":"); //$NON-NLS-1$
							   						byte[] macAddress = new byte[6];
							   						for(int k = 0; k < mac.length; k++) {        
							   							String x = mac[k];
							   							int y = Integer.parseInt(x, 16);;
							   						    macAddress[k] = (byte)(y & 0xFF);
							   						}
							   						
							   						assertEquals(new String(nextMulticastData.getGroupMac()), new String(macAddress));
							   						break;
							   					}
							   					else{
							   						throwWrongContentException(stag,val,mcNummer);
						    					}
					    					}
					    					else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
					    						throwEmptyContentException(stag, val, mcNummer);
					    					}
					    			case sourceAddress: if(!val.isEmpty()){
							   					if(InputValidator.checkIPv4(val) != null )
							   					{
							   						Inet4Address adr = ( Inet4Address ) InputValidator.checkIPv4(val);
							   						if(InputValidator.checkv4Adapters(adr)==true){
							   							assertEquals(nextMulticastData.getSourceIp().toString().replace("/", ""), val);
							   						}
							   						else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
							   							logger.log(Level.WARNING, "Invalid Network Adapter. Network Interface has been set to localhost."); //$NON-NLS-1$
							   							//assertEquals(true, true);
							   						}
							   						break;
							   					}
							   					else if(InputValidator.checkIPv6(val) != null )
							   					{
							   						Inet6Address adr = ( Inet6Address ) InputValidator.checkIPv6(val);
							   						if(InputValidator.checkv6Adapters(adr)==true){
							   							assertEquals(nextMulticastData.getSourceIp().toString().replace("/", ""), val);
							   						}
							   						else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
							   							logger.log(Level.WARNING,"Invalid Network Adapter. Network Interface has been set to localhost."); //$NON-NLS-1$
							   							//assertEquals(true, true);
							   						}
							   						break;
							   					}
							   					else if(InputValidator.check_MMRP(val.trim()))
							   					{
							   						//MMRP Interface setzen
							   						if (NetworkAdapter.findAddressIndex(Typ.SENDER_MMRP, val.trim()) != -1){
							   							assertEquals(new String(nextMulticastData.getDevice().getHardwareAddress()), new String(NetworkAdapter.getMmrpAdapters().get(NetworkAdapter.findAddressIndex(Typ.SENDER_MMRP, val.trim())).getHardwareAddress()));
							   						} else
							   						{
							   							logger.log(Level.WARNING, "Invalid Network Adapter. Network Interface has been set to localhost."); //$NON-NLS-1$
							   							//assertEquals(true, true);
							   						}
							   						break;
							   					}
							   					else{
							   						throwWrongContentException(stag, val, mcNummer);
						    					}
							    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
							    				throwEmptyContentException(stag, val, mcNummer);
					    					}
					    			case udpPort: if(!val.isEmpty()) {
					    						if(InputValidator.checkPort(val)>0)
					    							assertEquals(nextMulticastData.getUdpPort(), Integer.parseInt(val));
					    						else{
					    							if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"
					    							|| mcList.item(i).getNodeName()=="RECEIVER_V4"||mcList.item(i).getNodeName()=="RECEIVER_V6") //$NON-NLS-1$ //$NON-NLS-2$
					    								throwWrongContentException(stag, val, mcNummer);
						    					}
					    						break;
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"
					    				|| mcList.item(i).getNodeName()=="RECEIVER_V4"||mcList.item(i).getNodeName()=="RECEIVER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case packetLength: if(!val.isEmpty()){
					    				if(InputValidator.checkIPv4PacketLength(val) > 0 || InputValidator.checkIPv6PacketLength(val) > 0)
					    					assertEquals(nextMulticastData.getPacketLength(), Integer.parseInt(val));
					    				else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"||mcList.item(i).getNodeName()=="SENDER_MMRP"){ //$NON-NLS-1$ //$NON-NLS-2$
					    					throwWrongContentException(stag, val, mcNummer);
				    					}
					    				break;
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"||mcList.item(i).getNodeName()=="SENDER_MMRP"){ //$NON-NLS-1$ //$NON-NLS-2$
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case ttl: if(!val.isEmpty()){
					    				if(InputValidator.checkTimeToLive(val)>0)
					    						assertEquals(nextMulticastData.getTtl(), Integer.parseInt(val));
					    				else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
					    					throwWrongContentException(stag, val, mcNummer);
				    					}
					    				break;
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case packetRateDesired: if(!val.isEmpty()){
					    				if(InputValidator.checkPacketRate(val)>0)
					    						assertEquals(nextMulticastData.getPacketRateDesired(), Integer.parseInt(val));
					    				else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"||mcList.item(i).getNodeName()=="SENDER_MMRP"){ //$NON-NLS-1$ //$NON-NLS-2$
					    					throwWrongContentException(stag, val, mcNummer);
				    					}
					    				break;
					    			}else if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
					    				throwEmptyContentException(stag, val, mcNummer);
			    					}
					    			case typ: if(!val.isEmpty())
					    				{
					    					MulticastData.Typ typ = MulticastData.Typ.valueOf(val);
					    					if(typ != null){
					    						assertEquals(nextMulticastData.getTyp(), typ);
					    					}
					    					else{
					    						throwWrongContentException(stag, val, mcNummer);
					    					}
					    					break;
					    				}else {//if(mcList.item(i).getNodeName()=="SENDER_V4"||mcList.item(i).getNodeName()=="SENDER_V6"){ //$NON-NLS-1$ //$NON-NLS-2$
					    					throwEmptyContentException(stag, val, mcNummer);
				    					}
					    			case Date: break;
					    			case Host: break;
					    		}
					    	} //end of if
			    		}//end of for
			    		v1.add(mcd);
			    	}//end of for
			    }//end of if
	}
	
	private void throwWrongContentException(String tag, String value, int multicast) {
//		WrongConfigurationException ex = new WrongConfigurationException();
//		ex.setErrorMessage(Messages.getString("xmlParser.1")+value+Messages.getString("xmlParser.2")+tag+Messages.getString("xmlParser.3")+(multicast+1)+Messages.getString("xmlParser.4")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//		ex.printStackTrace();
//		//throw ex;
	}
	
	private void throwEmptyContentException(String tag, String value, int multicast)  {
//		WrongConfigurationException ex = new WrongConfigurationException();
//		ex.setErrorMessage(Messages.getString("xmlParser.1")+value+Messages.getString("xmlParser.2")+tag+Messages.getString("xmlParser.3")+(multicast+1)+Messages.getString("xmlParser.5")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//		//throw ex;
	}
}
