   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
 package dhbw.multicastor.program.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import dhbw.multicastor.program.data.DefaultMulticastData;
import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.interfaces.IgmpMldInterface;
import dhbw.multicastor.program.interfaces.MMRPInterface;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;


/**
 * @author Daniela Gerz, Manuel Eisenhofer
 * 
 */

public abstract class xmlParser implements
		dhbw.multicastor.program.interfaces.XMLParserInterface {
	protected Logger logger;

	//hier sind alle Node-Namen (IGMP/MLD), die Daten in ihren Childnodes haben kï¿½nnen, von der Konfigurationsdatei
	protected enum igmpMld {
		active, groupIp, sourceIp, udpPort, packetLength, ttl, packetRateDesired
	}

	//hier sind alle Node-Namen (MMRP), die Daten in ihren Childnodes haben kï¿½nnen, von der Konfigurationsdatei
	protected enum mmrp {
		active, macGroupId, macSourceId, packetLength, packetRateDesired, timerJoinMt
	}

	//Attribute die fï¿½r jeden Multicaststrom abgespeichert werden
	protected enum attributes {
		protocolType
	}

	
	protected enum uidTag {
		selectedTab, selectedUserlevel, groupadress, networkInterface, port, ttl, packetrate, packetlength, activeButton, selectedRows, columnOrderString, columnVisibilityString, isAutoSaveEnabled
	}

	XPathFactory factory = XPathFactory.newInstance();

	/**
	 * Konstruktor
	 * @param logger
	 */
	public xmlParser(Logger logger) {
		this.logger = logger;
	}

	/**
	 * This Method gets a Document with the type of xml and a Vector<MulticastData>. 
	 * It gets all information from the xml-File, creates the members of the vectors 
	 * and adds it to the vector
	 * 
	 * @param path
	 * @param v1
	 */
	public void loadConfig(String path, Vector<MulticastData> v1) throws SAXException,
			FileNotFoundException, IOException, WrongConfigurationException {
		Document doc = parseDocument(path);

		
		/*
		 * checks if the vector v1 is not null and calls the method loadMulticastData 
		 * 
		 */

		if (v1 != null) {
			loadMulticastData(doc, v1);
		}
	}

	/**
	 * This Method gets a Document with the type of xml and a
	 * Vector<MulticastData>. It gets all information from the xml-File, creates
	 * IgmpMldData or MmrpData and adds it to the Vector<MulticastData>
	 * 
	 * @param doc
	 * @param v1
	 * @throws WrongConfigurationException
	 */
	private void loadMulticastData(Document doc, Vector<MulticastData> v1)
			throws WrongConfigurationException {
		// ********************************************************
		// Lese die MultiCast Konfigurationsdaten aus dem XML
		// ********************************************************
		Vector<MulticastData> data = new Vector<MulticastData>();
		// Suche den Tag Multicasts
		NodeList multicasts = doc.getElementsByTagName("Multicasts");

		XPath xpath = factory.newXPath();
		XPathExpression expr;
		NodeList nodes = null;

		if (multicasts.getLength() == 1) {
			try {
				expr = xpath.compile("//MultiCastor/Multicasts/Multicast[@"
						+ attributes.protocolType
						+ "='IGMP'] | //MultiCastor/Multicasts/Multicast[@"
						+ attributes.protocolType + "='MLD']");
				nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				System.out.println("Nodes MLD/IGMP: "+nodes.getLength());
				v1.addAll(loadIgmpMld(nodes, false));
				expr = xpath.compile("//MultiCastor/Multicasts/Multicast[@"
						+ attributes.protocolType + "='MMRP']");
				nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				System.out.println("Nodes MMRP: "+nodes.getLength());
				v1.addAll(loadMMRP(nodes, false));
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				throw new WrongConfigurationException(
						"There is an error in the configuration-file:\n"
								+ e.getMessage());
			}
		}
	}

	/**
	 * This Method gets a NodeList with MMRP data and returns a
	 * Vector<MulticastData>
	 * 
	 * @param nodes
	 * @return
	 * @throws WrongConfigurationException
	 */
	protected Vector<MulticastData> loadMMRP(NodeList nodes, boolean d)
			throws WrongConfigurationException {
		Vector<MulticastData> v1 = new Vector<MulticastData>();
		MMRPInterface data = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			data = new MMRPData(ProtocolType.MMRP);
			String macGroupId = getChildNodeValue(nodes.item(i),
					mmrp.macGroupId);
			data.setActive(Boolean.parseBoolean(getChildNodeValue(
					nodes.item(i), mmrp.active)));
			data.setProtocolType(getProtocolType(nodes.item(i)));
			data.setPacketLength(InputValidator
					.checkMmrpPacketLength(getChildNodeValue(nodes.item(i),
							mmrp.packetLength)));
			data.setPacketRateDesired(InputValidator
					.checkPacketRate(getChildNodeValue(nodes.item(i),
							mmrp.packetRateDesired)));
			data.setTimerJoinMt(InputValidator.checkJoinMtTimer(getChildNodeValue(nodes.item(i), mmrp.timerJoinMt)));
			data.setMacGroupId(InputValidator.checkMACGroupAddress(macGroupId));
			data.setMacSourceId(InputValidator.checkMAC(getChildNodeValue(
					nodes.item(i), mmrp.macSourceId)));

			if (d && checkMmrp(data)) {
				v1.add((MulticastData) data);
			} else if (!d && checkNetworkInterface(data) && checkMmrp(data)) {
				v1.add((MulticastData) data);
			} else {
				logger.log(Level.WARNING, getErrorMessageMmrp(data), Event.WARNING);
			}
		}
		return v1;
	}

	/**
	 * This Method gets a NodeList with Igmp or Mld datas and returns a
	 * Vector<MulticastData> The param defaultValues says if the given nodes are
	 * defaultValues or not. Because the defaultValues have no sourceIp
	 * 
	 * @param nodeList
	 * @param boolean defaultValue
	 * @return
	 * @throws WrongConfigurationException
	 */
	protected Vector<MulticastData> loadIgmpMld(NodeList nodeList, boolean defaultValue)
			throws WrongConfigurationException {

		Vector<MulticastData> v1 = new Vector<MulticastData>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			IgmpMldInterface data = new IgmpMldData(ProtocolType.valueOf(nodeList
					.item(i).getAttributes()
					.getNamedItem(attributes.protocolType.toString())
					.getNodeValue()));
			data.setActive(Boolean.parseBoolean(getChildNodeValue(
					nodeList.item(i), igmpMld.active)));
			MulticastData.ProtocolType type = getProtocolType(nodeList.item(i));
			data.setProtocolType(type);
			data.setPacketLength(InputValidator
					.checkIPv4PacketLength(getChildNodeValue(nodeList.item(i),
							igmpMld.packetLength)));
			data.setTtl(InputValidator.checkTimeToLive(getChildNodeValue(
					nodeList.item(i), igmpMld.ttl)));
			data.setUdpPort(InputValidator.checkPort(getChildNodeValue(
					nodeList.item(i), igmpMld.udpPort)));
			data.setPacketRateDesired(InputValidator
					.checkPacketRate(getChildNodeValue(nodeList.item(i),
							igmpMld.packetRateDesired)));
			InetAddress groupAdr;
			int packetLength;
			InetAddress sourceAdr = null;
			if (type.equals(ProtocolType.IGMP)) {
				groupAdr = InputValidator.checkMC_IPv4(getChildNodeValue(
						nodeList.item(i), igmpMld.groupIp));
				packetLength = InputValidator
						.checkIPv4PacketLength(getChildNodeValue(nodeList.item(i),
								igmpMld.packetLength));
				if (packetLength < 0) {
					data.setPacketLength(52);
					logger.log(Level.WARNING, "PacketLength in Multicast with Group-Adress:'"
							+ groupAdr
							+ "' was not correct. The default PacketLength 52 was set", Event.WARNING);
				} else {
					data.setPacketLength(packetLength);
				}
				sourceAdr = InputValidator.checkIPv4(getChildNodeValue(
						nodeList.item(i), igmpMld.sourceIp));
			} else {
				groupAdr = InputValidator.checkMC_IPv6(getChildNodeValue(
						nodeList.item(i), igmpMld.groupIp));
				packetLength = InputValidator
						.checkIPv6PacketLength(getChildNodeValue(nodeList.item(i),
								igmpMld.packetLength));
				if (packetLength < 0) {
					data.setPacketLength(52);
					logger.info("PacketLength in Multicast with Group-Adress:'"
							+ groupAdr
							+ "' was not correct. The default PacketLength 52 was set");
				} else {
					data.setPacketLength(packetLength);
				}
				sourceAdr = InputValidator.checkIPv6(getChildNodeValue(
						nodeList.item(i), igmpMld.sourceIp));
			}
			data.setGroupIp(groupAdr);
			data.setSourceIp(sourceAdr);

			if (defaultValue && checkIgmpMld(data)) {
				v1.add((MulticastData) data);
			} else if (!defaultValue && checkNetworkInterface(data) && checkIgmpMld(data)) {
				v1.add((MulticastData) data);
			} else {
				logger.warning(getErrorMessageIgmpMld(data));
			}
		}
		return v1;
	}

	/**
	 * This Method searches a ChildNode in the given Node with the String,
	 * Object.toString(), and returns the Content of ChildNode
	 * 
	 * @param node
	 * @param nodeName
	 * @return
	 */
	protected String getChildNodeValue(Node node, Object nodeName) {
		NodeList nodes = node.getChildNodes();
		if (nodes.getLength() > 0) {
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeName().equals(nodeName.toString())) {
					return nodes.item(i).getTextContent();
				}
			}
		}
		return null;
	}
	

	/**
	 * saves all config-items. That means config from MulticastData
	 */
	public void saveConfig(String pfad, Vector<MulticastData> v1) throws IOException,
			WrongConfigurationException {

		// Erzeuge ein neues XML Dokument
		Document doc = createDocument();

		// Erzeuge das Root Element des XML Dokumentes, "MultiCastor".
		Element multiCastor = doc.createElement("MultiCastor");
		multiCastor.setAttribute("date", new Date().toString());
		doc.appendChild(multiCastor);

		/*
		 * Ab hier wird der XML Baum erstellt und Daten hinzugefÃ¼gt.
		 * MultiCastData ( Daten aus Vektor v1 ) 
		 * )
		 */
		if (v1 != null) {
			saveMulticastData(doc, multiCastor, v1);
		}

		// Erzeuge einen Transformer
		Transformer transformer = setupTransformer();

		// Erstelle einen String aus dem XML Baum
		String xmlString = XMLtoString(doc, transformer);

		//Erstelle xml-File wenn noch nicht vorhanden
		File xmlfile = new File(pfad);
		if (!xmlfile.exists()) {
			xmlfile.createNewFile();
		}
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(xmlfile));
		String prolog = "<?xml version=\"1.0\"?>\n";
		writer.write(prolog + xmlString);
		writer.close();

	}

	/**
	 * This method creates the attributes for the tag "Multicast" and calls
	 * either the method saveMMRP or saveIgmpMld depending on the ProtocolType
	 * from the given multicast
	 * 
	 * @param doc
	 * @param root
	 * @param v1
	 * @throws WrongConfigurationException
	 */
	private void saveMulticastData(Document doc, Element root,
			Vector<MulticastData> v1) throws WrongConfigurationException {
		// ********************************************************
		// Schreibe die MultiCast
		// Konfigurationsdaten in das XML
		// ********************************************************

		// Erzeugt Root Element fÃ¼r die MultiCast Konfigurationen
		Element multicasts = doc.createElement("Multicasts");
		root.appendChild(multicasts);
		Class<?> c = MulticastData.class;
		MulticastData multicast = null;
		//For all Multicastdata
		for (int count = 0; count < v1.size(); count++) {
			multicast = v1.get(count);
			//create a Childnode
			Element mcdTyp = doc.createElement("Multicast");
			//set protocoltype
			mcdTyp.setAttribute(attributes.protocolType.toString(), multicast
					.getProtocolType().toString());
			//if protocoltype is mmrp call method saveMMRP
			if (multicast.getProtocolType().equals(ProtocolType.MMRP)) {
				saveMMRP(doc, mcdTyp, (MMRPData) multicast);
			//else protocoltype must be igmp/mld then call method saveIgmpMld
			} else {
				saveIgmpMld(doc, mcdTyp, (IgmpMldInterface) multicast);
			}
			//append childnode
			multicasts.appendChild(mcdTyp);
		}
	}

	/**
	 * This method saves the multicast-datas (type of Igmp or Mld) in the param
	 * multicast
	 * 
	 * @param doc
	 * @param mcdTyp
	 * @param multicast
	 * @throws WrongConfigurationException
	 */
	private void saveIgmpMld(Document doc, Element mcdTyp,
			IgmpMldInterface multicast) throws WrongConfigurationException {
		//the variable c is needed for creating reflections
		Class<?> c = IgmpMldData.class;
		for (igmpMld tag : igmpMld.values()) {
			Element mcdElement = doc.createElement(tag.toString());
			Text text = doc.createTextNode("");
			//variable is needed for executing methods with reflections
			Object converter = null;

			switch (tag) {
			case groupIp:
				if (multicast.getGroupIp() != null)
					text = doc.createTextNode(multicast.getGroupIp()
							.getHostAddress());
				break;
			case sourceIp:
				if (multicast.getSourceIp() != null)
					text = doc.createTextNode(multicast.getSourceIp()
							.getHostAddress());
				break;
			default:
				Method method;
				try {
					//the method for a tag will be created
					method = c.getMethod("get"
							+ tag.toString().substring(0, 1).toUpperCase()
							+ tag.toString().substring(1,
									tag.toString().length()));
					//execute the method
					converter = method.invoke(multicast);
					text = doc.createTextNode(converter.toString());
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					throw new WrongConfigurationException(tag.toString()
							+ " nicht vorhanden");
				}
				break;
			}
			if (text != null) {
				mcdElement.appendChild(text);
				mcdTyp.appendChild(mcdElement);
			}
		}
	}

	/**
	 * This method saves the multicast-data (type of Mmrp) in the param
	 * multicast
	 * 
	 * @param doc
	 * @param mcdTyp
	 * @param multicast
	 */
	private void saveMMRP(Document doc, Element mcdTyp, MMRPInterface multicast) {
		Class<?> c = MMRPData.class;
		for (mmrp tag : mmrp.values()) {
			Element mcdElement = doc.createElement(tag.toString());
			Text text = doc.createTextNode("");
			Object converter = null;
			Method method;
			try {
				method = c.getMethod("get"
						+ tag.toString().substring(0, 1).toUpperCase()
						+ tag.toString().substring(1, tag.toString().length()));
				converter = method.invoke(multicast);
				text = doc.createTextNode(converter.toString());
			} catch (Exception e) {
				System.out.println(tag.toString() + " nicht vorhanden");
			}
			if (text != null) {
				mcdElement.appendChild(text);
				mcdTyp.appendChild(mcdElement);
			}
		}
	}

	

	/**
	 * loads the default-values from the configuration-file
	 * 
	 * @throws WrongConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean loadDefaultMulticastData(String path) throws WrongConfigurationException,
			SAXException, IOException {

		Document doc = parseDocumentFromJAR(path);
		if (doc != null) {
			XPath xpath = factory.newXPath();
			XPathExpression expr;
			NodeList nodes = null;
			MulticastData v = null;
			try {
				expr = xpath.compile("//MultiCastor/default/Multicast[@"
						+ attributes.protocolType + "='IGMP']");
				nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				v = loadIgmpMld(nodes, true).get(0);
				if (v != null) {
					DefaultMulticastData.setIgmp((IgmpMldData) v);
				}else{
					return false;
				}

				expr = xpath.compile("//MultiCastor/default/Multicast[@"
						+ attributes.protocolType + "='MLD']");
				nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				v = loadIgmpMld(nodes, true).get(0);
				if (v != null) {
					DefaultMulticastData.setMld((IgmpMldData) v);
				}else{
					return false;
				}

				expr = xpath.compile("//MultiCastor/default/Multicast[@"
						+ attributes.protocolType + "='MMRP']");
				nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				v = loadMMRP(nodes, true).get(0);
				if (v != null) {
					DefaultMulticastData.setMmrp((MMRPData) v);
				}else{
					return false;
				}
				return true;
			} catch (XPathExpressionException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				
				return false;
			}
		}
		return false;
	}

	private Document createDocument() {
		// Erzeuge ein neues XML Dokument
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = documentBuilder.newDocument();
		return doc;
	}

	/**
	 * returns a document from the given path
	 * 
	 * @param path
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	protected Document parseDocument(String path) throws SAXException,
			IOException {
		// Erzeuge ein neues XML Dokument
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		Document doc = null;

		try {
			documentBuilder = dbFactory.newDocumentBuilder();
			doc = documentBuilder.parse(path);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXParseException e) {
			
		}

		return doc;
	}

	
	/**
	 *
	 * @param pfad
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document parseDocumentFromJAR(String pfad) throws SAXException,
			IOException {
		FileInputStream is = new FileInputStream(pfad);
//		InputStream is = getClass().getClassLoader().getResourceAsStream(pfad);

		// Erzeuge ein neues XML Dokument
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		Document doc = null;

		try {
			documentBuilder = dbFactory.newDocumentBuilder();
			doc = documentBuilder.parse(is);
		} catch (Exception e) {
			if(logger!= null)
			logger.log(Level.WARNING, "Wrong configuration in DefaultMulticastData.xml\n+" +
					"DefaultMulticastData.xml has been replaced");
			
			return null;
		}

		return doc;

	}

	protected Transformer setupTransformer() {

		TransformerFactory transFactory = TransformerFactory.newInstance();
		transFactory.setAttribute("indent-number", 4);
		Transformer transformer = null;
		try {
			transformer = transFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		return transformer;
	}

	/**
	 * reads a XML-File and returns the content as a String
	 * @param doc
	 * @param transformer
	 * @return
	 */
	protected String XMLtoString(Document doc, Transformer transformer) {
		StringWriter sWriter = new StringWriter();
		StreamResult result = new StreamResult(sWriter);
		DOMSource source = new DOMSource(doc);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		String xmlString = sWriter.toString();

		return xmlString;
	}

	private void throwWrongContentException(String tag, String value,
			int multicast) throws WrongConfigurationException {
		WrongConfigurationException ex = new WrongConfigurationException(
				"Error in configuration file: Value \"" + value + "\" of "
						+ tag + " in Multicast " + (multicast + 1)
						+ " is wrong.");
		throw ex;
	}

	private void throwEmptyContentException(String tag, String value,
			int multicast) throws WrongConfigurationException {
		WrongConfigurationException ex = new WrongConfigurationException(
				"Error in configuration file: Value \"" + value + "\" of "
						+ tag + " in Multicast " + (multicast + 1)
						+ " is empty.");
		throw ex;
	}

	protected MulticastData.ProtocolType getProtocolType(Node node) {
		return MulticastData.ProtocolType.valueOf(node.getAttributes()
				.getNamedItem("protocolType").getTextContent());
	}

	protected abstract boolean checkIgmpMld(IgmpMldInterface data);

	protected abstract boolean checkNetworkInterface(IgmpMldInterface data);

	protected abstract boolean checkNetworkInterface(MMRPInterface data);

	protected abstract boolean checkMmrp(MMRPInterface data);

	protected abstract String getErrorMessageIgmpMld(IgmpMldInterface data);

	protected abstract String getErrorMessageMmrp(MMRPInterface data);

}
