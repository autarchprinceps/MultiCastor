   
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
 
  

import dhbw.multicastor.program.model.ByteTools;

/**
 * Repraesentiert ein VectorAttribute-Feld eines MMRPD-Units.
 * Stellt Methoden zur Dekodierung der einzelnen Events zur Verfuegung.
 *
 */
public class Attribute {
	
	/** FirstValue-Feld eines MMRPD-Units **/
	private byte[] firstValue;
	
	/** Kodierte Events eines MMRPD-Units **/
	private byte[] unpackedEvents;
	
	/** LeaveAll-Feld eines MMRPD-Units **/
	private int leaveAll;
	
	/** NumberOfEvents-Feld eines MMRPD-Units **/
	private int numOfEvents;
	
	/** FirstValue-Feld eines MMRPD-Units (als long) **/
	private long fV;

	public Attribute(byte[] pFirstValue, byte[] pEvents, int pLeaveAll, int pNumOfEvents) {
		this.firstValue = pFirstValue;
		this.unpackedEvents = pEvents;
		this.leaveAll = pLeaveAll;
		this.numOfEvents = pNumOfEvents;
		if (pFirstValue.length == 6) {
			this.fV = ByteTools.macToLong(pFirstValue);
		} else {
			fV = pFirstValue[0];
		}
	}

	public byte[] getFirstValue() {
		return firstValue;
	}
	
	public long getfV(){
		return this.fV;
	}

	public byte[] getEvents() {
		return unpackedEvents;
	}

	public int getLeaveAll() {
		return leaveAll;
	}
	
	public int getNumOfEvents() {
		return numOfEvents;
	}
	
	/**
	 * Holt einen Event unter der angegebenen Position.
	 * Falls die angegebene Position zu gross ist, wird -1 zurueckgeliefert.
	 * 
	 * @param l Nummer des Events
	 * @return den Wert des Events
	 */
	public int getEventN(long l) {
		if (l >= numOfEvents) {
			return -1;
		}
		int three = unpackedEvents[(int) l / 3] & 0xFF;
		l = l % 3;
		int value = three / 36;
		switch ((int)l) {
		case 0: {
			break;
		}
		case 1: {
			three -= (36 * value);
			value = three / 6;
			break;
		}
		default: {
			three -= (36 * value);
			value = three / 6;
			value = three - (6 * value);
		}
		}
		return value;
	}
	
	/**
	 * Holt einen Event fuer die angegebene Adresse.
	 * Falls kein Event fuer diese Adresse vorliegt, wird -1 zurueckgeliefert.
	 * 
	 * @param address MAC-Adress fuer ein Event
	 * @return den Wert des Events
	 */
	public synchronized int getMyEvent(long address){
		if(fV <= address){
			return getEventN(address - fV);
		}
		return -1;
	}

}
