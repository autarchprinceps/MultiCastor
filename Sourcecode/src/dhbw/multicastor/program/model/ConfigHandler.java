   
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import dhbw.multicastor.program.controller.MulticastController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.controller.ViewController.UpdateTyp;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.interfaces.XMLParserInterface;


public class ConfigHandler {

	private XMLParserInterface xml_parserSender = null;
	private XMLParserInterface xml_parserReceiver = null;
	private Logger logger = null;

	public ConfigHandler(Logger logger) {
		this.logger = logger;
		xml_parserSender = new xmlParserSender(this.logger);
		xml_parserReceiver = new xmlParserReceiver(this.logger);
	}

	public void loadConfig(MulticastController mcCtrl) {
		this.loadConfig("", true, mcCtrl);
	}

	public void loadDefaultMulticastData() {

		try {
			File defaulValueFile = new File("DefaultMulticastData.xml");
			if (!defaulValueFile.exists()) {
				extractDefaultValues();
			}
			if (!xml_parserSender
					.loadDefaultMulticastData("DefaultMulticastData.xml")) {
				extractDefaultValues();
				xml_parserSender
						.loadDefaultMulticastData("DefaultMulticastData.xml");
			}
		} catch (WrongConfigurationException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
	}

	private void extractDefaultValues() throws FileNotFoundException,
			IOException {
		BufferedInputStream input = new BufferedInputStream(
				this.getClass()
						.getClassLoader()
						.getResourceAsStream(
								"dhbw/multicastor/resources/configuration/DefaultMulticastData.xml"));
		FileOutputStream out = new FileOutputStream("DefaultMulticastData.xml");
		int i;
		while ((i = input.read()) >= 0) {
			out.write(i);
		}
		out.close();
		input.close();
	}

	public void loadConfig(String path, boolean defaultFile,
			MulticastController mcCtrl) {
		final String p = "MultiCastor.xml";
		// Diese Vektoren werden geladen
		Vector<MulticastData> multicasts = new Vector<MulticastData>();
		String message = new String();
		boolean skip = false;
		if (defaultFile) {
			try {
				if (mcCtrl.getCurrentModus().equals(Modus.SENDER)) {
					xml_parserSender.loadConfig(p, multicasts);
				} else {
					xml_parserReceiver.loadConfig(p, multicasts);
				}
				logger.log(Level.INFO, "Configuration has been loaded");
			} catch (Exception e) {
				if (e instanceof FileNotFoundException) {
					message = "Default configurationfile was not found. MultiCastor starts without preconfigured Multicasts and with default GUI configuration.";
				} else if (e instanceof SAXException) {
					message = "Default configurationfile could not be parsed correctly. MultiCastor starts without preconfigured Multicasts and with default GUI configuration.";
				} else if (e instanceof IOException) {
					message = "Default configurationfile could not be loaded. MultiCastor starts without preconfigured Multicasts and with default GUI configuration.";
				} else if (e instanceof WrongConfigurationException) {
					message = ((WrongConfigurationException) e)
							.getErrorMessage();
				} else if (e instanceof IllegalArgumentException) {
					message = "Error in default configurationfile.";
				} else {
					message = "Unexpected error of type: " + e.getClass();
				}
				skip = true;
				logger.log(Level.WARNING, message);
			}
		} else {
			try {
				if (mcCtrl.getCurrentModus().equals(Modus.SENDER)) {
					xml_parserSender.loadConfig(path, multicasts);
				} else {
					xml_parserReceiver.loadConfig(path, multicasts);
				}
				logger.log(Level.INFO, "Configurationfile loaded: " + path);
			} catch (Exception e) {
				if (e instanceof FileNotFoundException) {
					message = "Configurationfile not found.";
				} else if (e instanceof SAXException) {
					message = "Configurationfile could not be parsed.";
				} else if (e instanceof IOException) {
					message = "Configurationfile could not be loaded.";
				} else if (e instanceof WrongConfigurationException) {
					message = ((WrongConfigurationException) e)
							.getErrorMessage();
				} else if (e instanceof IllegalArgumentException) {
					message = "Error in configurationfile.";
				} else {
					message = "Unexpected error of type: " + e.getClass();
				}
				skip = true;
				message = message + " Used path: " + path;
				logger.log(Level.WARNING, message);
			}
		}
		if (!skip) {
			// Fï¿½ge Multicast hinzu
			for (MulticastData m : multicasts) {
				/**
				 * hier war eine Ueberpruefung, ob die jeweiligen CheckBoxen
				 * beim Laden ausgewaehlt sind. diese Checkboxen werden nicht
				 * mehr benietigt, daher wurde die Ueberpreufeung geloescht
				 */
				mcCtrl.addMC(m);

				if (mcCtrl.getView_controller() != null) {
					mcCtrl.getView_controller().updateTable(UpdateTyp.INSERT);
				}
			}
		}
		if (mcCtrl.getView_controller() != null) {
//			mcCtrl.getView_controller().loadAutoSave();
		}
	}

	public void loadConfigWithoutGUI(String path, MulticastController mcCtrl)
			throws FileNotFoundException, SAXException, IOException,
			WrongConfigurationException {
		Vector<MulticastData> v = new Vector<MulticastData>();
		if (mcCtrl.getCurrentModus().equals(Modus.SENDER)) {
			xml_parserSender.loadConfig(path, v);
		} else {
			xml_parserReceiver.loadConfig(path, v);
		}
		if (v != null) {
			for (MulticastData m : v) {
				// System.out.println("Found Multicast: " + m);
				mcCtrl.addMC(m); // hier vllt. nur adden wenn man auch starten
									// will, da das leider nicht mehr geht
				/*
				 * if(m.isActive()){ // Startet auf aktiv gesetzte Multicasts
				 * aus der Konfigurationsdatei //
				 * System.out.println("Started Multicast: " + m); startMC(m); }
				 */
			}
		}
	}

	public void saveConfig(String path, boolean complete,
			Vector<MulticastData> v) {
		final String p = "MultiCastor.xml";

		try { // Uebergibt den Vektor mit allen Multicasts an den XMLParser
			if (complete) {
				// hier ist egal wer speichert, da das Speichern bei beiden
				// parser gleich ist
				xml_parserSender.saveConfig(p, v);
				// logger.log(Level.INFO, "Saved default Configfile.");
			} else {
				// hier ist egal wer speichert, da das Speichern bei beiden
				// parser gleich ist
				xml_parserSender.saveConfig(path, v);
				addLastConfigs(path);
				logger.log(Level.INFO, "Saved Configfile.");
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Could not save default Configurationfile.");
		}
	}

	private void addLastConfigs(String path) {
		// TODO schauen was das macht
		// if(lastConfigs.size()<3){
		// lastConfigs.add(0,path);
		// } else {
		// lastConfigs.remove(2);
		// lastConfigs.add(0,path);
		// }
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
