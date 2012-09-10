   
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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.view.FrameMain;
import dhbw.multicastor.program.view.PanelMulticastConfigNewButton;


public class WorkbenchHandler {

	private ViewController ctrl;
	
	
	public WorkbenchHandler(ViewController ctrl) {
		this.ctrl = ctrl;
	}
	
	public void setWindowSize(int width, int height){
		ctrl.getFrameMain().setSize(width, height);
	}
	
	public void setMode(String mode){
		if(mode.equals("sender")){
			ctrl.initializeSender();
			ctrl.getFrameMain().setTitle("Sender");
		}else{
			ctrl.initializeReceiver();
			ctrl.getFrameMain().setTitle("Receiver");
		}
	}
	
	public void setRB(ProtocolType type){
		PanelMulticastConfigNewButton buttons = ctrl.getFrameMain().getPanelMulticastconfigNewContext().getButton();
		if(type == ProtocolType.IGMP){
			buttons.getRbtn_IGMP().doClick(500);
		}else if(type == ProtocolType.MLD){
			buttons.getRbtn_MLD().doClick(500);
		}else if(type==ProtocolType.MMRP){
			buttons.getRbtn_MMRP().doClick(500);
		}
	}
	
	public void setGraphOrConsole(String gOc){
		if(gOc.equals("graph")){
			ctrl.getFrameMain().getTab_console().setSelectedIndex(0);
		}else{
			ctrl.getFrameMain().getTab_console().setSelectedIndex(1);
		}
	}
	
	public void setLocale(String language, String country){
		Locale.setDefault(new Locale(language, country));
	}
	
	public void setPosition(int posx, int posy){
		
		if(posx == -8 && posy == -8){
			ctrl.getFrameMain().setExtendedState(Frame.MAXIMIZED_BOTH);
		}
		ctrl.getFrameMain().setLocation(posx, posy);
	}
	
	public void setPaths(NodeList paths){
		if(paths != null){
			for(int i = 0; i<paths.getLength(); i++){ 
				Node path = paths.item(i);
				if(_checkFile(path.getTextContent())){
					XmlParserWorkbench.addFilePath(path.getTextContent());
				}
			}
		}
	}
	
	public Document buildXmlDocument(Document doc, File file){
		
		FrameMain frame = ctrl.getFrameMain();
		
		// workbench
		Element workbench = doc.createElement("workbench");
		
		// window-size
		Element window_size = doc.createElement("window-size");
		Dimension dim = frame.getSize();
		window_size.setAttribute("width", Integer.toString(dim.width));
		window_size.setAttribute("height", Integer.toString(dim.height));
		
		// mode
		Element mode = doc.createElement("mode");
		if(ctrl.getCurrentModus() == Modus.SENDER){
			mode.setTextContent("sender");
		}else{
			mode.setTextContent("receiver");
		}
		
		// active-rb
		Element active_rb = doc.createElement("active-rb");
		PanelMulticastConfigNewButton buttons = frame.getPanelMulticastconfigNewContext().getButton();
		if(buttons.getRbtn_IGMP().isSelected()){
			active_rb.setTextContent("IGMP");
		}else if(buttons.getRbtn_MLD().isSelected()){
			active_rb.setTextContent("MLD");
		}else{
			active_rb.setTextContent("MMRP");
		}
		
		// graph-console
		Element gOc = doc.createElement("graph-console");
		int index = ctrl.getFrameMain().getTab_console().getSelectedIndex();
		switch(index){
			// Tab Graph
			case 0: gOc.setTextContent("graph");
				break;
			// Tab Console
			case 1: gOc.setTextContent("console");
				break;
			default: gOc.setTextContent("graph");
				break;
		}
		
		// locale
		Locale loc = Locale.getDefault();
		Element locale = doc.createElement("locale");
		locale.setAttribute("language", loc.getLanguage());
		locale.setAttribute("country", loc.getCountry());
		
		// position
		Point point = ctrl.getFrameMain().getLocation();
		Element position = doc.createElement("position");
		position.setAttribute("posx", Integer.toString(point.x));
		position.setAttribute("posy", Integer.toString(point.y));
		
		// ConfigPaths
		Element configpaths = doc.createElement("config");
		ArrayList<String> lastconfigpath = XmlParserWorkbench.getLastConfigFiles();
		if(!lastconfigpath.isEmpty()){
			int count = 0;
			for(int i = 0; i<lastconfigpath.size(); i++){
				if(count<4 && _checkFile(lastconfigpath.get(i))){
					Element path = doc.createElement("path");
					path.setTextContent(lastconfigpath.get(i));
					configpaths.appendChild(path);
				}
			}
		}
		
		// Nodes hinzufuegen
		workbench.appendChild(window_size);
		workbench.appendChild(mode);
		workbench.appendChild(active_rb);
		workbench.appendChild(gOc);
		workbench.appendChild(locale);
		workbench.appendChild(position);
		workbench.appendChild(configpaths);
		doc.appendChild(workbench);
		
		return doc;
	}
	
	private boolean _checkFile(String filepath){
		File file = new File(filepath);
		return file.exists();
	}

	public void setModeBefore(String v_mode) {
		if(v_mode.equals("sender")){
			ctrl.changeModus(Modus.SENDER);
		}else{
			ctrl.changeModus(Modus.RECEIVER);
		}
	}
	
	public Modus getSavedModus(XmlParserWorkbench workbench){
		if(workbench.getV_mode().equals("sender")){
			return Modus.SENDER;
		}else{
			return Modus.RECEIVER;
		}
		
	}
	
}
