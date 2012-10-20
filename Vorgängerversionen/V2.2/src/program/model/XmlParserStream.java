package program.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import program.controller.Messages;
import program.data.MulticastData;
import program.data.MulticastData.Typ;

/**
 * This class loads and saves multicast stream settings.
 * 
 * @author Alexander Behm
 * 
 */
public class XmlParserStream {
	/**
	 * the logger that is used for all logging activity
	 */
	private Logger logger;

	/**
	 * the possible tags for the XML configuration
	 */
	private enum mcdTag {
		active, groupAddress, sourceAddress, udpPort, packetLength, ttl, packetRateDesired, typ, Date, Host
	}

	/**
	 * Instantiates the class and sets the logger
	 * 
	 * @param logger
	 *            The logger object that is used for logging
	 */
	public XmlParserStream(Logger logger) {
		this.logger = logger;
	}

	/**
	 * The function loads the file in the given path and parses it's content to
	 * the mc_data vector
	 * 
	 * @param pfad
	 *            The path to the file to load
	 * @param mc_data
	 *            The vector where the loaded multicasts are stored in
	 * @throws SAXException
	 *             if the document is not valid XML
	 * @throws IOException
	 *             is the file is not accessible or does not exist
	 * @throws WrongConfigurationException
	 *             if the XML configuration does not contain valid XML
	 *             configuration
	 */
	public void loadConfig(String pfad, Vector<MulticastData> mc_data)
			throws SAXException, IOException {
		Document doc = parseDocument(pfad);
		loadMulticastData(doc, mc_data);
	}

	/**
	 * Saves the gives Multicasts as XML in Path
	 * 
	 * @param pfad
	 *            The path to the XML File
	 * @param v1
	 *            the Vector of MutlicastData, that contains the Multicast
	 *            stream to save
	 * @throws IOException
	 *             if the file is not accessible or the file does not exist
	 */
	public void saveConfig(String pfad, Vector<MulticastData> v1)
			throws IOException {
		// Create XML document, that contains nodes and elements
		Document doc = createDocument();

		// Create the root element that is called "MultiCastor" and appent it to
		// the document
		Element multiCastor = doc.createElement("MultiCastor"); //$NON-NLS-1$
		doc.appendChild(multiCastor);

		// Add the stream data to the XML document
		saveMulticastData(doc, multiCastor, v1);

		// ##### XML Output #####

		// Create transformer that can transform XML to Strings
		Transformer transformer = setupTransformer();

		// Do the transformation to String
		String xmlString = XMLtoString(doc, transformer);

		// Open the outputfile and write the xml as version 1.0 to it
		File xmlfile = new File(pfad);
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(xmlfile));
		String prolog = "<?xml version=\"1.0\" ?>\n"; //$NON-NLS-1$
		writer.write(prolog + xmlString);
		// Don't forget to close the file
		// Otherwise it will fail if you try to reload
		writer.close();

	}

	/**
	 * Parses the document in pfad and returns the parsed xml document
	 * 
	 * @param pfad
	 *            The path to the XML document to parse
	 * @return The parsed document
	 * @throws SAXException
	 *             if the document is not well formatted
	 * @throws IOException
	 *             if the file does not exist or is not accessible
	 */
	private Document parseDocument(String pfad) throws SAXException,
			IOException {
		// Create a new XML Document and parse it with DocumentBuilder
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		Document doc = null;

		try {
			documentBuilder = dbFactory.newDocumentBuilder();
			doc = documentBuilder.parse(pfad);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return doc;
	}

	/**
	 * Creates a new and empty document by using DocumentBuilderFactory
	 * 
	 * @return new empty document
	 */
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
	 * Saves the vector of MulticastData as XML in root-node
	 * 
	 * @param doc
	 *            A document that is used to build the XML
	 * @param root
	 *            The Element where the XML data is appended
	 * @param v1
	 *            The Vector of MutlicastData that will be saved
	 */
	private void saveMulticastData(Document doc, Element root,
			Vector<MulticastData> v1) {
		// Create root Element for Date
		Element date = doc.createElement("Date"); //$NON-NLS-1$

		// Add our root Element to the XML file
		root.appendChild(date);

		doc.createElement("Date"); //$NON-NLS-1$
		Text text = doc.createTextNode(new Date().toString());
		date.appendChild(text);

		// Get Hostname if possible
		String hostname;
		try {
			java.net.InetAddress localMachine = java.net.InetAddress
					.getLocalHost();
			hostname = localMachine.getHostName();
		} catch (java.net.UnknownHostException uhe) {
			hostname = "unknown";
		}

		// Create root Element for host
		Element Host = doc.createElement("Host"); //$NON-NLS-1$

		// Add our root Element to the XML file
		root.appendChild(Host);

		doc.createElement("Host"); //$NON-NLS-1$
		text = doc.createTextNode(hostname);
		Host.appendChild(text);

		// Create root Element for Multicastdata
		Element multicasts = doc.createElement("Multicasts"); //$NON-NLS-1$

		// Add our root Element to the XML file
		root.appendChild(multicasts);

		// Walk through all MulticastData objects
		for (int count = 0; count < v1.size(); count++) {
			// Find out the stream type (IPV$, IPV&, MMRP)
			// Add child element corresponding to type
			MulticastData.Typ typ = v1.get(count).getTyp();
			Element mcdTyp = doc.createElement(typ.toString());
			multicasts.appendChild(mcdTyp);

			// Go through all parameters for MulticastData
			for (mcdTag tag : mcdTag.values()) {
				Element mcdElement = null;
				// Create child element for current setting
				// and create temporary integer
				if (tag != mcdTag.Host && tag != mcdTag.Date) {
					mcdElement = doc.createElement(tag.toString());
					text = doc.createTextNode(""); //$NON-NLS-1$
				}
				Integer converter = new Integer(0);

				// Do the actions for the current tag
				switch (tag) {
				case active:
					Boolean b = new Boolean(v1.get(count).isActive());
					text = doc.createTextNode(b.toString());
					break;
				case groupAddress:
					if (v1.get(count).getGroupAddress() != null) {
						text = doc.createTextNode(v1.get(count)
								.getGroupAddress());
					}
					break;
				case sourceAddress:
					if (v1.get(count).getSourceAddress() != null) {
						text = doc.createTextNode(v1.get(count)
								.getSourceAddress());
					}
					break;
				case udpPort:
					converter = v1.get(count).getUdpPort();
					text = doc.createTextNode(converter.toString());
					break;
				case packetLength:
					converter = v1.get(count).getPacketLength();
					text = doc.createTextNode(converter.toString());
					break;
				case ttl:
					converter = v1.get(count).getTtl();
					text = doc.createTextNode(converter.toString());
					break;
				case packetRateDesired:
					converter = v1.get(count).getPacketRateDesired();
					text = doc.createTextNode(converter.toString());
					break;
				case typ:
					text = doc
							.createTextNode(v1.get(count).getTyp().toString());
					break;
				}
				if (tag != mcdTag.Host && tag != mcdTag.Date) {
					mcdElement.appendChild(text);
					mcdTyp.appendChild(mcdElement);
				}

			}

		}

	}

	/**
	 * loads the parsed xml document and gives out all MulticastData objects
	 * that were defined
	 * 
	 * @param doc
	 *            the document that should be parsed
	 * @param v1
	 *            the vector, to that the MulticastData objects will be added
	 * @throws WrongConfigurationException
	 *             if there is an error in the xml tree
	 */
	private void loadMulticastData(Document doc, Vector<MulticastData> v1)
			{
		// ********************************************************
		// Lese die MultiCast Konfigurationsdaten aus dem XML
		// ********************************************************

		// Suche den Tag Multicasts
		NodeList multicasts = doc.getElementsByTagName("Multicasts"); //$NON-NLS-1$

		// Der Tag "Multicasts" ist nur 1 Mal vorhanden,
		// wenn das XML korrekt ist
		if (multicasts.getLength() == 1) {
			// Leere Objekt
			v1.clear();

			// Erstelle neues MulticastData Objekt
			MulticastData mcd;

			// Finde alle ChildNodes von Multicasts
			// Diese sind SENDER_V4, RECEIVER_V4, SENDER_V6, RECEIVER_V6 oder
			// UNDEFINED
			NodeList mcList = multicasts.item(0).getChildNodes();

			// Z�hle alle Multicast Tags
			int mcNummer = 0;
			boolean fail = false;
			// Iteration SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6 Tags
			for (int i = 0; i < mcList.getLength(); i++) {

				// Evaluiere nur SENDER_V4/RECEIVER_V4,SENDER_V6,RECEIVER_V6
				// Tags
				if (mcList.item(i).getNodeName().equals("#text")) { //$NON-NLS-1$
					continue;
				}
				mcNummer++;
				fail = false;

				mcd = new MulticastData();
				Node configNode;
				Element configValue;

				// Finde alle ChildNodes des momentanen SENDER/RECEIVER Knoten
				NodeList configList = mcList.item(i).getChildNodes();
				// Iteration �ber alle Child Nodes des momentanen
				// SENDER/RECEIVER Knoten
				for (int j = 0; j < configList.getLength(); j++) {
					configNode = configList.item(j);

					if (configNode.getNodeType() == Node.ELEMENT_NODE) {
						configValue = (Element) configNode;
						String val = configValue.getTextContent();
						String stag = configValue.getTagName();
						mcdTag tag = mcdTag.valueOf(stag);

						switch (tag) {
						case active:
							if (val.equals("true")) //$NON-NLS-1$
							{
								mcd.setActive(true);
								break;
							} else {
								mcd.setActive(false);
								break;
							}
						case groupAddress:
							if (!val.isEmpty()) {
								if (InputValidator.checkMC_IPv4(val) != null) {
									Inet4Address adr = (Inet4Address) InputValidator
											.checkMC_IPv4(val);
									mcd.setGroupIp(adr);
									break;
								} else if (InputValidator.checkMC_IPv6(val) != null) {
									Inet6Address adr = (Inet6Address) InputValidator
											.checkMC_IPv6(val);
									mcd.setGroupIp(adr);
									break;
								} else if (InputValidator.checkMC_MMRP(val)) {
									String[] mac = val.split(":"); //$NON-NLS-1$
									byte[] macAddress = new byte[6];
									for (int k = 0; k < mac.length; k++) {
										String x = mac[k];
										int y = Integer.parseInt(x, 16);
										;
										macAddress[k] = (byte) (y & 0xFF);
									}

									mcd.setGroupMac(macAddress);
									break;
								} else {
									throwWrongContentException(stag, val,
											mcNummer, fail);
									fail = true;
								}
							} else if (mcList.item(i).getNodeName() == "SENDER_V4" || mcList.item(i).getNodeName() == "SENDER_V6") { //$NON-NLS-1$ //$NON-NLS-2$
								throwEmptyContentException(stag, val, mcNummer,
										fail);
								fail = true;
							}
						case sourceAddress:
							if (!val.isEmpty()) {
								if (InputValidator.checkIPv4(val) != null) {
									Inet4Address adr = (Inet4Address) InputValidator
											.checkIPv4(val);
									if (InputValidator.checkv4Adapters(adr) == true) {
										mcd.setSourceIp(adr);
									} else if (mcList.item(i).getNodeName() == "SENDER_V4" || mcList.item(i).getNodeName() == "SENDER_V6") //$NON-NLS-1$ //$NON-NLS-2$
									{
										logger.log(Level.WARNING,
												"Invalid Network Adapter. Network Interface has been set to localhost."); //$NON-NLS-1$
										adr = (Inet4Address) InputValidator
												.checkIPv4("127.0.0.1"); //$NON-NLS-1$
										mcd.setSourceIp(adr);
									}
									break;
								} else if (InputValidator.checkIPv6(val) != null) {
									Inet6Address adr = (Inet6Address) InputValidator
											.checkIPv6(val);
									if (InputValidator.checkv6Adapters(adr) == true) {
										mcd.setSourceIp(adr);
									} else if (mcList.item(i).getNodeName() == "SENDER_V4" || mcList.item(i).getNodeName() == "SENDER_V6") { //$NON-NLS-1$ //$NON-NLS-2$
										logger.log(Level.INFO,
												"Invalid Network Adapter. Network Interface has been set to localhost."); //$NON-NLS-1$
										adr = (Inet6Address) InputValidator
												.checkIPv6("::1"); //$NON-NLS-1$
										mcd.setSourceIp(adr);
									}
									break;
								} else if (InputValidator
										.check_MMRP(val.trim())) {
									// MMRP Interface setzen
									if (NetworkAdapter.findAddressIndex(
											Typ.SENDER_MMRP, val.trim()) != -1)
										mcd.setDevice(NetworkAdapter
												.getMmrpAdapters()
												.get(NetworkAdapter
														.findAddressIndex(
																Typ.SENDER_MMRP,
																val.trim())));
									else {
										if (NetworkAdapter.findAddressIndex(
												Typ.SENDER_MMRP,
												"00:00:00:00:00:00") != -1) {
											mcd.setDevice(NetworkAdapter
													.getMmrpAdapters()
													.get(NetworkAdapter
															.findAddressIndex(
																	Typ.SENDER_MMRP,
																	"00:00:00:00:00:00")));
											logger.log(Level.INFO,
													"Invalid Network Adapter. Network Interface has been set to localhost."); //$NON-NLS-1$
										} else {
											if (NetworkAdapter
													.getMmrpAdapters().size() > 0) {
												mcd.setDevice(NetworkAdapter
														.getMmrpAdapters().get(
																0));
												logger.log(Level.INFO,
														"Invalid Network Adapter. Setting first available adapter.");
											} else {
												fail = true;
												logger.log(Level.INFO,
														"Invalid Network Adapter. Removing Stream");
											}

										}
									}
									break;
								} else {

									throwWrongContentException(stag, val,
											mcNummer, fail);
									fail = true;
								}
							} else if (mcList.item(i).getNodeName() == "SENDER_V4" || mcList.item(i).getNodeName() == "SENDER_V6") { //$NON-NLS-1$ //$NON-NLS-2$
								throwEmptyContentException(stag, val, mcNummer,
										fail);
								fail = true;
							}
						case udpPort:
							if (!val.isEmpty()) {
								if (InputValidator.checkPort(val) > 0)
									mcd.setUdpPort(Integer.parseInt(val));
								else {
									if (mcList.item(i).getNodeName() == "SENDER_V4"
											|| mcList.item(i).getNodeName() == "SENDER_V6"
											|| mcList.item(i).getNodeName() == "RECEIVER_V4" || mcList.item(i).getNodeName() == "RECEIVER_V6") //$NON-NLS-1$ //$NON-NLS-2$
									{
										throwWrongContentException(stag, val,
												mcNummer, fail);
										fail = true;
									}

								}
								break;
							} else if (mcList.item(i).getNodeName() == "SENDER_V4"
									|| mcList.item(i).getNodeName() == "SENDER_V6"
									|| mcList.item(i).getNodeName() == "RECEIVER_V4" || mcList.item(i).getNodeName() == "RECEIVER_V6") //$NON-NLS-1$ //$NON-NLS-2$
							{
								throwEmptyContentException(stag, val, mcNummer,
										fail);
								fail = true;
							}
						case packetLength:
							if (!val.isEmpty()) {
								if (InputValidator.checkIPv4PacketLength(val) > 0
										|| InputValidator
												.checkIPv6PacketLength(val) > 0)
									mcd.setPacketLength(Integer.parseInt(val));
								else if (mcList.item(i).getNodeName() == "SENDER_V4" || mcList.item(i).getNodeName() == "SENDER_V6" || mcList.item(i).getNodeName() == "SENDER_MMRP") { //$NON-NLS-1$ //$NON-NLS-2$
									throwWrongContentException(stag, val,
											mcNummer, fail);
									fail = true;
								}
								break;
							} else if (mcList.item(i).getNodeName() == "SENDER_V4" || mcList.item(i).getNodeName() == "SENDER_V6" || mcList.item(i).getNodeName() == "SENDER_MMRP") { //$NON-NLS-1$ //$NON-NLS-2$
								throwEmptyContentException(stag, val, mcNummer,
										fail);
								fail = true;
							}
						case ttl:
							if (!val.isEmpty()
									&& InputValidator.checkTimeToLive(val) > 0) {
								mcd.setTtl(Integer.parseInt(val));
							} else if (mcList.item(i).getNodeName() == "SENDER_V4" || mcList.item(i).getNodeName() == "SENDER_V6") { //$NON-NLS-1$ //$NON-NLS-2$
								throwWrongContentException(stag, val, mcNummer,
										fail);
								fail = true;
							}
							;
							break;
						case packetRateDesired:
							if (!val.isEmpty()
									&& InputValidator.checkPacketRate(val) > 0) {
								mcd.setPacketRateDesired(Integer.parseInt(val));
							} else if (mcList.item(i).getNodeName() == "SENDER_V4" || mcList.item(i).getNodeName() == "SENDER_V6" || mcList.item(i).getNodeName() == "SENDER_MMRP") { //$NON-NLS-1$ //$NON-NLS-2$
								throwWrongContentException(stag, val, mcNummer,
										fail);
								fail = true;
							}
							break;
						case typ:
							if (!val.isEmpty()) {
								MulticastData.Typ typ = MulticastData.Typ
										.valueOf(val);
								if (Typ.isV6(typ) || Typ.isV4(typ)
										|| Typ.isMMRP(typ)) {
									mcd.setTyp(typ);
								} else {
									throwWrongContentException(stag, val,
											mcNummer, fail);
									fail = true;
								}
								break;
							} else {
								throwEmptyContentException(stag, val, mcNummer,
										fail);
								fail = true;
							}
						case Date:
							break;
						case Host:
							break;
						}
					} // end of if
				}// end of for
				if (!fail)
					v1.add(mcd);
			}// end of for
		}// end of if
	}

	/**
	 * Logs the message and shows a dialog if fail is set to false
	 * 
	 * @param tag
	 *            The xml tag that has an error
	 * @param value
	 *            The value that was found in this tag
	 * @param multicast
	 *            The number of the stream that has an error
	 * @param fail
	 *            Boolean wheter this is the first error
	 * @throws WrongConfigurationException
	 */
	private void throwWrongContentException(String tag, String value,
			int multicast, boolean fail) {
		if (!fail)
			logger.log(
					Level.INFO,
					Messages.getString("xmlParser.1") + value
							+ Messages.getString("xmlParser.2") + tag
							+ Messages.getString("xmlParser.3") + (multicast)
							+ Messages.getString("xmlParser.4"));

	}

	/**
	 * Logs the message and shows a dialog if fail is set to false
	 * 
	 * @param tag
	 *            The xml tag that has an error
	 * @param value
	 *            The value that was found in this tag
	 * @param multicast
	 *            The number of the stream that has an error
	 * @param fail
	 *            Boolean wheter this is the first error
	 * @throws WrongConfigurationException
	 */
	private void throwEmptyContentException(String tag, String value,
			int multicast, boolean fail) {
		if (!fail)
			logger.log(
					Level.INFO,
					Messages.getString("xmlParser.1") + value
							+ Messages.getString("xmlParser.2") + tag
							+ Messages.getString("xmlParser.3") + (multicast)
							+ Messages.getString("xmlParser.5"));
	}

	/**
	 * sets a transformer for xml up. This is used to convert the xml to string
	 * 
	 * @return the set up transformer
	 */
	private Transformer setupTransformer() {

		TransformerFactory transFactory = TransformerFactory.newInstance();
		transFactory.setAttribute("indent-number", 4); //$NON-NLS-1$
		Transformer transformer = null;
		try {
			transformer = transFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

		return transformer;
	}

	/**
	 * converts an xml node with all of its childs into a string. This can then
	 * be used to write an xml file
	 * 
	 * @param doc
	 *            the document to transform to string
	 * @param transformer
	 *            the transformer that should be used
	 * @return the xml as string
	 */
	private String XMLtoString(Document doc, Transformer transformer) {
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

}
