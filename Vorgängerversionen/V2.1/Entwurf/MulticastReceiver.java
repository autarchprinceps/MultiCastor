   
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
 
  

import java.util.logging.Level;
import java.util.logging.Logger;

import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.interfaces.MulticastThreadSuper;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;


public abstract class MulticastReceiver extends MulticastThreadSuper {
	
	/** Wenn auf wahr, lauscht dieser Receiver auf ankommende Pakete. */
	protected boolean active = false;
	/** Wird fuer die Fehlerausgabe verwendet. */
	protected Logger logger;
	protected PacketAnalyzer packetAnalyzer;
	/** Maximale Paketlaenge */
	protected final int length = 65575;
	/** Byte Array in dem das Paket gespeichert wird. */
	protected byte[] buf = new byte[length];
	
	
	public MulticastReceiver(MulticastData multicastData) {
		super(multicastData);
		// TODO Auto-generated constructor stub
	}
	
	public MulticastReceiver(MulticastData m, Logger logger){
		this(m);
		if(logger==null){
			System.out.println("FATAL ERROR - cannot create MulticastReceiver without Logger");
			return;
		}
		if(m==null){
			logger.log(Level.WARNING, "Error while creating MulticastReceiver. MulticastData cannot be empty.", Event.WARNING);
			return;
		}
		this.logger = logger;
	}
	
	public abstract boolean joinGroup();
	
	/**
	 * Setzt alle Vorraussetzungen damit der MulticastReceiver mit einem Thread gestartet werden kann.
	 * Hierbei muss der MulticastController sicherstellen, dass kein weiterer Thread mit diesem Objekt laeuft.
	 */
	@Override
	public void setActive(boolean b) {
			if(b){
				setStillRunning(true);
				// Verhindert eine "recentlyChanged"-Markierung direkt nach dem Starten
				packetAnalyzer.setLastActivated(6);
				mcData.setReceivedPackSource(null);
			}
			active = b;
			mcData.setActive(b);
		// Values will be resetted when Receiver actually stops. Just before calling update functions in PacketAnalyzer
		//	packetAnalyzer.resetValues();
		}		


	/**
	 * Updated die Durchschnittswerte im MulticastData-Objekt
	 */
	@Override
	public void updateMin() {
		packetAnalyzer.updateMin();
	}


	/**
	 * Updated die Werte im MulticastData-Objekt
	 */
	@Override
	public void update() {
		packetAnalyzer.update();
		// damit die Paketlaenge beim naechsten Paket erneut bestimmt werden kann
		//		groessere Pakete fallen immer auf, jedoch sind kleinere Pakete nur so erkennbar
		initializeBuf();	
	}
	
	/**
	 * Schreibt lauter Einsen in den Buffer.
	 */
	protected void initializeBuf(){
		for(int i = 0; i<buf.length; i++){
			buf[i] = 1;
		}
	}
	
	public void reset(){
		packetAnalyzer.reset();
	}
	
	/**
	 * Gibt eine Referenz auf das MulticastData-Objekt zurueck
	 * @return MulticastData-Objekt mit dem dieser MulticastReceiver erstellt wurde
	 */
	@Override
	public MulticastData getMultiCastData() {
		return mcData;
	}
}
