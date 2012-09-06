   
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
 
 package dhbw.multicastor.program.interfaces;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.xml.sax.SAXException;

import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.model.WrongConfigurationException;



public interface XMLParserInterface
{


	/** Auslesen einer XML-Konfigurationsdatei fï¿½r den User und aus der Kommandozeile
	 * @param pfad
	 * Ort, an dem die Konfigurationsdatei liegt
	 * @param v1
	 * MultiCastData enthï¿½lt Multicast Konfigurationseinstellungen
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadConfig( String pfad, Vector<MulticastData> v1) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException;
	

	/** Speichert die getï¿½tigten Konfigurationen in einer XML-Datei ab.
	 *  Fï¿½r Kommandozeile und manuelle Speicherung einer Konfigurationsdatei durch den Benutzer.
	 * @param pfad
	 * Der Ort, an dem die Datei gespeichert werden soll
	 * @param v1
	 * Vektor aus Multicast Konfigurationseinstellungen
	 * @throws IOException
	 * @throws WrongConfigurationException 
	 */
	public void saveConfig(String pfad, Vector<MulticastData> v1) throws IOException, WrongConfigurationException;

	/**
	 * Lï¿½dt alle DefaultMulticastwerte und speichert sie in der Klasse
	 * DefaultMulticastData
	 * Pfad der default XML-Datei: /dhbw/multicastor/resources/configuration/DefaultMulticastData.xml
	 * @param path 
	 * @throws WrongConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean loadDefaultMulticastData(String path) throws WrongConfigurationException,
	SAXException, IOException;
}
