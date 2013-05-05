package multicastor.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import multicastor.data.GUIData;
import multicastor.data.MulticastData;
import multicastor.data.MulticastData.Protocol;
import multicastor.data.MulticastData.Typ;
import multicastor.interfaces.XMLParserInterface;
import multicastor.lang.LanguageManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * XML Parser Klasse die das Interface {@link XMLParserInterface} implementiert
 * und die Daten aus den XML Konfigurationsdateien in den Datenstrukturen
 * speichert.
 */
public class xmlParser implements multicastor.interfaces.XMLParserInterface {
	/** XML Tag Namen fuer die Multicast Daten */
	private enum mcdTag {
		active, groupIp, groupMac, packetLength, packetRateDesired, sourceIp, sourceMac, ttl, typ, udpPort, protocol
	}

	/**
	 * Language Manager ist wichtig fuer die multi Language Unterstuetzung
	 */
	private final LanguageManager lang = LanguageManager.getInstance();

	private final Logger logger;

	/**
	 * Konstruktor
	 * 
	 * @param logger
	 *            Logger fuer die Ausgaben
	 */
	public xmlParser(final Logger logger) {
		this.logger = logger;
	}

	/**
	 * Laedt die Multicast GUI Config
	 * 
	 * @param path
	 *            Pfad zur XML Datei
	 * @param data
	 *            GUIData Objekt mit den GUI Daten
	 */
	@Override
	public void loadGUIConfig(final String path, final GUIData data)
			throws SAXException, FileNotFoundException, IOException,
			WrongConfigurationException {
		// load GUI Config [FF]
		final Document doc = parseDocument(path);

		if(data != null) {
			loadGUIData(doc, data);
		}
	}

	/**
	 * Laedt die Multicast Data Config
	 * 
	 * @param path
	 *            Pfad zur XML Datei
	 * @param v
	 *            MulticastData Vector
	 */
	@Override
	public void loadMultiCastConfig(final String path,
			final Vector<MulticastData> v) throws SAXException,
			FileNotFoundException, IOException, WrongConfigurationException {
		final Document doc = parseDocument(path);

		if(v != null) {
			loadMulticastData(doc, v);
		}
	}

	/**
	 * Speichert die Multicastor GUI Informationen in dem XML File
	 * 
	 * @param pfad
	 *            Pfad zur XML Datei
	 * @param data
	 *            GUI Daten Objekt
	 */
	@Override
	public void saveGUIConfig(final String pfad, final GUIData data)
			throws IOException {

		Element el;

		// Erzeuge ein neues XML Dokument
		final Document doc = createDocument();
		final Element guiConfig = doc.createElement("MultiCastor");
		doc.appendChild(guiConfig);

		// Erzeugt Root Element fuer die User System Informationen
		final Element system = doc.createElement("System");
		guiConfig.appendChild(system);
		system.appendChild(el = doc.createElement("Time"));
		el.setTextContent(new Date().toString());
		try {
			system.appendChild(el = doc.createElement("Hostname"));
			el.setTextContent(InetAddress.getLocalHost().getHostName());
			system.appendChild(el = doc.createElement("Hostaddress"));
			el.setTextContent(InetAddress.getLocalHost().getHostAddress());
		} catch(final UnknownHostException e) {
			// already loged at the program start. Just create empty tags
			system.appendChild(el = doc.createElement("Hostname"));
			system.appendChild(el = doc.createElement("Hostaddress"));
		}
		system.appendChild(el = doc.createElement("Username"));
		el.setTextContent(System.getProperty("user.name"));
		system.appendChild(el = doc.createElement("Javaversion"));
		el.setTextContent(System.getProperty("java.version"));
		system.appendChild(el = doc.createElement("Javavendor"));
		el.setTextContent(System.getProperty("java.vendor"));

		// Erzeugt Root Element fuer die Default Werte
		final Element default_l3 = doc.createElement("DEFAULT_L3");
		guiConfig.appendChild(default_l3);

		default_l3.appendChild(el = doc.createElement("GroupIp"));
		el.setTextContent(data.Default_L3_GroupIp);
		default_l3.appendChild(el = doc.createElement("UdpPort"));
		el.setTextContent(data.Default_L3_UdpPort);
		default_l3.appendChild(el = doc.createElement("Ttl"));
		el.setTextContent(data.Default_L3_Ttl);
		default_l3.appendChild(el = doc.createElement("PacketRateDesired"));
		el.setTextContent(data.Default_L3_PacketRateDesired);
		default_l3.appendChild(el = doc.createElement("PacketLength"));
		el.setTextContent(data.Default_L3_PacketLength);

		// Erzeugt Root Element fuer die Default Werte
		final Element default_l2 = doc.createElement("DEFAULT_L2");
		guiConfig.appendChild(default_l2);

		default_l2.appendChild(el = doc.createElement("GroupMac"));
		el.setTextContent(data.Default_L2_GroupMac);
		default_l2.appendChild(el = doc.createElement("PacketRateDesired"));
		el.setTextContent(data.Default_L2_PacketRateDesired);
		default_l2.appendChild(el = doc.createElement("PacketLength"));
		el.setTextContent(data.Default_L2_PacketLength);

		// Erzeugt Root Element fuer die User System Informationen
		final Element tabs = doc.createElement("Tabs");
		guiConfig.appendChild(tabs);

		tabs.appendChild(el = doc.createElement("L2_SENDER"));
		el.setTextContent(data.getL2_RECEIVER().toString());
		tabs.appendChild(el = doc.createElement("L2_RECEIVER"));
		el.setTextContent(data.getL2_SENDER().toString());
		tabs.appendChild(el = doc.createElement("L3_SENDER"));
		el.setTextContent(data.getL3_SENDER().toString());
		tabs.appendChild(el = doc.createElement("L3_RECEIVER"));
		el.setTextContent(data.getL3_RECEIVER().toString());
		tabs.appendChild(el = doc.createElement("ABOUT"));
		el.setTextContent(data.getABOUT().toString());
		tabs.appendChild(el = doc.createElement("PLUS"));
		el.setTextContent(data.getPLUS().toString());

		final Element language = doc.createElement("Language");
		language.setTextContent(data.getLanguage());
		guiConfig.appendChild(language);
		final Element windowName = doc.createElement("WindowName");
		windowName.setTextContent(data.getWindowName());
		guiConfig.appendChild(windowName);

		// Erzeuge einen Transformer
		final Transformer transformer = setupTransformer();

		// Erstelle einen String aus dem XML Baum
		final String xmlString = XMLtoString(doc, transformer);

		// print xml to console
		// System.out.println("Here's the xml:\n\n" + xmlString);

		final File xmlfile = new File(pfad);
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(xmlfile));
		final String prolog = "<?xml version=\"1.0\"?>\n";
		writer.write(prolog + xmlString);
		writer.close();

	}

	/**
	 * speichert die Multicast Data Config aus dem MulticastData Vector in einer
	 * XML Datei
	 * 
	 * @param pfad
	 *            Pfad zur XML Datei
	 * @param v
	 *            MulticastData Vector
	 */
	@Override
	public void saveMulticastConfig(final String pfad,
			final Vector<MulticastData> v) throws IOException {
		// Erzeuge ein neues XML Dokument
		final Document doc = createDocument();

		// Erzeuge das Root Element des XML Dokumentes, "MultiCastor".
		final Element multiCastor = doc.createElement("MultiCastor");
		doc.appendChild(multiCastor);

		// Erzeugt Root Element fuer die User System Informationen
		final Element system = doc.createElement("System");
		multiCastor.appendChild(system);
		Element el;
		system.appendChild(el = doc.createElement("Time"));
		el.setTextContent(new Date().toString());
		try {
			system.appendChild(el = doc.createElement("Hostname"));
			el.setTextContent(InetAddress.getLocalHost().getHostName());
			system.appendChild(el = doc.createElement("Hostaddress"));
			el.setTextContent(InetAddress.getLocalHost().getHostAddress());
		} catch(final UnknownHostException e) {
			// already logged at the program start. Just create empty tags
			system.appendChild(el = doc.createElement("Hostname"));
			system.appendChild(el = doc.createElement("Hostaddress"));
		}
		system.appendChild(el = doc.createElement("Username"));
		el.setTextContent(System.getProperty("user.name"));
		system.appendChild(el = doc.createElement("Javaversion"));
		el.setTextContent(System.getProperty("java.version"));
		system.appendChild(el = doc.createElement("Javavendor"));
		el.setTextContent(System.getProperty("java.vendor"));

		// Speichert die Multicast Daten
		saveMulticastData(doc, multiCastor, v);

		// ##### XML Ausgabe #####

		// Erzeuge einen Transformer
		final Transformer transformer = setupTransformer();

		// Erstelle einen String aus dem XML Baum
		final String xmlString = XMLtoString(doc, transformer);

		// print xml to console
		// System.out.println("Here's the xml:\n\n" + xmlString);

		final File xmlfile = new File(pfad);
		BufferedWriter writer = new BufferedWriter(new FileWriter(xmlfile));
		final String prolog = "<?xml version=\"1.0\"?>\n";
		writer.write(prolog + xmlString);
		writer.close();

	}

	/**
	 * Erstellt ein neues XML Dokument
	 * 
	 * @return
	 */
	private Document createDocument() {
		// Erzeuge ein neues XML Dokument
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = dbFactory.newDocumentBuilder();
		} catch(final ParserConfigurationException e) {
			e.printStackTrace();
		}
		final Document doc = documentBuilder.newDocument();
		return doc;
	}

	/**
	 * Laedt wirklich die Multicast GUI Config und speichert die XML Datei Werte
	 * in GUIData
	 * 
	 * @param doc
	 *            Document (Pfad) zur XML Datei
	 * @param data
	 *            GUIData Objekt mit den GUI Daten
	 */
	private void loadGUIData(final Document doc, final GUIData data) {
		// ********************************************************
		// Lese die GUI Konfigurationsdaten aus dem XML
		// ********************************************************
		final NodeList tabs = doc.getElementsByTagName("Tabs");
		if(tabs.getLength() == 1) {
			final NodeList tabList = tabs.item(0).getChildNodes();
			for(int i = 0; i < tabList.getLength(); i++) {
				// Evaluiere nur L3_SENDER, L3_RECEIVER, L2_SENDER, L2_RECEIVER
				// Tags
				if(tabList.item(i).getNodeName().equals("#text")) {
					continue;
				}

				if(tabList.item(i).getNodeName().equals("L2_SENDER")) {
					data.setL2_RECEIVER(GUIData.TabState.valueOf(tabList
							.item(i).getTextContent()));
				}
				if(tabList.item(i).getNodeName().equals("L2_RECEIVER")) {
					data.setL2_SENDER(GUIData.TabState.valueOf(tabList.item(i)
							.getTextContent()));
				}
				if(tabList.item(i).getNodeName().equals("L3_SENDER")) {
					data.setL3_SENDER(GUIData.TabState.valueOf(tabList.item(i)
							.getTextContent()));
				}
				if(tabList.item(i).getNodeName().equals("L3_RECEIVER")) {
					data.setL3_RECEIVER(GUIData.TabState.valueOf(tabList
							.item(i).getTextContent()));
				}
				if(tabList.item(i).getNodeName().equals("ABOUT")) {
					data.setABOUT(GUIData.TabState.valueOf(tabList.item(i)
							.getTextContent()));
				}
				if(tabList.item(i).getNodeName().equals("PLUS")) {
					data.setPLUS(GUIData.TabState.valueOf(tabList.item(i)
							.getTextContent()));
				}
			}
		}

		final NodeList default_l2 = doc.getElementsByTagName("DEFAULT_L2");
		if(default_l2.getLength() == 1) {
			final NodeList tabList = default_l2.item(0).getChildNodes();
			for(int i = 0; i < tabList.getLength(); i++) {
				if(tabList.item(i).getNodeName().equals("#text")) {
					continue;
				}

				if(tabList.item(i).getNodeName().equals("GroupMac")) {
					data.Default_L2_GroupMac = tabList.item(i).getTextContent();
				}
				if(tabList.item(i).getNodeName().equals("PacketRateDesired")) {
					data.Default_L2_PacketRateDesired = tabList.item(i)
							.getTextContent();
				}
				if(tabList.item(i).getNodeName().equals("PacketLength")) {
					data.Default_L2_PacketLength = tabList.item(i)
							.getTextContent();
				}
			}
		}

		final NodeList default_l3 = doc.getElementsByTagName("DEFAULT_L3");
		if(default_l3.getLength() == 1) {
			final NodeList tabList = default_l3.item(0).getChildNodes();
			for(int i = 0; i < tabList.getLength(); i++) {
				if(tabList.item(i).getNodeName().equals("#text")) {
					continue;
				}

				if(tabList.item(i).getNodeName().equals("GroupIp")) {
					data.Default_L3_GroupIp = tabList.item(i).getTextContent();
				}
				if(tabList.item(i).getNodeName().equals("UdpPort")) {
					data.Default_L3_UdpPort = tabList.item(i).getTextContent();
				}
				if(tabList.item(i).getNodeName().equals("Ttl")) {
					data.Default_L3_Ttl = tabList.item(i).getTextContent();
				}
				if(tabList.item(i).getNodeName().equals("PacketRateDesired")) {
					data.Default_L3_PacketRateDesired = tabList.item(i)
							.getTextContent();
				}
				if(tabList.item(i).getNodeName().equals("PacketLength")) {
					data.Default_L3_PacketLength = tabList.item(i)
							.getTextContent();
				}
			}
		}

		final NodeList windowTitle = doc.getElementsByTagName("WindowName");
		if(windowTitle.getLength() == 1) {
			data.setWindowName(windowTitle.item(0).getTextContent());
		}
		final NodeList language = doc.getElementsByTagName("Language");
		if(language.getLength() == 1) {
			data.setLanguage(language.item(0).getTextContent());
		}
	}

	/**
	 * Laedt wirklich die Multicast Daten und speichert die XML Daten in dem
	 * MultocastData Vector
	 * 
	 * @param doc
	 *            Document (Pfad) zur XML Datei
	 * @param v
	 *            MulticastData Vector
	 */
	private void loadMulticastData(final Document doc,
			final Vector<MulticastData> v) throws WrongConfigurationException {
		// ********************************************************
		// Lese die MultiCast Konfigurationsdaten aus dem XML
		// ********************************************************

		// Suche den Tag Multicasts
		final NodeList multicasts = doc.getElementsByTagName("Multicasts");

		// Der Tag "Multicasts" ist nur 1 Mal vorhanden,
		// wenn das XML korrekt ist
		if(multicasts.getLength() == 1) {
			// Luesche bisherige Einstellungen
			v.clear();
			// Erstelle neues MulticastData Objekt
			MulticastData mcd;
			// Finde alle ChildNodes von Multicasts
			// Diese sind L3_SENDER, L3_RECEIVER, L2_SENDER, L2_RECEIVER oder
			// UNDEFINED
			final NodeList mcList = multicasts.item(0).getChildNodes();

			// Zuehle alle Multicast Tags
			int mcNummer = 0;

			// Iteration L3_SENDER, L3_RECEIVER, L2_SENDER, L2_RECEIVER Tags
			for(int i = 0; i < mcList.getLength(); i++) {
				// Evaluiere nur L3_SENDER, L3_RECEIVER, L2_SENDER, L2_RECEIVER
				// Tags
				if(mcList.item(i).getNodeName().equals("#text")) {
					continue;
				}
				mcNummer++;

				mcd = new MulticastData();
				Node configNode;
				Element configValue;

				// Finde alle ChildNodes des momentanen SENDER/RECEIVER Knoten
				final NodeList configList = mcList.item(i).getChildNodes();
				// Iteration ueber alle Child Nodes des momentanen
				// SENDER/RECEIVER Knoten
				for(int j = 0; j < configList.getLength(); j++) {
					configNode = configList.item(j);

					if(configNode.getNodeType() == Node.ELEMENT_NODE) {
						configValue = (Element)configNode;
						final String val = configValue.getTextContent();
						final String stag = configValue.getTagName();
						final mcdTag tag = mcdTag.valueOf(stag);

						switch(tag) {
							case active:
								if(val.equals("true")) {
									mcd.setActive(true);
								} else {
									mcd.setActive(false);
								}
								break;
							case groupIp:
								if(!val.isEmpty()) {
									if(InputValidator.checkMC_IPv4(val) != null) {
										final Inet4Address adr = (Inet4Address)InputValidator
												.checkMC_IPv4(val);
										mcd.setGroupIp(adr);
									} else if(InputValidator.checkMC_IPv6(val) != null) {
										final Inet6Address adr = (Inet6Address)InputValidator
												.checkMC_IPv6(val);
										mcd.setGroupIp(adr);
									} else {
										throwWrongContentException(stag, val,
												mcNummer);
									}
								} else if((mcList.item(i).getNodeName() == "L3_SENDER")
										|| (mcList.item(i).getNodeName() == "L3_RECEIVER")) { // [FF]
																								// SENDER_V4
																								// ||
																								// SENDER_V6
																								// ->
																								// L3_SENDER
									throwEmptyContentException(stag, val, mcNummer);
								}
								break;
							case sourceIp:
								if(!val.isEmpty()) {
									if(InputValidator.checkIPv4(val) != null) {
										Inet4Address adr = (Inet4Address)InputValidator
												.checkIPv4(val);
										if(InputValidator.checkAdapters(adr) == true) {
											mcd.setSourceIp(adr);
										} else if((mcList.item(i).getNodeName() == "L3_SENDER")
												|| (mcList.item(i)
														.getNodeName() == "L3_RECEIVER")) {
											logger.log(
													Level.WARNING,
													lang.getProperty("warning.invalidNetAdapter"));
											adr = (Inet4Address)InputValidator
													.checkIPv4("127.0.0.1");
											mcd.setSourceIp(adr);
										}
									} else if(InputValidator.checkIPv6(val) != null) {
										Inet6Address adr = (Inet6Address)InputValidator
												.checkIPv6(val);
										if(InputValidator.checkAdapters(adr) == true) {
											mcd.setSourceIp(adr);
										} else if((mcList.item(i).getNodeName() == "L3_SENDER")
												|| (mcList.item(i)
														.getNodeName() == "L3_RECEIVER")) {
											logger.log(
													Level.WARNING,
													lang.getProperty("warning.invalidNetAdapter"));
											adr = (Inet6Address)InputValidator
													.checkIPv6("::1");
											mcd.setSourceIp(adr);
										}
									} else {
										throwWrongContentException(stag, val,
												mcNummer);
									}
								} else if((mcList.item(i).getNodeName() == "L3_SENDER")
										|| (mcList.item(i).getNodeName() == "L3_RECEIVER")) {
									throwEmptyContentException(stag, val, mcNummer);
								}
								break;
							case udpPort:
								if(!val.isEmpty()) {
									if(InputValidator.checkPort(val) > 0) {
										mcd.setUdpPort(Integer.parseInt(val));
									} else {
										if((mcList.item(i).getNodeName() == "L3_SENDER")
												|| (mcList.item(i)
														.getNodeName() == "L3_RECEIVER")) {
											throwWrongContentException(stag,
													val, mcNummer);
										} else {
											mcd.setUdpPort(0);
										}
									}
								} else if(mcList.item(i).getNodeName() == "L3_SENDER") { // [FF]
																							// SENDER_V4
																							// ||
																							// SENDER_V6
																							// ->
																							// L3_SENDER
									throwEmptyContentException(stag, val, mcNummer);
								}
								break;
							case packetLength:
								if(!val.isEmpty()) {
									if((InputValidator
											.checkIPv4PacketLength(val) > 0)
											|| (InputValidator
													.checkIPv6PacketLength(val) > 0)) {
										mcd.setPacketLength(Integer
												.parseInt(val));
									} else if(mcList.item(i).getNodeName() == "L3_SENDER") { // [FF]
																								// SENDER_V4
																								// ||
																								// SENDER_V6
																								// ->
																								// L3_SENDER
										throwWrongContentException(stag, val,
												mcNummer);
									}
								} else if(mcList.item(i).getNodeName() == "L3_SENDER") { // [FF]
																							// SENDER_V4
																							// ||
																							// SENDER_V6
																							// ->
																							// L3_SENDER
									throwEmptyContentException(stag, val, mcNummer);
								}
								break;
							case ttl:
								if(!val.isEmpty()) {
									if(InputValidator.checkTimeToLive(val) > 0) {
										mcd.setTtl(Integer.parseInt(val));
									} else if(mcList.item(i).getNodeName() == "L3_SENDER") { // [FF]
																								// SENDER_V4
																								// ||
																								// SENDER_V6
																								// ->
																								// L3_SENDER
										throwWrongContentException(stag, val,
												mcNummer);
									}
								} else if(mcList.item(i).getNodeName() == "L3_SENDER") { // [FF]
																							// SENDER_V4
																							// ||
																							// SENDER_V6
																							// ->
																							// L3_SENDER
									throwEmptyContentException(stag, val, mcNummer);
								}
								break;
							case packetRateDesired:
								if(!val.isEmpty()) {
									if(InputValidator.checkPacketRate(val) > 0) {
										mcd.setPacketRateDesired(Integer
												.parseInt(val));
									} else if(mcList.item(i).getNodeName() == "L3_SENDER") { // [FF]
																								// SENDER_V4
																								// ||
																								// SENDER_V6
																								// ->
																								// L3_SENDER
										throwWrongContentException(stag, val,
												mcNummer);
									}
								} else if(mcList.item(i).getNodeName() == "L3_SENDER") { // [FF]
																							// SENDER_V4
																							// ||
																							// SENDER_V6
																							// ->
																							// L3_SENDER
									throwEmptyContentException(stag, val, mcNummer);
								}
								break;
							case typ:
								if(!val.isEmpty()) {
									final MulticastData.Typ typ = MulticastData.Typ
											.valueOf(val);
									if(typ != null) {
										mcd.setTyp(typ);
									} else {
										throwWrongContentException(stag, val,
												mcNummer);
									}
								} else if(mcList.item(i).getNodeName() == "L3_SENDER") { // [FF]
																							// SENDER_V4
																							// ||
																							// SENDER_V6
																							// ->
																							// L3_SENDER
									throwEmptyContentException(stag, val, mcNummer);
								}
								break;
							case sourceMac:
								if(!val.isEmpty()) {
									try {
										mcd.setMmrpSourceMac(mcd
												.getMMRPFromString(val));
									} catch(final Exception e) {
										System.out.println(e);
										// If we can't parse it.... we just dont
										// load it
										// Feel free to write an logoutput if
										// you want to
									}
								}
								break;
							case groupMac:
								if(!val.isEmpty()) {
									try {
										mcd.setMmrpGroupMac(mcd
												.getMMRPFromString(val));
									} catch(final Exception e) {
										System.out.println(e);
										// If we can't parse it.... we just dont
										// load it
										// Feel free to write an logoutput if
										// you want to
									}
								}
								break;
							case protocol:
								if(!val.isEmpty()) {
									final MulticastData.Protocol prot = MulticastData.Protocol
											.valueOf(val);
									if(prot != null) {
										mcd.setProtocol(prot);
									} else {
										mcd.setProtocol(Protocol.UNDEFINED);
									}
								}
						} // end of switch
					} // end of if
				}// end of for
				v.add(mcd);
			}// end of for
		}// end of if
	}

	/**
	 * parst die XML Datei
	 * 
	 * @param pfad
	 *            zur XML Datei
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document parseDocument(final String pfad) throws SAXException,
			IOException {
		// Erzeuge ein neues XML Dokument
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = null;
		Document doc = null;

		try {
			documentBuilder = dbFactory.newDocumentBuilder();
			doc = documentBuilder.parse(pfad);

		} catch(final ParserConfigurationException e) {
			e.printStackTrace();
		}

		return doc;
	}

	/**
	 * speichert wirklich die Multicast Data Config aus dem MulticastData Vector
	 * in einer XML Datei
	 * 
	 * @param doc
	 *            Document (Pfad) zur XML Datei
	 * @param root
	 *            XML root Element
	 * @param data
	 *            MulticastData Vector
	 */
	private void saveMulticastData(final Document doc, final Element root,
			final Vector<MulticastData> v1) { // ********************************************************
		// Schreibe die MultiCast
		// Konfigurationsdaten in das XML
		// ********************************************************

		// Erzeugt Root Element fuer die MultiCast Konfigurationen
		final Element multicasts = doc.createElement("Multicasts");
		root.appendChild(multicasts);

		// Fuer alle verschiedenen Konfigurationen
		for(int count = 0; count < v1.size(); count++) {
			// Ermittle den Typ ( (IPv4|IPv6)(Sender|Receiver) )
			// Fuege dementsprechend ein Kind Element hinzu
			final MulticastData.Typ typ = v1.get(count).getTyp();
			final Element mcdTyp = doc.createElement(typ.toString());
			multicasts.appendChild(mcdTyp);

			// Fuer alle vorhandenen Einstellungen
			for(final mcdTag tag : mcdTag.values()) {
				final Element mcdElement = doc.createElement(tag.toString());
				Text text = doc.createTextNode("");
				Integer converter = new Integer(0);

				switch(tag) {
					case active:
						final Boolean b = new Boolean(v1.get(count).isActive());
						text = doc.createTextNode(b.toString());
						break;
					case groupIp:
						if(v1.get(count).getGroupIp() != null) {
							text = doc.createTextNode(v1.get(count)
									.getGroupIp().getHostAddress());
						}
						break;
					case sourceIp:
						if(v1.get(count).getSourceIp() != null) {
							text = doc.createTextNode(v1.get(count)
									.getSourceIp().getHostAddress());
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
						text = doc.createTextNode(v1.get(count).getTyp()
								.toString());
						break;
					// [FH] V2: Added Support for L3
					case groupMac:
						if((typ == Typ.L2_RECEIVER) || (typ == Typ.L2_SENDER)) {
							text = doc.createTextNode(v1.get(count)
									.getMmrpGroupMacAsString());
						}
						break;

					case sourceMac:
						if((typ == Typ.L2_RECEIVER) || (typ == Typ.L2_SENDER)) {
							text = doc.createTextNode(v1.get(count)
									.getMmrpSourceMacAsString());
						}
						break;
					case protocol:
						text = doc.createTextNode(v1.get(count).getProtocol().toString());
						
				}
				mcdElement.appendChild(text);
				mcdTyp.appendChild(mcdElement);

			}
		}
		
	}

	/**
	 * Setup Transformer to create a template for the XML File, such as indent
	 * numbers and more
	 */
	private Transformer setupTransformer() {

		final TransformerFactory transFactory = TransformerFactory
				.newInstance();
		transFactory.setAttribute("indent-number", 4);
		Transformer transformer = null;
		try {
			transformer = transFactory.newTransformer();
		} catch(final TransformerConfigurationException e) {
			e.printStackTrace();
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		return transformer;
	}

	/**
	 * Custom Exception fuer ein leeres Config File
	 * 
	 * @param tag
	 * @param value
	 * @param multicast
	 * @throws WrongConfigurationException
	 */
	private void throwEmptyContentException(final String tag,
			final String value, final int multicast)
			throws WrongConfigurationException {
		final WrongConfigurationException ex = new WrongConfigurationException();
		ex.setErrorMessage("Error in configuration file: Value \"" + value
				+ "\" of " + tag + " in Multicast " + (multicast + 1)
				+ " is empty.");
		throw ex;
	}

	/**
	 * Custom Exceptions bei falschem Inhalt
	 * 
	 * @param tag
	 * @param value
	 * @param multicast
	 * @throws WrongConfigurationException
	 */
	private void throwWrongContentException(final String tag,
			final String value, final int multicast)
			throws WrongConfigurationException {
		final WrongConfigurationException ex = new WrongConfigurationException();
		ex.setErrorMessage("Error in configuration file: Value \"" + value
				+ "\" of " + tag + " in Multicast " + (multicast + 1)
				+ " is wrong.");
		throw ex;
	}

	/**
	 * Erstellt einen XML String anhand von einem Transformer Template
	 * 
	 * @param doc
	 *            Document (Pfad) zur XML Datei
	 * @param transformer
	 *            Transformer objekt (Template) fuer doe XML Datei
	 * @return
	 */
	private String XMLtoString(final Document doc, final Transformer transformer) {
		final StringWriter sWriter = new StringWriter();
		final StreamResult result = new StreamResult(sWriter);
		final DOMSource source = new DOMSource(doc);
		try {
			transformer.transform(source, result);
		} catch(final TransformerException e) {
			e.printStackTrace();
		}

		final String xmlString = sWriter.toString();

		return xmlString;
	}

}
