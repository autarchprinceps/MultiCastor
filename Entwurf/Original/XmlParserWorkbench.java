   
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
 
  

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.interfaces.IgmpMldInterface;
import dhbw.multicastor.program.interfaces.MMRPInterface;


public class XmlParserWorkbench extends xmlParser{

	// Default-Werte
	private final static int DEF_WIDTH = 850;
	private final static int DEF_HEIGHT = 694;
	private final static int DEF_POSX = 100;
	private final static int DEF_POSY = 100;
	private final static String DEF_MODE = "sender";
	private final static String DEF_GoC = "graph";
	private final static String DEF_LANGUAGE = "en";
	private final static String DEF_COUNTRY = "US";
	private final static ProtocolType DEF_RB = ProtocolType.IGMP;
	
	// Handler, der die Angeben aus dem XML-File umsetzt.
	private WorkbenchHandler handler;
	private Document doc;
	
	// gelesene Werte
	private String v_country = DEF_COUNTRY;
	private String v_language = DEF_LANGUAGE;
	private String v_mode = DEF_MODE;
	private int v_width = DEF_WIDTH;
	private int v_height = DEF_HEIGHT;
	private int v_posx = DEF_POSX;
	private int v_posy = DEF_POSY;
	private ProtocolType v_rb = DEF_RB;
	private String v_GoC = DEF_GoC;
	private NodeList paths = null;
	
	// Zuletzt verwendete Konfigurationsdateien
	private static ArrayList<String> lastConfigFiles = new ArrayList<String>();
	
	public XmlParserWorkbench(Logger logger, ViewController ctrl) {
		super(logger);
		// WorkbenchHandler Instanz erstellen, Vermeidung von NullPointerException
		handler = new WorkbenchHandler(ctrl);
	}

	@Override
	protected boolean checkIgmpMld(IgmpMldInterface data) {
		return false;
	}

	@Override
	protected boolean checkNetworkInterface(IgmpMldInterface data) {
		return false;
	}

	@Override
	protected boolean checkNetworkInterface(MMRPInterface data) {
		return false;
	}

	@Override
	protected boolean checkMmrp(MMRPInterface data) {
		return false;
	}

	@Override
	protected String getErrorMessageIgmpMld(IgmpMldInterface data) {
		return null;
	}

	@Override
	protected String getErrorMessageMmrp(MMRPInterface data) {
		return null;
	}
	
	/**
	 * Aktuelle Stand der Oberfläche in einer XML-Datei abspeichern.
	 */
	public void saveWorkbench(String filepath){
		File xmlFile = new File(filepath);
		
		DocumentBuilder builder;
		DocumentBuilderFactory factory;
		DOMImplementation impl;
		Document doc;
		
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			impl = builder.getDOMImplementation();
			doc = impl.createDocument(null, null, null);
			
			doc = handler.buildXmlDocument(doc, xmlFile);
			
			// Transformer erstellen
			Transformer transformer = setupTransformer();
			
			// Erstelle einen String aus dem XML Baum
			String xmlString = XMLtoString(doc, transformer);
			
			if (!xmlFile.exists()) {
				xmlFile.createNewFile();
			}
			BufferedWriter writer = null;
			writer = new BufferedWriter(new FileWriter(xmlFile));
			String prolog = "<?xml version=\"1.0\"?>" + System.getProperty("line.separator");
			writer.write(prolog + xmlString);
			writer.close();
			
//			System.out.println("Here's the xml:\n\n" + prolog + xmlString);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadWorkbench(String filepath){
		
		try {
			doc = parseDocument(filepath);
			NodeList workbench = doc.getElementsByTagName("workbench");
			
			// Validierung der XML Nodes
			if(_validateNodeLength(workbench, "workbench")){
				_validateWindowSize(doc.getElementsByTagName("window-size"));
				_validateMode(doc.getElementsByTagName("mode"));
				_validateActiveRB(doc.getElementsByTagName("active-rb"));
				_validateGraphOrConsole(doc.getElementsByTagName("graph-console"));
				_validateLocale(doc.getElementsByTagName("locale"));
				_validatePosition(doc.getElementsByTagName("position"));
				_validatePaths(doc.getElementsByTagName("config"));
			}
			
		} catch (Exception e) {
			_loggingDefaultLoad("workbench");
		}
	} 
	
	private void _validatePaths(NodeList configpaths) {
		if(_validateNodeLength(configpaths, "config")){
			XPath xpath = factory.newXPath();
			XPathExpression expr;
			try {
				expr = xpath.compile("//workbench/config/path");
				paths = (NodeList)  expr.evaluate(doc, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Validierung des window-size Nodes und setzen der WindowSize.<br>
	 * Im Fehlerfall werden die Default-Werte gesetzt.
	 * @param windowSize
	 * 				NodeList mit einem Element, ansonsten wird Fehler geloggt<br>
	 * 				und der Default Wert wird verwendet.
	 */
	private void _validateWindowSize(NodeList windowSize){
			
		// Prüfen ob Länge == 1
		if(_validateNodeLength(windowSize, "window-size")){
			Node node = windowSize.item(0);
			NamedNodeMap attributes = node.getAttributes();
			Node n_width  = attributes.getNamedItem("width");
			Node n_height = attributes.getNamedItem("height");
			if(n_width != null && n_height != null){
				try{
					if( (Integer.valueOf(n_width.getTextContent()) >= 850) &&
							(Integer.valueOf(n_height.getTextContent()) >= 694)){
						v_width = Integer.valueOf(n_width.getTextContent());
						v_height = Integer.valueOf(n_height.getTextContent());
					}
				}catch(NumberFormatException e){
					_loggingDefaultLoad("window-size");
				}
			}else{
				_loggingDefaultLoad("window-size");
			}
		}
		
	}
	
	private void _validateLocale(NodeList locale){
		
		if(_validateNodeLength(locale, "locale")){
			Node node = locale.item(0);
			NamedNodeMap attributes = node.getAttributes();
			Node n_language = attributes.getNamedItem("language");
			Node n_country = attributes.getNamedItem("country");
			if(n_language.getTextContent().equals("de") || n_language.getTextContent().equals("en")){
				v_language = n_language.getTextContent();
			}else{
				_loggingDefaultLoad("locale");
			}
		
			if(n_country.getTextContent().equals("DE") || n_country.getTextContent().equals("US")){
				v_country = n_country.getTextContent();
			}else{
				_loggingDefaultLoad("locale");
			}
		}
	}
	
	private void _validatePosition(NodeList position){
		
		if(_validateNodeLength(position, "position")){
			Node node = position.item(0);
			NamedNodeMap attributes = node.getAttributes();
			Node n_posx = attributes.getNamedItem("posx");
			Node n_posy = attributes.getNamedItem("posy");
			try{
				if(Integer.valueOf(n_posx.getTextContent()) >= 0 || 
						Integer.valueOf(n_posx.getTextContent()) == -8){
					v_posx = Integer.valueOf(n_posx.getTextContent());
				}else{
					_loggingDefaultLoad("position");
				}
			
				if(Integer.valueOf(n_posy.getTextContent()) >= 0 ||
						Integer.valueOf(n_posy.getTextContent()) == -8){
					v_posy = Integer.valueOf(n_posy.getTextContent());
				}else{
					_loggingDefaultLoad("position");
				}
			}catch(NumberFormatException e){
				_loggingDefaultLoad("position");
			}
		}
	}
	
	
	private void _validateMode(NodeList mode){
		
		if(_validateNodeLength(mode, "mode")){
			Node n_mode = mode.item(0);
			String content = n_mode.getTextContent();
			if(content.equals("sender") || content.equals("receiver")){
				v_mode = content;
			}else{
				_loggingDefaultLoad("mode");
			}
		}
	}
	
	private void _validateActiveRB(NodeList activeRB){
		
		if(_validateNodeLength(activeRB, "active-rb")){
			Node n_rb = activeRB.item(0);
			String content = n_rb.getTextContent();
			if(content.equals("IGMP") || content.equals("igmp")){
				v_rb = ProtocolType.IGMP;
			}else if(content.equals("MLD") || content.equals("mld")){
				v_rb = ProtocolType.MLD;
			}else if(content.equals("MMRP") || content.equals("mmrp")){
				v_rb = ProtocolType.MMRP;
			}else{
				_loggingDefaultLoad("active-rb");
			}
		}
	}
	
	private void _validateGraphOrConsole(NodeList graphOrConsole){
		
		if(_validateNodeLength(graphOrConsole, "graph-console")){
			Node n_Goc = graphOrConsole.item(0);
			String content = n_Goc.getTextContent();
			if(content.equals("graph") || content.equals("console")){
				v_GoC = content;
			}else{
				_loggingDefaultLoad("graph-console");
			}
		}
	}
	
	/**
	 * Pruefen, ob die Nodelaenge == 1, ansonsten wird eine Warning geloggt.
	 * @param node
	 * @return Boolean (true wenn Laenge des Nodes == 1, ansonsten false)
	 */
	private boolean _validateNodeLength(NodeList node, String node_name){
		if(node.getLength() == 1){
			return true;
		}
		_loggingDefaultLoad(node_name);
		return false;
	}
	
	private void _loggingDefaultLoad(String node){
		logger.warning("The node " + node + " is corrupt. The default configuration is loaded.");
	}

	public void setWorkbenchBefore(){
		// Locale setzen
		handler.setLocale(v_language, v_country);
		// ConfigPaths setzen
		handler.setPaths(paths);
		handler.setModeBefore(v_mode);
	}
	
	public void setWorkbenchAfter(){
//		handler.setMode(v_mode);
		// Position des JFrame setzen
		handler.setPosition(v_posx, v_posy);
		// Window-Size setzen
		handler.setWindowSize(v_width, v_height);
//		// Mode setzen
		
//		// Graph or Console setzen
		handler.setGraphOrConsole(v_GoC);
//		// RB setzen
//		handler.setRB(v_rb);
	}
	
	public static void addFilePath(String filepath){
		if(!lastConfigFiles.contains(filepath)){
				lastConfigFiles.add(0, filepath);
		}
	}

	public static ArrayList<String> getLastConfigFiles() {
		return lastConfigFiles;
	}

	// TODO Getter fuer Tests -> bei Auslieferung entfernen
	
	public String getV_country() {
		return v_country;
	}

	public String getV_language() {
		return v_language;
	}

	public String getV_mode() {
		return v_mode;
	}

	public int getV_width() {
		return v_width;
	}

	public int getV_height() {
		return v_height;
	}

	public int getV_posx() {
		return v_posx;
	}

	public int getV_posy() {
		return v_posy;
	}

	public ProtocolType getV_rb() {
		return v_rb;
	}

	public String getV_GoC() {
		return v_GoC;
	}

	public void setRB() {
		handler.setRB(v_rb);
	}
	
	
	
}
