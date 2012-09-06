   
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;



public class MulticastReceiverLayer3 extends MulticastReceiver {
	/** Javasocket fuer Multicasts. */
	private MulticastSocket multicastSocket;
	


	/**
	 * Ein MulticastReceiver entspricht genau einem MulticastData-Objekt vom Receiver Typ. Der MulticastReceiver ist
	 * fuer das Beitreten und Verlassen der MulticastGruppen zustaending. Er kann gestartet und gestoppt werden.
	 * @param m MulticastData-Objekt mit GroupIP und Port Informationen. Alle ermittelten Informationen werden hier rein geschrieben.
	 * @param logger Wird genutzt um Status- und Fehlermeldungen auszugeben. Diese werden in die MessageQueue geschrieben.
	 */
	public MulticastReceiverLayer3(MulticastData m, Logger logger){
		super(m,logger);
		// Erzeuge Socket mit Port; nutze Standardport falls eingegebener Port nicht funktioniert.
		try {
			
				multicastSocket = new MulticastSocket(((IgmpMldData)m).getUdpPort());
			
			// Der Empfaenger nutzt alle verfuegbaren Netzwerkschnittstellen
			//	multicastSocket.setInterface(mcData.getSourceIp());
		}catch(IOException e){
			logger.log(Level.WARNING, "'" + e.getMessage() + "' Versuche default Port 4711 zu setzen...", Event.ERROR);
			try{
				int udpPort = 4711;
				
					((IgmpMldData)mcData).setUdpPort(udpPort);
				
				multicastSocket = new MulticastSocket(udpPort);
			}catch(IOException e2){
				logger.log(Level.WARNING, e.getMessage(), Event.WARNING);
				return;
			}
		}	
		packetAnalyzer = new PacketAnalyzer(mcData, logger);
		// resets MulticastData Object to avoid default value -1
		mcData.resetValues();
	}
	
	/**
	 * Der MulticastReciever tritt der, im MulticastData-Objekt bei der Initialisierung angegebenen, Multicastgruppe bei.
	 * @return Gibt <code>true</code> zurueck, wenn der Multicastgruppe nicht beigetreten werden konnte.
	 */
	public boolean joinGroup() {
		// ********************************************
		// Join MultiCast-Group
		// ********************************************
		try {
			multicastSocket.joinGroup(((IgmpMldData) mcData).getGroupIp());

			logger.log(Level.INFO, mcData.identify()
					+ ": Started Receiver with MC Object.", Event.JOINED);
		} catch (IOException e1) {
			// Setzt Aktivwerte wieder auf false und gibt eine Fehlermeldung aus
			mcData.setActive(false);
			active = false;
			logger.log(Level.WARNING, mcData.identify()
					+ ":Could not start Receiver." + e1.getMessage(), Event.WARNING);
			return true;
		}
		return false;
	}
	
	/**
	 * Veranlasst den MulticastReceiver die Multicastgruppe zu verlassen.
	 */
	private void leaveGroup(){
		//********************************************
		// Leave MultiCast-Group
		//********************************************
		try {
			multicastSocket.leaveGroup(((IgmpMldData) mcData).getGroupIp());
			logger.log(Level.INFO, mcData.identify()
					+ ": Left Multicastgroup of MC Object " + ((IgmpMldData) mcData).getPacketCount() + " packets received", Event.LEAVE);
		} catch (IOException e) {
			logger.log(Level.WARNING, mcData.identify()
					+ ": Could not leave MC group of MC Object.", Event.WARNING);
		}
	}
	
	/**
	 * Wartet auf einkommende Pakete bis der MulticastReceiver ueber setActive(false) deaktiviert wird.
	 * Empfangene Pakete werden an den PacketAnalyzer gegeben und dort analysiert.
	 */
	@Override
	public void run() {
		/*
		 * JoinGroup wurde in den MulticastController verlagert, da
		 * es hier oefters zu Problemen kam, wenn ein Receiver sehr 
		 * oft und schnell hinter einander gestartet und gestoppt 
		 * wurde.
		 */
		
		// Initialisiert den Buffer mit Einsen
		initializeBuf();
		
		// local variables
		DatagramPacket datagram = new DatagramPacket(buf, length);

		while(active){
			try {
				multicastSocket.setSoTimeout(1000);
				multicastSocket.receive(datagram);
				packetAnalyzer.setTimeout(false);
				//********************************************
				// Analyse received packet
				//********************************************
				packetAnalyzer.analyzePacket(datagram.getData());
				if(mcData.getReceivedPackSource() == null){
					mcData.setReceivedPackSource(datagram.getAddress());
				}
			} catch (IOException e) {
				if(e instanceof SocketTimeoutException){
					// bei Timeout erfaehrt es der PacketAnalyzer wegen Interrupts
					packetAnalyzer.setTimeout(true);
				} else {
					logger.log(Level.WARNING, "Error while receiving packets: " + e.getMessage(), Event.WARNING);
				}
			}
		}
		// Verlaesst die Multicast Gruppe
		leaveGroup();
		// Resetted gemessene Werte (SenderID bleibt erhalten, wegen des Wiedererkennungswertes)
		packetAnalyzer.resetValues();
		packetAnalyzer.update();
		packetAnalyzer.updateMin();
		// Thread ist beendet
		setStillRunning(false);
	}



	/**
	 * Setzt den InternenPacketCount
	 * @param packetCount Wert auf den der interne PacketCount gesetzt wird
	 */
/*	public void setInternerPacketCount(int packetCount){
		packetAnalyzer.setInternerPacketCount(packetCount);
	}*/

}
